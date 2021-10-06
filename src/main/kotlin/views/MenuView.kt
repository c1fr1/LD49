package views

import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.EnigWindow
import engine.opengl.Font
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min

abstract class MenuView(w : EnigWindow) : EnigView() {

	val window = w

	var cam = Camera2D(w, 2f)

	var time = Math.random().toFloat() * 10000f

	lateinit var buttonShader : ShaderProgram
	lateinit var textShader : ShaderProgram

	lateinit var woodTex : Texture
	lateinit var font : Font
	lateinit var vao : VAO

	override fun generateResources(window: EnigWindow) {
		super.generateResources(window)

		textShader = ShaderProgram("textShader")
		buttonShader = ShaderProgram("buttonShader")

		woodTex = Texture("tiles/woodTex.png")

		font = Font("Inkfree.ttf", 128f, 1024, 512)

		vao = VAO(0f, 0f, 1f, 1f)
	}

	fun renderButton(button : Button, dtime : Float) {
		button.updateStrength(window, dtime)
		renderButtonBackground(button)
		renderButtonText(button)
	}

	fun renderButtonBackground(button : Button) {
		buttonShader.enable()
		woodTex.bind()
		buttonShader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix()
			.translate(0.1f-window.aspectRatio, button.y, 0f)
			.scale(button.width * 0.2f, 0.2f, 1f)
		buttonShader[ShaderType.FRAGMENT_SHADER, 0] = button.strength
		buttonShader[ShaderType.FRAGMENT_SHADER, 1] = time * 2
		buttonShader[ShaderType.FRAGMENT_SHADER, 2] = button.width
		buttonShader[ShaderType.FRAGMENT_SHADER, 3] = button.y
		vao.fullRender()
	}

	fun renderButtonText(button : Button) {
		textShader.enable()
		vao.prepareRender()
		font.bind()
		font.getMats(button.text, cam.getMatrix()
			.translate(0.15f - window.aspectRatio, button.y + 0.05f, 0f)
			.scale(0.2f)) {wm, tm ->
			for (i in wm.indices) {
				textShader[ShaderType.VERTEX_SHADER, 0] = wm[i]
				textShader[ShaderType.VERTEX_SHADER, 1] = tm[i]
				textShader[ShaderType.FRAGMENT_SHADER, 0] = Vector3f(1f, 1f, 1f)
				vao.drawTriangles()
			}
		}
		vao.unbind()
	}
}

class Button(val y : Float, val width : Float, var text : String) {
	var strength = 0.93f

	fun updateStrength(window : EnigWindow, dtime : Float) {
		if (hovering(window)) {
			strength = max(strength - dtime / 2f, 0.8f)
		} else {
			strength = min(strength + dtime / 2f, 0.93f)
		}
	}

	fun hovering(window : EnigWindow) : Boolean {
		val xRange = (0.1f - window.aspectRatio)..(0.1f - window.aspectRatio + width / 5f)
		val yRange = y..(y + 0.2f)
		return window.inputHandler.glCursorX * window.aspectRatio in xRange && -window.inputHandler.glCursorY in yRange
	}
}
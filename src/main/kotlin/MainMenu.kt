import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.*
import engine.opengl.bufferObjects.FBO
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import java.nio.file.Paths
import kotlin.math.max
import kotlin.math.min
import org.joml.Vector3f

class MainMenu(w : EnigWindow) : EnigView() {

	val window = w

	var cam = Camera2D(w, 2f)

	var time = 0f

	var nextView = 0

	val playButton = Button(3f, "PLAY")

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

		font = Font(Paths.get({}.javaClass.classLoader.getResource("Inkfree.ttf")!!.toURI()), 128f, 1024, 512)

		vao = VAO(0f, 0f, 1f, 1f)
	}

	var buttonStrength = 0.9f

	override fun loop(frameBirth: Long, dtime: Float): Boolean {
		FBO.prepareDefaultRender()

		playButton.updateStrength(window.inputHandler, dtime)

		renderButtonBackground(playButton)
		renderButtonText(playButton)

		time += dtime
		nextView = -1
		return window.inputHandler.keys[GLFW_KEY_ESCAPE] == KeyState.Released
	}

	fun renderButtonBackground(button : Button) {
		buttonShader.enable()
		woodTex.bind()
		buttonShader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix()
			.translate(0.1f-window.aspectRatio, 0f, 0f)
			.scale(button.width  * 0.2f, 0.2f, 1f)
		buttonShader[ShaderType.FRAGMENT_SHADER, 0] = button.strength
		buttonShader[ShaderType.FRAGMENT_SHADER, 1] = time * 2
		buttonShader[ShaderType.FRAGMENT_SHADER, 2] = button.width
		vao.fullRender()
	}

	fun renderButtonText(button : Button) {
		textShader.enable()
		vao.prepareRender()
		font.bind()
		font.getMats(button.text, cam.getMatrix()
			.translate(0.15f - window.aspectRatio, 0.05f, 0f)
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

class Button(val width : Float, val text : String) {
	var strength = 0.93f

	fun updateStrength(input : InputHandler, dtime : Float) {
		if (input.glCursorX > 0f) {
			strength = max(strength - dtime / 2f, 0.8f)
		} else {
			strength = min(strength + dtime / 2f, 0.93f)
		}
	}
}
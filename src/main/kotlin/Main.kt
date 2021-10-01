import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.*
import engine.opengl.bufferObjects.*
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

fun main() {
	EnigContext.init()
	val window = EnigWindow("enignets demo", GLContextPreset.standard2D)
	val view = Main(window)
	view.runInGLSafe(window)

	EnigContext.terminate()
}

class Main(w : EnigWindow) : EnigView() {

	val input = w.inputHandler

	val cam = Camera2D(w)

	lateinit var tileVAO : VAO

	override fun generateResources(window: EnigWindow) {

		tileVAO = VAO(0f, 0f, 1f, 1f)

		super.generateResources(window)
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Pressed
	}

	fun renderTiles() {
		tileVAO.prepareRender()
		for (x in -10..10) {
			for (y in -10..10) {
				cam.getMatrix().scale(10f).translate(x.toFloat(), y.toFloat(), 0f)
				tileVAO.drawTriangles()
			}
		}
		tileVAO.unbind()
	}
}
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
	private val NUM_TILES_PER_ROW = 20

	override fun generateResources(window: EnigWindow) {
		val tileVAOPositions = FloatArray(VBO.squareTC.size * NUM_TILES_PER_ROW)
		super.generateResources(window)
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Pressed
	}
}
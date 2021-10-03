import engine.EnigView
import engine.opengl.EnigWindow
import engine.opengl.Font
import engine.opengl.KeyState
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import java.nio.file.Paths

class MainMenu(w : EnigWindow) : EnigView() {

	val input = w.inputHandler

	var time = 0f

	lateinit var buttonShader : ShaderProgram
	lateinit var textShader : ShaderProgram
	lateinit var font : Font
	lateinit var vao : VAO

	override fun generateResources(window: EnigWindow) {
		super.generateResources(window)

		textShader = ShaderProgram("textShader")

		font = Font(Paths.get({}.javaClass.classLoader.getResource("Inkfree.ttf")!!.toURI()), 128f, 1024, 512)
	}

	override fun loop(frameBirth: Long, dtime: Float): Boolean {
		time += dtime
		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Released
	}
}
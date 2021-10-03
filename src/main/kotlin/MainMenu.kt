import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.EnigWindow
import engine.opengl.Font
import engine.opengl.KeyState
import engine.opengl.Texture
import engine.opengl.bufferObjects.FBO
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import java.nio.file.Paths

class MainMenu(w : EnigWindow) : EnigView() {

	val window = w

	var cam = Camera2D(w)

	var time = 0f

	var nextView = 0

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

	override fun loop(frameBirth: Long, dtime: Float): Boolean {
		FBO.prepareDefaultRender()

		buttonShader.enable()
		woodTex.bind()
		buttonShader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix().scale(50f)
		buttonShader[ShaderType.FRAGMENT_SHADER, 0] = 0.9f
		buttonShader[ShaderType.FRAGMENT_SHADER, 1] = time
		buttonShader[ShaderType.FRAGMENT_SHADER, 2] = 1f
		vao.fullRender()

		time += dtime
		nextView = -1
		return window.inputHandler.keys[GLFW_KEY_ESCAPE] == KeyState.Released
	}
}
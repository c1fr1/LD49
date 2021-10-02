import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.*
import engine.opengl.bufferObjects.*
import engine.opengl.jomlExtensions.Vector3f
import engine.opengl.jomlExtensions.unaryMinus
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
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

	val player = Player(w)

	var world = World()

	lateinit var squareVAO : VAO

	override fun generateResources(window: EnigWindow) {
		super.generateResources(window)

		squareVAO = VAO(-1f, -1f, 2f, 2f)

		world.generateResources()
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		FBO.prepareDefaultRender()

		player.updatePlayerPosition(dtime, input)

		world.renderTiles(player)
		renderPlayer() //TODO add more interesting player

		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Pressed
	}

	fun renderPlayer() {
		world.tileShader[ShaderType.VERTEX_SHADER, 0] = player.getMatrix().translate(player.x, player.y, 0f).scale(2f)
		squareVAO.fullRender()
	}
}
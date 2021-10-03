import engine.EnigView
import engine.opengl.*
import engine.opengl.bufferObjects.*
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

fun main() {
	EnigContext.init()
	val window = EnigWindow("LD49", GLContextPreset.standard2D)
	val view = Main(window)
	view.runInGLSafe(window)

	EnigContext.terminate()
}

class Main(w : EnigWindow) : EnigView() {

	val input = w.inputHandler

	val aspect = w.aspectRatio

	val player = Player(w)

	var world = World()

	lateinit var squareVAO : VAO
	lateinit var hpShader : ShaderProgram
	lateinit var texShader : ShaderProgram
	lateinit var font : Font

	override fun generateResources(window: EnigWindow) {
		super.generateResources(window)

		squareVAO = VAO(-1f, -1f, 2f, 2f)
		hpShader = ShaderProgram("hpShader")
		texShader = ShaderProgram("textureShader")

		world.generateResources()
		player.generateResources()
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		FBO.prepareDefaultRender()


		player.updatePlayerPosition(dtime, input, world, aspect, world.time)
		world.update(dtime, player)

		world.render(player, squareVAO, texShader)
		player.render(squareVAO, texShader)
		renderHPBar()

		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Pressed
	}

	fun renderHPBar() {
		hpShader.enable()
		squareVAO.prepareRender()
		hpShader[ShaderType.VERTEX_SHADER, 0] = Matrix4f().translate(0f, -1f, 0f).scale(1f, 0.02f, 1f)
		hpShader[ShaderType.FRAGMENT_SHADER, 0] = player.hp
		squareVAO.drawTriangles()
		squareVAO.unbind()
	}
}
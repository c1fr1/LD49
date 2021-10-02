import engine.EnigView
import engine.entities.Camera2D
import engine.opengl.*
import engine.opengl.bufferObjects.*
import engine.opengl.jomlExtensions.Vector3f
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

	lateinit var tileShader : ShaderProgram
	lateinit var tileVAO : VAO

	override fun generateResources(window: EnigWindow) {

		tileVAO = VAO(0f, 0f, 1f, 1f)
		tileShader = ShaderProgram("tileShader")

		super.generateResources(window)
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		FBO.prepareDefaultRender()
		player.updatePlayerPosition(dtime, input)
		renderTiles()
		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Pressed
	}

	fun renderTiles() {
		tileShader.enable()
		tileVAO.prepareRender()
		var y = -9 + world.ditchedRows
		for (row in world.tiles) {
			for (x in row.indices) {
				tileShader[ShaderType.FRAGMENT_SHADER, 0] = row[x]
				tileShader[ShaderType.VERTEX_SHADER, 0] = player.getMatrix().scale(5f).translate((x - world.rowWidth / 2).toFloat(), y.toFloat(), 0f)
				tileVAO.drawTriangles()
			}
			++y
		}
		tileVAO.unbind()
	}
}
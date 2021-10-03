import engine.EnigView
import engine.opengl.*
import engine.opengl.bufferObjects.*
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.w3c.dom.Text
import java.nio.file.Paths

class GameView(w : EnigWindow) : EnigView() {

	val input = w.inputHandler

	val aspect = w.aspectRatio

	val player = Player(w)

	var world = World()

	lateinit var squareVAO : VAO
	lateinit var hpShader : ShaderProgram
	lateinit var texShader : ShaderProgram
	lateinit var textShader : ShaderProgram
	lateinit var font : Font

	val tutorialManager = TutorialManager()

	override fun generateResources(window: EnigWindow) {
		super.generateResources(window)

		squareVAO = VAO(-1f, -1f, 2f, 2f)
		hpShader = ShaderProgram("hpShader")
		texShader = ShaderProgram("textureShader")
		textShader = ShaderProgram("textShader")

		font = Font(Paths.get({}.javaClass.classLoader.getResource("Inkfree.ttf")!!.toURI()), 128f, 1024, 512)
		world.generateResources()
		player.generateResources()
		reset()
	}

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		FBO.prepareDefaultRender()

		player.updatePlayerPosition(dtime, input, world, aspect, world.time)
		world.update(dtime, player)
		tutorialManager.manage(world, player, dtime)

		world.render(player, squareVAO, texShader)
		player.render(squareVAO, texShader)
		renderHPBar()
		renderScore()
		if (tutorialManager.step in tutorialManager.texts.indices) {
			renderTutorialText()
		}

		return input.keys[GLFW_KEY_ESCAPE] == KeyState.Released
	}

	fun renderTutorialText() {
		textShader.enable()
		world.tileVAO.prepareRender()
		font.bind()

		lateinit var texMats : Array<Matrix4f>
		lateinit var worldMats : Array<Matrix4f>
		var y = 0
		for (text in tutorialManager.texts[tutorialManager.step]) {
			font.getMats(
				text,
				player.projectionMatrix.translate(-aspect * 50f + 4f, 30 - y * 10f, 0f, Matrix4f()).scale(5f, Matrix4f())
			) { wm, tm ->
				worldMats = wm
				texMats = tm
			}
			textShader[ShaderType.FRAGMENT_SHADER, 0] = Vector3f(1f, 1f, 1f);
			for (i in texMats.indices) {
				textShader[ShaderType.VERTEX_SHADER, 0] = worldMats[i]
				textShader[ShaderType.VERTEX_SHADER, 1] = texMats[i]
				world.tileVAO.fullRender()
			}
			++y
		}
		world.tileVAO.unbind()
	}

	fun renderScore() {
		textShader.enable()
		world.tileVAO.prepareRender()

		if (player.hp >= 0f) {
			lateinit var texMats: Array<Matrix4f>
			lateinit var worldMats: Array<Matrix4f>
			font.getMats(
				"${world.score}",
				player.projectionMatrix.translate(-aspect * 50f + 4f, 40f, 0f, Matrix4f()).scale(10f, Matrix4f())
			) { wm, tm ->
				worldMats = wm
				texMats = tm
			}
			font.bind()
			textShader[ShaderType.FRAGMENT_SHADER, 0] = Vector3f(1f, 1f, 1f);
			for (i in texMats.indices) {
				textShader[ShaderType.VERTEX_SHADER, 0] = worldMats[i]
				textShader[ShaderType.VERTEX_SHADER, 1] = texMats[i]
				world.tileVAO.fullRender()
			}
			world.tileVAO.unbind()
		} else {
			renderCenteredText("Game Over", -25f)
			renderCenteredText("Final Score Is ${world.score}", -5f)
			if (world.score >= HighScoreManager.highScore) {
				renderCenteredText("HIGH SCORE!", 15f)
				HighScoreManager.highScore = world.score
			}
		}
	}
	fun renderCenteredText(text : String, y : Float) {

		lateinit var texMats: Array<Matrix4f>
		lateinit var worldMats: Array<Matrix4f>
		var width = 0f
		for (char in text) {
			width += font.charData[char.code - 32].xadvance()
		}
		font.getMats(
			text,
			player.projectionMatrix.translate(-5 * width / font.fontSize, y, 0f, Matrix4f()).scale(10f, Matrix4f())
		) { wm, tm ->
			worldMats = wm
			texMats = tm
		}
		font.bind()
		textShader[ShaderType.FRAGMENT_SHADER, 0] = Vector3f(1f, 1f, 1f);
		for (i in texMats.indices) {
			textShader[ShaderType.VERTEX_SHADER, 0] = worldMats[i]
			textShader[ShaderType.VERTEX_SHADER, 1] = texMats[i]
			world.tileVAO.fullRender()
		}
		world.tileVAO.unbind()
	}

	fun renderHPBar() {
		hpShader.enable()
		squareVAO.prepareRender()
		hpShader[ShaderType.VERTEX_SHADER, 0] = Matrix4f().translate(0f, -1f, 0f).scale(1f, 0.02f, 1f)
		hpShader[ShaderType.FRAGMENT_SHADER, 0] = player.hp
		squareVAO.drawTriangles()
		squareVAO.unbind()
	}

	fun reset() {
		player.hp = 1f
		player.x = 0f
		player.y = 0f
		world.reset()
	}
}
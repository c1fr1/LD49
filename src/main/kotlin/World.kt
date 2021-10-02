import engine.entities.Camera2D
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.floor
import org.joml.Vector2fc
import org.joml.Vector2i
import java.util.*
import kotlin.math.roundToInt

class World {
	val tiles : LinkedList<Array<Float>> = LinkedList()
	var ditchedRows = 0
	private val rowsShownBelowCam  = 10

	val rowWidth : Int

	lateinit var tileShader : ShaderProgram
	lateinit var tileVAO : VAO

	constructor(numRequiredRows : Int = 21, rowWidth : Int = 20) {
		this.rowWidth = rowWidth
		for (i in 0 until numRequiredRows) {
			tiles.add(Array(rowWidth) {(Math.random().toFloat() + 4f) / 5f})
		}
	}

	fun generateResources() {
		tileVAO = VAO(0f, 0f, 1f, 1f)
		tileShader = ShaderProgram("tileShader")
	}

	fun renderTiles(camera : Camera2D) {
		tileShader.enable()
		tileVAO.prepareRender()
		var y = ditchedRows - rowsShownBelowCam
		for (row in tiles) {
			for (x in row.indices) {
				tileShader[ShaderType.FRAGMENT_SHADER, 0] = row[x]
				tileShader[ShaderType.VERTEX_SHADER, 0] = camera.getMatrix().scale(5f).translate((x - rowWidth / 2).toFloat(), y.toFloat(), 0f)
				tileVAO.drawTriangles()
			}
			++y
		}
		tileVAO.unbind()
	}

	fun degradeTiles(player : Vector2fc) {

	}

	fun getTilePosForWorldPos(pos : Vector2fc) : Vector2i {
		val x = floor(pos.x() / 5f + (rowWidth / 2)).toInt()
		val y = floor(pos.y() / 5f).toInt() + rowsShownBelowCam - ditchedRows
		return Vector2i(x, y)
	}

	fun boundsCheck(pos : Vector2i) : Boolean {
		return pos.x > 0 && pos.y > 0 && pos.x < rowWidth && pos.y < tiles.size
	}

	operator fun get(x : Int, y : Int) = tiles[y][x]
	operator fun get(pos : Vector2i) = get(pos.x, pos.y)
}
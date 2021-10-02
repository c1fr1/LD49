import engine.entities.Camera2D
import engine.opengl.bufferObjects.VAO
import engine.opengl.jomlExtensions.plus
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.floor
import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector2i
import java.util.*

class World {
	val enemies = arrayListOf<Enemy>(LinearEnemy(0f, 10f))
	val projectiles = ArrayList<Projectile>()
	val tiles : LinkedList<Array<Float>> = LinkedList()
	var ditchedRows = 0
	private val rowsShownBelowCam = 10
	private val requiredRowsAboveCam = 15

	val rowWidth : Int

	lateinit var tileShader : ShaderProgram
	lateinit var tileVAO : VAO

	constructor(rowWidth : Int = 20) {
		this.rowWidth = rowWidth
		for (i in 0 until rowsShownBelowCam + requiredRowsAboveCam) {
			addRow()
		}
	}

	fun generateResources() {
		tileVAO = VAO(0f, 0f, 1f, 1f)
		tileShader = ShaderProgram("tileShader")
		Enemy.generateResources()
		ProjectileType.generateResources()
	}

	fun render(camera : Camera2D, squareVAO : VAO, texShader : ShaderProgram) {
		renderTiles(camera)
		Enemy.renderGroup(enemies, camera, squareVAO, texShader)

		squareVAO.prepareRender()
		for (projectile in projectiles) {
			projectile.type.getTexture().bind()
			texShader[ShaderType.VERTEX_SHADER, 0] = projectile.transformMat(camera.getMatrix())
			squareVAO.drawTriangles()
		}
		squareVAO.unbind()
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

	fun update(dtime : Float, player : Player) {
		degradeTiles(dtime, player)
		updateProjectiles(dtime, player)
		updateEnemies(dtime, player)
	}

	fun updateEnemies(dtime : Float, player : Player) {
		for (enemy in enemies) {
			enemy.update(dtime, projectiles, player)
		}
	}

	fun updateProjectiles(dtime : Float, player : Player) {
		var i = 0
		while (i < projectiles.size) {
			if (projectiles[i].updatePosition(dtime, player)) {
				projectiles.removeAt(i)
			} else {
				++i
			}
		}
	}

	fun degradeTiles(dtime : Float, player : Vector2fc) {
		val posll = getTilePosForWorldPos(player + Vector2f(-2f, -2f))
		val posul = getTilePosForWorldPos(player + Vector2f(-2f, 2f))
		val poslr = getTilePosForWorldPos(player + Vector2f(2f, -2f))
		val posur = getTilePosForWorldPos(player + Vector2f(2f, 2f))
		val degradingFactor = dtime / 2f
		if (boundsCheck(posll) && posll != posul && posll != poslr && posll != posur) {
			set(posll, get(posll) - degradingFactor)
		}
		if (boundsCheck(posul) && posul != poslr && posul != posur) {
			set(posul, get(posul) - degradingFactor)
		}
		if (boundsCheck(poslr) && poslr != posur) {
			set(poslr, get(poslr) - degradingFactor)
		}
		if (boundsCheck(posur)) {
			set(posur, get(posur) - degradingFactor)
		}

		while (tiles.size - requiredRowsAboveCam < posul.y) addRow()
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
	operator fun set(x : Int, y : Int, value : Float) {tiles[y][x] = value}
	operator fun set(pos : Vector2i, value : Float) = set(pos.x, pos.y, value)

	private fun addRow() {
		tiles.add(Array(rowWidth) {(Math.random().toFloat() + 4f) / 5f})
	}

}
import engine.entities.Camera2D
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.floor
import org.joml.Math.random
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
	var time = 0f

	val rowWidth : Int

	lateinit var tileShader : ShaderProgram
	lateinit var tileVAO : VAO
	lateinit var tileTexture : Texture

	constructor(rowWidth : Int = 20) {
		this.rowWidth = rowWidth
		for (i in 0 until rowsShownBelowCam + requiredRowsAboveCam) {
			addRow()
		}
	}

	fun generateResources() {
		tileVAO = VAO(0f, 0f, 1f, 1f)
		tileShader = ShaderProgram("tileShader")
		tileTexture = Texture("tiles/tile0.png")
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
		tileTexture.bind()
		var y = ditchedRows - rowsShownBelowCam
		for (row in tiles) {
			for (x in row.indices) {
				tileShader[ShaderType.FRAGMENT_SHADER, 0] = row[x]
				tileShader[ShaderType.FRAGMENT_SHADER, 1] = Vector2f(x.toFloat(), y.toFloat())
				tileShader[ShaderType.FRAGMENT_SHADER, 2] = time
				tileShader[ShaderType.VERTEX_SHADER, 0] = camera.getMatrix().scale(5f).translate((x - rowWidth / 2).toFloat(), y.toFloat(), 0f)
				tileVAO.drawTriangles()
			}
			++y
		}
		tileVAO.unbind()
	}

	fun update(dtime : Float, player : Player) {
		time += dtime
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
		val degradingFactor = dtime / 2f

		val playerPos = getTilePos(player)
		var y = 0
		for (row in tiles) {
			for (x in row.indices) {
				if (playerPos.x == x && playerPos.y == y) {
					row[x] -= degradingFactor
				} else if (row[x] < 0.85 || (playerPos.distance(x, y) > 10f && y < playerPos.y)) {
					row[x] -= degradingFactor / 2f
				} else if (y - playerPos.y < 2) {
					row[x] -= degradingFactor / 40f
				}
			}
			++y
		}

		while (tiles.first.all { it < 0f }) {
			tiles.pop()
			++ditchedRows
		}

		while (tiles.size - requiredRowsAboveCam < playerPos.y) addRow(true)
	}

	fun getTileX(x : Float) = floor(x / 5f + (rowWidth / 2)).toInt()
	fun getTileY(y : Float) = (y / 5f).toInt() + rowsShownBelowCam - ditchedRows

	fun getTilePos(pos : Vector2fc) =Vector2i(getTileX(pos.x()), getTileY(pos.y()))

	fun boundsCheck(pos : Vector2f) = boundsCheck(getTilePos(pos))

	fun boundsCheck(pos : Vector2i) = boundsCheckX(pos.x) && boundsCheckY(pos.y)

	fun boundsCheckX(x : Int) = x in 0 until rowWidth

	fun boundsCheckY(y : Int) = y in tiles.indices

	operator fun get(x : Int, y : Int) = tiles[y][x]
	operator fun get(pos : Vector2i) = get(pos.x, pos.y)
	operator fun set(x : Int, y : Int, value : Float) {tiles[y][x] = value}
	operator fun set(pos : Vector2i, value : Float) = set(pos.x, pos.y, value)
	operator fun get(pos : Vector2f) = get(getTilePos(pos))
	operator fun set(pos : Vector2f, value : Float) = set(getTilePos(pos), value)

	fun getWorldPositionX(x : Int) = 5f * (x.toFloat() + 0.5f - (rowWidth / 2))

	fun getWorldPositionY(y : Int) = 5f * (y.toFloat() + 0.5f + ditchedRows - rowsShownBelowCam)

	private fun addRow(spawnEnemies : Boolean = false) {
		tiles.add(Array(rowWidth) {(Math.random().toFloat() + 4f) / 5f})
		while (random() < 0.1f && spawnEnemies) {
			enemies.add(LinearEnemy(getWorldPositionX((random() * rowWidth).toInt()), getWorldPositionY(ditchedRows + tiles.size)))
		}
	}

}
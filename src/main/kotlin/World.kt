import enemies.Enemy
import enemies.HydrantEnemy
import enemies.LinearEnemy
import enemies.Sprinkler
import engine.entities.Camera2D
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.*
import org.joml.Math.*
import java.lang.Math
import java.util.*

class World {
	val enemies = arrayListOf<Enemy>()
	val projectiles = ArrayList<Projectile>()
	val tiles : LinkedList<Array<Float>> = LinkedList()
	var ditchedRows = 0
	private val rowsShownBelowCam = 10
	private val requiredRowsAboveCam = 15
	var time = 0f

	val rowWidth : Int

	var score = 0
	val scoreMultiplier : Int
		get() = ditchedRows / rowWidth + 1

	lateinit var tileShader : ShaderProgram
	lateinit var tileVAO : VAO
	lateinit var tileTexture : Texture

	var rowsInSection = 1

	var currentPhase = Phase.empty

	constructor(rowWidth : Int = 20) {
		this.rowWidth = rowWidth
		for (i in 0 until rowsShownBelowCam + requiredRowsAboveCam) {
			addRow()
		}
	}

	fun generateResources() {
		tileVAO = VAO(0f, 0f, 1f, 1f)
		tileShader = ShaderProgram("tileShader")
		tileTexture = Texture("tiles/woodTex.png")
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
		var prevRow : Array<Float>? = null
		var y = ditchedRows - rowsShownBelowCam - 1

		val iter = tiles.iterator()

		val rows : Array<Array<Float>?> = arrayOfNulls(3)
		rows[2] = if (iter.hasNext()) iter.next() else null

		while (rows[2] != null || rows[1] != null) {

			for (x in -1..rowWidth) {
				val strs = Matrix3f()
				for (rx in -1..1) {
					for (ry in 0..2) {
						strs[rx + 1, ry] = if (x + rx in 0 until rowWidth)rows[ry]?.get(x + rx) ?: 0f else 0f
					}
				}

				tileShader[ShaderType.FRAGMENT_SHADER, 0] = strs
				tileShader[ShaderType.FRAGMENT_SHADER, 1] = Vector2f(x.toFloat() - (rowWidth / 2), y.toFloat() + 1f)
				tileShader[ShaderType.FRAGMENT_SHADER, 2] = time
				tileShader[ShaderType.FRAGMENT_SHADER, 3] = ParticleManager.nonPlayerRandParticleId()
				tileShader[ShaderType.VERTEX_SHADER, 0] = camera.getMatrix().scale(5f).translate((x - rowWidth / 2).toFloat(), y.toFloat(), 0f)
				tileVAO.drawTriangles()
			}

			rows[0] = rows[1]
			rows[1] = rows[2]
			rows[2] = if (iter.hasNext()) iter.next() else null
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
			enemy.update(dtime, this, player)
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

	fun degradeTiles(dtime : Float, player : Player) {
		val degradingFactor = dtime / 2f

		val playerPos = getTilePos(player)
		var y = 0
		for (row in tiles) {
			for (x in row.indices) {
				if (enemies.any { it.protectsTile(getTilePos(it), x, y)}) {

				} else if (row[x] < 0) {
					row[x] = -0.0001f;
				} else if (playerPos.x == x && playerPos.y == y) {
					row[x] -= degradingFactor
				} else if (row[x] < 0.85 || (playerPos.distance(x, y) > 10f && y < playerPos.y)) {
					row[x] -= degradingFactor / 2f
				} else if (y - playerPos.y < 2) {
					row[x] -= degradingFactor / 40f
				}
			}
			++y
		}

		while (tiles[1].all { it < 0f }) {
			tiles.pop()
			++ditchedRows
			if (player.hp > 0) {
				score += scoreMultiplier
			}
		}

		while (tiles.size - playerPos.y < requiredRowsAboveCam) addRow(true)
	}

	fun getTileX(x : Float) = floor(x / 5f + (rowWidth / 2)).toInt()
	fun getTileY(y : Float) = (y / 5f).toInt() + rowsShownBelowCam - ditchedRows

	fun getTilePos(pos : Vector2fc) = Vector2i(getTileX(pos.x()), getTileY(pos.y()))

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
		tiles.add(Array(rowWidth) {
			(Math.random().toFloat() + 5f) / 6f - abs(it - (rowWidth / 2)).toFloat() / (rowWidth.toFloat() * 5)
		})
		if (spawnEnemies) {
			rowsInSection--
			if (rowsInSection <= 0) {
				currentPhase = Phase.randomType()
				rowsInSection = currentPhase.getLength()
				currentPhase.getEnemies(1f + scoreMultiplier / 100f, this) {enemies.add(it)}
				enemies.add(HydrantEnemy(getWorldPositionX(rowWidth / 2) - 2.5f, getWorldPositionY(tiles.size)))
			}
		}
	}

	fun reset() {
		ditchedRows = 0
		tiles.clear()
		projectiles.clear()
		enemies.clear()
		score = 0
		for (i in 0 until rowsShownBelowCam + requiredRowsAboveCam) {
			addRow()
		}
	}

}
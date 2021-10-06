import enemies.Enemy
import enemies.LinearEnemy
import enemies.Sprayer
import enemies.Sprinkler
import org.joml.Math.random

enum class Phase(val numExts : Int, val sprinklers : Int, sprayers : Int, val avgLength : Int) {
	ext(4, 0, 0, 20),
	sprinkle(0, 2, 0, 20),
	extSprinkle(2, 1, 0, 30),
	sprinkleSpray(0, 1, 1, 20),
	extSpray(2, 0, 1, 15),
	empty(0, 0, 0, 10),
	;

	fun getLength() : Int {
		return avgLength + (5f * (random() - 0.5)).toInt()
	}

	fun getEnemies(difficulty : Float, world : World, addFunction : (Enemy) -> Unit) {
		for (x in 0 until ((numExts + random()) * difficulty).toInt()) {
			val x = world.getWorldPositionX((random() * world.rowWidth).toInt())
			val y = world.getWorldPositionY((random() * (world.rowsInSection - 1)).toInt() + world.tiles.size + 1)
			addFunction(LinearEnemy(x, y))
		}
		for (x in 0 until ((sprinklers + random() / 2f) * difficulty).toInt()) {
			val x = world.getWorldPositionX((random() * world.rowWidth).toInt())
			val y = world.getWorldPositionY((random() * (world.rowsInSection - 1)).toInt() + world.tiles.size + 1)
			addFunction(Sprinkler(x, y))
		}
		for (x in 0 until ((sprinklers + random() / 2f) * difficulty).toInt()) {
			val x = world.getWorldPositionX((random() * world.rowWidth).toInt())
			val y = world.getWorldPositionY((random() * (world.rowsInSection - 1)).toInt() + world.tiles.size + 1)
			addFunction(Sprayer(x, y))
		}
	}

	companion object {
		fun randomType() : Phase {
			val r = random() * 11
			return if (r < 2) {
				ext
			} else if (r < 4) {
				sprinkle
			} else if (r < 6) {
				extSprinkle
			} else if (r < 8) {
				sprinkleSpray
			} else if (r < 10) {
				extSpray
			} else {
				empty
			}
		}
	}
}
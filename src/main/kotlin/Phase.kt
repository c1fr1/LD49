import enemies.Enemy
import enemies.LinearEnemy
import enemies.Sprinkler
import org.joml.Math.random

enum class Phase(val numExts : Int, val sprinklers : Int, val avgLength : Int) {
	ext(4, 0, 20),
	empty(0, 0, 10),
	sprinkle(0, 2, 20),
	mix(2, 1, 30);

	fun getLength() : Int {
		return avgLength + (5f * (random() - 0.5)).toInt()
	}

	fun getEnemies(difficulty : Float, world : World, addFunction : (Enemy) -> Unit) {
		for (x in 0 until (numExts * difficulty).toInt()) {
			val x = world.getWorldPositionX((random() * world.rowWidth).toInt())
			val y = world.getWorldPositionY((random() * world.rowsInSection).toInt() + world.tiles.size)
			addFunction(LinearEnemy(x, y))
		}
		for (x in 0 until (sprinklers * difficulty).toInt()) {
			val x = world.getWorldPositionX((random() * world.rowWidth).toInt())
			val y = world.getWorldPositionY((random() * world.rowsInSection).toInt() + world.tiles.size)
			addFunction(Sprinkler(x, y))
		}
	}

	companion object {
		fun random() : Phase {
			val r = Math.random()
			return if (r < 0.3) {
				ext
			} else if (r < 0.4) {
				empty
			} else if (r < 0.7) {
				sprinkle
			} else {
				mix
			}
		}
	}
}
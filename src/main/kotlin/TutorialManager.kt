import enemies.HydrantEnemy
import enemies.LinearEnemy
import org.joml.Math.random

class TutorialManager {
	var step = -1
	var texts = arrayOf(
		arrayOf("WASD to move, left click to shoot"),
		arrayOf("left shift to dash"),
		arrayOf("tiles burn when you stand of them", "you will rapidly loose health when not standing on a tile", "enemies protect the tiles around them"),
		arrayOf("despite appearances, this is a fire extinguisher", "it hurts, but you can kill it too!"),
		arrayOf("fire hydrants are invincible while there are enemies below them", "you will not survive trying to pass them"),
		arrayOf("you gain points by burning the bridge and killing enemies", "good luck!")
	)
	var timeOnStep = 0f
	var exampleExtinguisher : LinearEnemy? = null
	var exampleHydrant : HydrantEnemy? = null
	fun manage(world : World, player : Player, dtime : Float) {
		timeOnStep += dtime
		if (step in texts.indices) {
			world.enemies.removeAll{!(it === exampleExtinguisher) && !(it === exampleHydrant)}
		}
		when (step) {
			0 -> {
				for (row in world.tiles) {
					for (i in row.indices) {
						row[i] = 1f
					}
				}
				world.enemies.clear()
				if (timeOnStep > 1f && player.lengthSquared() > 10f && player.projectiles.isNotEmpty()) {
					++step
					timeOnStep = 0f
				}
			}
			1 -> {
				for (row in world.tiles) {
					for (i in row.indices) {
						row[i] = 1f
					}
				}
				if (timeOnStep > 1f && player.dashCD > 0) {
					++step
					timeOnStep = 0f
				}
			}
			2 -> {
				if (timeOnStep > 10f) {
					val playerPos = world.getTilePos(player)
					exampleExtinguisher = LinearEnemy(world.getWorldPositionX(playerPos.x), world.getWorldPositionY(playerPos.y + 7))
					world.enemies.add(exampleExtinguisher!!)
					++step
					timeOnStep = 0f
				}
			}
			3 -> {
				if (exampleExtinguisher!!.hp < 0f) {
					val playerPos = world.getTilePos(player)
					exampleExtinguisher = LinearEnemy(world.getWorldPositionX((random() * world.rowWidth).toInt()), world.getWorldPositionY(playerPos.y + 7))
					exampleHydrant = HydrantEnemy(world.getWorldPositionX(world.rowWidth / 2) - 2.5f, world.getWorldPositionY(playerPos.y + 9))
					world.enemies.add(exampleExtinguisher!!)
					world.enemies.add(exampleHydrant!!)
					++step
					timeOnStep = 0f
				}
			}
			4 -> {
				if (exampleHydrant!!.hp < 0f) {
					++step
					timeOnStep = 0f
				}
			}
			5 -> {
				if (timeOnStep < 5f) {
					++step
				}
			}
		}
	}
}
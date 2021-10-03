class TutorialManager {
	var step = 0
	var texts = arrayOf(
		arrayOf("WASD to move, left click to shoot"),
		arrayOf("tiles burn when you stand of them", "you will rapidly loose health when not standing on a tile", "enemies protect the tiles around them"),
		arrayOf("despite appearances, this is a fire extinguisher", "it hurts, but you can kill it too!"),
		arrayOf("fire hydrants are invincible while there are enemies below them", "you will not survive trying to pass them"),
		arrayOf("you gain points by burning the bridge and killing enemies", "good luck!")
	)
	var timeOnStep = 0f
	fun manage(world : World, player : Player, dtime : Float) {
		timeOnStep += dtime
		when (step) {
			0 -> {
				for (row in world.tiles) {
					for (i in row.indices) {
						row[i] = 1f
					}
				}
				if (timeOnStep > 1f && player.lengthSquared() > 10f && player.projectiles.isNotEmpty()) {
					++step
				}
			}
			1 -> {
				if (timeOnStep > 5f) {
					++step
				}
			}
		}
	}
}
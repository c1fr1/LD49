class TutorialManager {
	val step = 0
	var texts = arrayOf(
		arrayOf("WASD to move, left click to shoot"),
		arrayOf("tiles burn when you stand of them"))
	fun manage(world : World) {
		if (step == 0) {
			for (row in world.tiles) {
				for (i in row.indices) {
					row[i] = 1f
				}
			}
		}
	}
}
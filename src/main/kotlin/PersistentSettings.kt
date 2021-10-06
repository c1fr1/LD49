import java.io.*

object PersistentSettings {

	var highScore : Int = 0
	var playerColor : PlayerColor = PlayerColor.default
	fun load() {
		try {
			val settingsFile = BufferedReader(FileReader("./FuseSettings.txt"))
			highScore = settingsFile.readLine().toIntOrNull() ?: 0
			val ordinal = settingsFile.readLine()?.toIntOrNull() ?: 0
			val values = PlayerColor.values()
			playerColor = if (ordinal in values.indices) values[ordinal] else PlayerColor.default
			settingsFile.close()
		} catch (e : FileNotFoundException) {}
	}

	fun save() {
		val writer = FileWriter("./FuseSettings.txt")
		writer.write("$highScore\n${playerColor.ordinal}\n")
		writer.close()
	}
}

enum class PlayerColor(val displayName : String) {
	default("Classic"),
	bright("Hot"),
	dark("Dirty"),
	green("Green"),
	blue("Blue")
}
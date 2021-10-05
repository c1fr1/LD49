import java.io.*

object PersistentSettings {
	var highScore = 0
	var playerColor = PlayerColor.dark
	fun load() {
		try {
			val settingsFile = BufferedReader(FileReader("FuseSettings.txt"))
			highScore = settingsFile.readLine().toIntOrNull() ?: 0
			playerColor = PlayerColor.values().firstOrNull { it.ordinal == (settingsFile.readLine().toIntOrNull() ?: 0) } ?: PlayerColor.default
			settingsFile.close()
		} catch (e : FileNotFoundException) {}
	}

	fun save() {
		val writer = BufferedWriter(FileWriter("FuseSettings.txt"))
		writer.write("$highScore\n${playerColor.ordinal}\n")
	}
}

enum class PlayerColor(val displayName : String) {
	default("Classic"),
	bright("Hot"),
	dark("Dirty"),
	green("Green"),
	blue("Blue")
}
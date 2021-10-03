import org.joml.Math.random

enum class Phase(val numExts : Int, val sprinklers : Int, val avgLength : Int) {
	ext(4, 0, 20),
	empty(0, 0, 10),
	sprinkle(0, 2, 20),
	mix(3, 1, 30);

	fun getLength() : Int {
		return avgLength + (5f * (random() - 0.5)).toInt()
	}

	companion object {

	}
}
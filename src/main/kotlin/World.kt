import java.util.*

class World {
	val tiles : Queue<Array<Float>> = LinkedList()
	var ditchedRows: Int = 0

	val rowWidth : Int

	constructor(numRequiredRows : Int = 21, rowWidth : Int = 20) {
		this.rowWidth = rowWidth
		for (i in 0 until numRequiredRows) {
			tiles.add(Array(rowWidth) {(Math.random().toFloat() + 4f) / 5f})
		}
	}
}
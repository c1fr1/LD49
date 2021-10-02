import engine.entities.Camera2D
import engine.opengl.EnigWindow
import engine.opengl.InputHandler
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class Player(w : EnigWindow) : Camera2D(w) {
	private var forward = GLFW_KEY_W
	private var backward = GLFW_KEY_S
	private var left = GLFW_KEY_A
	private var right = GLFW_KEY_D

	fun updatePlayerPosition(dtime : Float, input : InputHandler) {
		val delta = Vector2f()
		if (input.keys[forward].isDown) delta.y += 1f
		if (input.keys[backward].isDown) delta.y -= 1f
		if (input.keys[left].isDown) delta.x -= 1f
		if (input.keys[right].isDown) delta.x += 1f

		val speed = dtime * 25

		if (delta.lengthSquared() > 0.5f) {
			delta.normalize(speed)
		}
		x += delta.x
		y += delta.y
	}
}
import engine.entities.Camera2D
import engine.opengl.EnigWindow
import engine.opengl.InputHandler
import engine.opengl.bufferObjects.SSBO1f
import engine.opengl.bufferObjects.SSBO2f
import engine.opengl.bufferObjects.SSBO3f
import engine.opengl.bufferObjects.VAO
import engine.opengl.jomlExtensions.toFloatArray
import engine.opengl.shaders.ComputeProgram
import example.rand
import org.joml.Math.random
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.min


private const val NUM_PARTICLES = 100

class Player(w : EnigWindow) : Camera2D(w) {

	var hp = 1f
	var projectiles = ArrayList<Projectile>()

	// CONTROLS
	private var forward = GLFW_KEY_W
	private var backward = GLFW_KEY_S
	private var left = GLFW_KEY_A
	private var right = GLFW_KEY_D

	private lateinit var shader : ComputeProgram
	private lateinit var posSSBO : SSBO2f
	private lateinit var velSSBO : SSBO2f
	private lateinit var sizeSSBO : SSBO1f
	private lateinit var colorSSBO : SSBO3f

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

		val recoverySpeed = 0.25f

		hp = min(hp + dtime * recoverySpeed, 1f)
	}

	fun generateParticles(dtime : Float, time : Float) {
		shader[0] = this
		shader[1] = dtime
		shader[2] = time
		posSSBO.bindToPosition(0)
		velSSBO.bindToPosition(1)
		sizeSSBO.bindToPosition(2)
		shader.run(NUM_PARTICLES)
	}

	fun generateResources() {
		shader = ComputeProgram("particles.glsl")
		posSSBO = SSBO2f(FloatArray(2 * NUM_PARTICLES))
		velSSBO = SSBO2f(FloatArray(2 * NUM_PARTICLES))
		sizeSSBO = SSBO1f(FloatArray(NUM_PARTICLES) {random().toFloat()})
		colorSSBO = SSBO3f(Array(NUM_PARTICLES) {
			Vector3f(0.5f + random().toFloat() / 2f, 0f + random().toFloat(), random().toFloat() / 10f)
		}.toFloatArray())
	}
}
import engine.entities.Camera2D
import engine.opengl.EnigWindow
import engine.opengl.InputHandler
import engine.opengl.bufferObjects.SSBO1f
import engine.opengl.bufferObjects.SSBO2f
import engine.opengl.bufferObjects.SSBO3f
import engine.opengl.bufferObjects.VAO
import engine.opengl.checkGLError
import engine.opengl.jomlExtensions.toFloatArray
import engine.opengl.shaders.ComputeProgram
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.clamp
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

	private lateinit var compShader : ComputeProgram
	private lateinit var shader : ShaderProgram
	private lateinit var posSSBO : SSBO2f
	private lateinit var velSSBO : SSBO2f
	private lateinit var sizeSSBO : SSBO1f
	private lateinit var colorSSBO : SSBO3f

	fun updatePlayerPosition(dtime : Float, input : InputHandler, world : World) {
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

		val recoverySpeed = dtime * 0.25f

		if (world[this] < 0f) {
			hp -= recoverySpeed * 2f
		} else {
			hp = min(hp + recoverySpeed, 1f)
		}
	}

	fun generateParticles(dtime : Float, time : Float) {
		checkGLError()
		compShader.enable()
		compShader[0] = this as Vector2f
		compShader[1] = dtime
		compShader[2] = time
		posSSBO.bindToPosition(0)
		velSSBO.bindToPosition(1)
		sizeSSBO.bindToPosition(2)
		checkGLError()
		compShader.run(NUM_PARTICLES)
		checkGLError()
	}

	fun render(vao : VAO) {
		shader.enable()
		posSSBO.bindToPosition(0)
		sizeSSBO.bindToPosition(2)
		colorSSBO.bindToPosition(3)
		shader[ShaderType.VERTEX_SHADER, 0] = getMatrix()
		vao.prepareRender()
		vao.drawTrianglesInstanced(NUM_PARTICLES)
		vao.unbind()
	}

	fun generateResources() {
		compShader = ComputeProgram("particles.glsl")
		shader = ShaderProgram("particleShader")
		posSSBO = SSBO2f(FloatArray(2 * NUM_PARTICLES))
		velSSBO = SSBO2f(FloatArray(2 * NUM_PARTICLES))
		sizeSSBO = SSBO1f(FloatArray(NUM_PARTICLES) {random().toFloat()})



		fun emberSlide(factor : Float) : Vector3f {
			return Vector3f(clamp(3f - factor * 3f, 0f, 1f), 1f - factor, 0f)
		}

		colorSSBO = SSBO3f(Array(NUM_PARTICLES) {
			emberSlide(random().toFloat())
		}.toFloatArray())
	}
}
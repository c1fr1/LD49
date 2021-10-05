import engine.TAUf
import engine.entities.Camera2D
import engine.openal.Sound
import engine.openal.SoundSource
import engine.opengl.bufferObjects.*
import engine.opengl.jomlExtensions.*
import engine.opengl.shaders.ComputeProgram
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math
import org.joml.Math.clamp
import org.joml.Math.random
import org.joml.Vector2f
import org.joml.Vector3f

object ParticleManager {
	val particleCount = 800
	val playerParticleCount = 100

	private lateinit var compShader : ComputeProgram
	private lateinit var shader : ShaderProgram
	private lateinit var posSSBO : SSBO2f
	private lateinit var velSSBO : SSBO2f
	private lateinit var sizeSSBO : SSBO1f
	private lateinit var colorSSBO : SSBO3f

	private val particleRequests = ArrayList<ParticleRequest>()

	fun requestParticles(particle : Projectile, vel : Vector2f) {
		val color = when (particle.type) {
			ProjectileType.player -> emberSlide(random().toFloat() * 0.5f)
			 else -> Vector3f(0.5f, 0.5f, 0.5f)
		}
		particleRequests.add(ParticleRequest(color, particle as Vector2f, vel))
	}

	fun generateParticles(dtime : Float, time : Float, speed : Vector2f, player : Player) {
		SSBO.syncSSBOs()

		val sizes = sizeSSBO.retrieveGLData()
		val poses = posSSBO.retrieveGLData()
		val vels = velSSBO.retrieveGLData()
		val colors = colorSSBO.retrieveGLData()
		for (particleRequest in particleRequests) {
			for (i in 0 until particleRequest.count) {
				val ind = nonPlayerRandParticleId()
				if (sizes[ind] < 0) {
					sizes[ind] = 0.5f + random().toFloat() / 2f
				}
				poses[ind * 2] = particleRequest.pos.x
				poses[ind * 2 + 1] = particleRequest.pos.y
				val velOffset = Vector2f(5f, 0f).rotate(random().toFloat() * TAUf) * random().toFloat()
				vels[ind * 2] = particleRequest.vel.x + velOffset.x
				vels[ind * 2 + 1] = particleRequest.vel.y + velOffset.y

				colors[ind * 4] = particleRequest.color.x
				colors[ind * 4 + 1] = particleRequest.color.y
				colors[ind * 4 + 2] = particleRequest.color.z
			}
		}
		particleRequests.clear()
		sizeSSBO.updateData(0L, sizes)
		posSSBO.updateData(0L, poses)
		velSSBO.updateData(0L, vels)
		colorSSBO.updateData(0L, colors)

		compShader.enable()
		compShader[0] = player as Vector2f
		compShader[1] = dtime
		compShader[2] = time
		compShader[3] = speed
		compShader[4] = player.hp
		posSSBO.bindToPosition(0)
		velSSBO.bindToPosition(1)
		sizeSSBO.bindToPosition(2)
		compShader.run(particleCount)
	}

	fun render(vao : VAO, cam : Camera2D) {
		shader.enable()
		posSSBO.bindToPosition(0)
		sizeSSBO.bindToPosition(2)
		colorSSBO.bindToPosition(3)
		shader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix()
		vao.prepareRender()
		vao.drawTrianglesInstanced(particleCount)
		vao.unbind()
	}

	fun generateResources() {
		compShader = ComputeProgram("particles.glsl")
		shader = ShaderProgram("particleShader")
		posSSBO = SSBO2f(FloatArray(2 * particleCount))
		velSSBO = SSBO2f(FloatArray(2 * particleCount))
		sizeSSBO = SSBO1f(FloatArray(particleCount) {if (it < playerParticleCount) Math.random().toFloat() else -1f})

		fun colorSlide(factor : Float, baseColor : Vector3f) : Vector3f {
			return Vector3f(factor, factor, factor) + baseColor
		}

		colorSSBO = SSBO3f(Array(particleCount) {
			when(PersistentSettings.playerColor) {
				PlayerColor.default -> emberSlide(random().toFloat())
				PlayerColor.bright -> emberSlide(random().toFloat() - 0.5f)
				PlayerColor.dark -> emberSlide(random().toFloat() + 0.35f)
				PlayerColor.green -> colorSlide(random().toFloat() / 2f, Vector3f(0f, 0.5f, 0f))
				PlayerColor.blue -> colorSlide(random().toFloat() / 2f, Vector3f(0f, 0f, 0.9f))
			}
		}.toFloatArray())
	}

	fun nonPlayerRandParticleId() : Int {
		return (playerParticleCount until particleCount).random()
	}

	fun emberSlide(factor : Float) : Vector3f {
		return Vector3f(clamp(3f - factor * 3f, 0f, 1f), 1f - factor, -2 * factor)
	}
}

class ParticleRequest(val color : Vector3f, val pos : Vector2f, val vel : Vector2f, val count : Int = 20)
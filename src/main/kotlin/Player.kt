import enemies.Enemy
import engine.PIf
import engine.entities.Camera2D
import engine.entities.Orientation2D
import engine.openal.Sound
import engine.openal.SoundSource
import engine.opengl.EnigWindow
import engine.opengl.InputHandler
import engine.opengl.KeyState
import engine.opengl.bufferObjects.SSBO1f
import engine.opengl.bufferObjects.SSBO2f
import engine.opengl.bufferObjects.SSBO3f
import engine.opengl.bufferObjects.VAO
import engine.opengl.checkGLError
import engine.opengl.jomlExtensions.minus
import engine.opengl.jomlExtensions.plus
import engine.opengl.jomlExtensions.toFloatArray
import engine.opengl.shaders.ComputeProgram
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.clamp
import org.joml.Math.random
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Player(w : EnigWindow) : Camera2D(w) {

	var hp = 1f
	var shotCD = 0f
	var dashCD = 0f
	var projectiles = ArrayList<PlayerProjectile>()

	// CONTROLS
	private var forward = GLFW_KEY_W
	private var backward = GLFW_KEY_S
	private var left = GLFW_KEY_A
	private var right = GLFW_KEY_D
	private var dash = GLFW_KEY_LEFT_SHIFT

	private lateinit var sources : Array<SoundSource>
	private lateinit var hitSound : Sound
	private lateinit var fireSound : Sound
	private lateinit var fireSource : SoundSource
	private lateinit var attackSounds : Array<Sound>
	var sourceIndex = 0

	private val internalCam = Camera2D(w)

	override fun getMatrix(): Matrix4f {
		return internalCam.getMatrix()
	}

	fun updatePlayerPosition(dtime : Float, input : InputHandler, world : World, aspectRatio : Float, time : Float) {

		val delta = Vector2f()
		if (input.keys[forward].isDown) delta.y += 1f
		if (input.keys[backward].isDown) delta.y -= 1f
		if (input.keys[left].isDown) delta.x -= 1f
		if (input.keys[right].isDown) delta.x += 1f

		if (input.keys[dash] == KeyState.Released && dashCD < 0 && hp > 0) {
			val targetPos = Vector2f(this)
			targetPos.x += aspectRatio * 50 * input.glCursorX
			targetPos.y -= 50 * input.glCursorY
			if (targetPos.distance(this) > 20f) {
				set(this + (targetPos - this).normalize(20f))
			} else {
				set(targetPos)
			}
			dashCD = 2f
		}
		dashCD -= dtime

		var speed = dtime * 20

		internalCam.lerp(this, 0.1f)
		if (internalCam.distance(this) < speed * 2f) {
			internalCam.set(this)
		}

		if (shotCD > 0) {
			speed /= 2
		}

		if (delta.lengthSquared() > 0.5f) {
			delta.normalize(speed)
		}
		if (hp > 0) {
			x += delta.x
			y += delta.y
		}
		ParticleManager.generateParticles(dtime, time, delta, this)

		val recoverySpeed = dtime * 0.25f

		if (hp > 0) {
			if (!world.boundsCheck(this) || world[this] < 0f) {
				hp -= recoverySpeed * 2f
			} else {
				hp = min(hp + recoverySpeed, 1f)
			}
		}

		if (hp > 0f) {
			fireSource.setVolume(clamp(hp, 0f, 1f) / 3f + 0.5f)
			fireSource.setPitch(1.5f - clamp(hp, 0f, 1f) / 2f)
		} else {
			fireSource.setVolume(0f)
		}

		shotCD -= dtime
		if (input.mouseButtons[GLFW_MOUSE_BUTTON_LEFT].isDown && shotCD < 0 && hp > 0f) {
			attack(input, aspectRatio)
		}

		var i = 0
		while (i < projectiles.size) {
			if (projectiles[i].updatePosition(dtime, this) || projectiles[i].checkEnemyCollision(world)) {
				projectiles.removeAt(i)
			} else {
				++i
			}
		}
	}

	fun attack(input : InputHandler, aspectRatio : Float) {
		projectiles.add(PlayerProjectile(this, input, aspectRatio))
		shotCD = 0.25f
		playSound(attackSounds.random(), 10f)
	}

	fun render(vao : VAO, texShader : ShaderProgram) {
		ParticleManager.render(vao, this)
		vao.prepareRender()
		texShader.enable()
		for (proj in projectiles) {
			proj.type.getTexture().bind()
			texShader[ShaderType.VERTEX_SHADER, 0] = proj.transformMat(getMatrix())
			vao.drawTriangles()
		}
		vao.unbind()
	}

	fun landHit() {
		playSound(hitSound, 1f)
	}

	fun playSound(sound : Sound, volume : Float) {
		sources[sourceIndex].stop()
		sources[sourceIndex].setVolume(volume)
		sources[sourceIndex++].playSound(sound)
		sourceIndex %= sources.size
	}

	fun generateResources() {
		ParticleManager.generateResources()

		sources = Array(10) { SoundSource(0f, 0f, 0f) }
		fireSource = SoundSource(0f, 0f, 0f)
		hitSound = Sound("sounds/sizzle0.ogg")
		attackSounds = Array(3) {Sound("sounds/attack$it.ogg")}
		fireSound = Sound("sounds/fire.ogg")
		fireSource.setVolume(0.5f)
		fireSource.setLoop()
		fireSource.playSound(fireSound)
	}
}

class PlayerProjectile(player : Player, input : InputHandler, aspectRatio : Float, val speed : Float = 30f) :
	Projectile, Orientation2D(atan2(-input.glCursorY, input.glCursorX * aspectRatio), player) {
	override val type : ProjectileType = ProjectileType.player
	override var travelDistance : Float = 75f

	override fun updatePosition(dtime : Float, player : Player) : Boolean {
		val distance = dtime * speed
		x += cos(rotation) * distance
		y += sin(rotation) * distance
		travelDistance -= distance
		return travelDistance < 0f
	}

	fun checkEnemyCollision(world : World) : Boolean {
		for (i in world.enemies.indices) {
			if (distance(world.enemies[i]) < 3f) {
				world.enemies[i].hp -= 0.2f
				world.enemies[i].playSound(Enemy.damagedSounds.random(), 0.5f, this, 0.2f)
				if (world.enemies[i].hp < 0) {
					world.score += world.enemies[i].bounty * world.scoreMultiplier
					world.enemies.removeAt(i)
				}
				ParticleManager.requestParticles(this, Vector2f(cos(rotation) * speed, sin(rotation) * speed) / 2f)
				return true
			}
		}
		return false
	}

	override fun transformMat(cam : Matrix4f) : Matrix4f = cam.translate(x, y, 0f).rotateZ(rotation - PIf / 2)
}
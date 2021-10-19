package enemies

import Projectile
import World
import engine.PIf
import engine.entities.Camera2D
import engine.entities.Orientation2D
import engine.openal.Sound
import engine.openal.SoundSource
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Math.random
import org.joml.Vector2f
import org.joml.Vector2i

sealed class Enemy(x : Float, y : Float, rotation : Float = 0f) : Orientation2D(rotation, Vector2f(x, y)) {

	open var attackTimer = 2f * random().toFloat()

	open var hp = 1f

	abstract val bounty : Int

	open fun update(dtime : Float, world : World, playerPos : Orientation2D) {
		attackTimer -= dtime
		if (attackTimer < 0) {
			shootProjectiles(world.projectiles, playerPos)
		}
	}

	abstract fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D)

	abstract fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) : Boolean

	fun playSound(sound : Sound, volume : Float = 1f, player : Vector2f = Vector2f(0f, 0f), pitch : Float = 1f) {
		sources[sourceIndex].stop()
		sources[sourceIndex].setVolume(volume)
		sources[sourceIndex].x = (x - player.x) / 50f
		sources[sourceIndex].z = (y - player.y) / 50f
		sources[sourceIndex].setPitch(pitch)
		sources[sourceIndex].updateSourcePosition()
		sources[sourceIndex++].playSound(sound)
		sourceIndex %= sources.size
	}

	companion object {
		lateinit var extinguisherTex : Texture
		lateinit var hydrantTex : Texture
		lateinit var invincibleHydrantTex : Texture
		lateinit var sprinklerTex : Texture
		lateinit var sprayerTex : Texture
		lateinit var sources : Array<SoundSource>
		lateinit var attackSounds : Array<Sound>
		lateinit var damagedSounds : Array<Sound>
		lateinit var sprinklerSounds : Array<Sound>

		var sourceIndex = 0

		fun renderGroup(enemies : ArrayList<Enemy>, cam : Camera2D, square : VAO, shader : ShaderProgram) {
			shader.enable()
			square.prepareRender()
			for (enemy in enemies) {
				when (enemy) {
					is HydrantEnemy -> {
						if (enemies.any { it.y < enemy.y }) {
							invincibleHydrantTex.bind()
						} else {

							hydrantTex.bind()
						}
					}
					is LinearEnemy -> extinguisherTex.bind()
					is Sprinkler -> sprinklerTex.bind()
					is Sprayer -> sprayerTex.bind()
				}
				shader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix().translate(enemy.x, enemy.y, 0f).scale(2f).rotateZ(enemy.rotation + PIf / 2f)
				square.drawTriangles()
			}
			square.unbind()
		}

		fun generateResources() {
			extinguisherTex = Texture("fire extinguisher.png")
			hydrantTex = Texture("fire hydrant.png")
			invincibleHydrantTex = Texture("invincible fire hydrant.png")
			sprinklerTex = Texture("sprinkler.png")
			sprayerTex = Texture("sprayer.png")
			sources = Array(20) { SoundSource(0f, 0f, 0f) }
			attackSounds = Array(4) {Sound("sounds/ext$it.ogg")}
			damagedSounds = Array(3) {Sound("sounds/hit$it.ogg")}
			sprinklerSounds = Array(3) {Sound("sounds/sprinkler/sprinkler$it.ogg")}
		}
	}
}
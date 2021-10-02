import engine.entities.Camera2D
import engine.entities.Orientation2D
import engine.openal.Sound
import engine.openal.SoundSource
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Vector2f

abstract class Enemy(x : Float, y : Float) : Orientation2D(0f, Vector2f(x, y)) {

	open var attackTimer = 2f

	open var hp = 1f

	fun update(dtime : Float, projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		attackTimer -= dtime
		if (attackTimer < 0) {
			attackTimer = 1.75f + Math.random().toFloat() / 4f
			shootProjectiles(projectileList, playerPos)
		}
	}

	abstract fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D)

	fun playSound(sound : Sound, volume : Float = 1f, player : Vector2f = Vector2f(0f, 0f), pitch : Float = 1f) {
		sources[sourceIndex].stop()
		sources[sourceIndex].setVolume(volume)
		sources[sourceIndex].x = 100f
		sources[sourceIndex].y = y - player.y
		sources[sourceIndex].setPitch(pitch)
		sources[sourceIndex].updateSourcePosition()
		sources[sourceIndex++].playSound(sound)
		sourceIndex %= sources.size
	}

	companion object {
		lateinit var hydrantTex : Texture
		lateinit var sources : Array<SoundSource>
		lateinit var attackSounds : Array<Sound>
		lateinit var damagedSounds : Array<Sound>

		var sourceIndex = 0

		fun renderGroup(enemies : ArrayList<Enemy>, cam : Camera2D, square : VAO, shader : ShaderProgram) {
			shader.enable()
			square.prepareRender()
			hydrantTex.bind()
			for (enemy in enemies) {
				shader[ShaderType.VERTEX_SHADER, 0] = cam.getMatrix().translate(enemy.x, enemy.y, 0f).scale(2f)
				square.drawTriangles()
			}
			square.unbind()
		}

		fun generateResources() {
			hydrantTex = Texture("fire extinguisher.png")
			sources = Array(20) { SoundSource(0f, 0f, 0f) }
			attackSounds = Array(4) {Sound("sounds/ext$it.wav")}
			damagedSounds = Array(3) {Sound("sounds/hit$it.wav")}
		}
	}
}
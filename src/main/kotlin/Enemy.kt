import engine.entities.Camera2D
import engine.entities.Orientation2D
import engine.opengl.Texture
import engine.opengl.bufferObjects.VAO
import engine.opengl.shaders.ShaderProgram
import engine.opengl.shaders.ShaderType
import org.joml.Vector2f

abstract class Enemy(x : Float, y : Float) : Orientation2D(0f, Vector2f(x, y)) {

	open var attackTimer = 2f

	fun update(dtime : Float, projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		attackTimer -= dtime
		if (attackTimer < 0) {
			attackTimer = 0.5f + Math.random().toFloat() / 4f
			shootProjectiles(projectileList, playerPos)
		}
	}

	abstract fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D)

	companion object {
		lateinit var hydrantTex : Texture

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
		}
	}
}
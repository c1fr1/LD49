import engine.opengl.Texture
import org.joml.Matrix4f
import org.joml.Vector2fc

interface Projectile : Vector2fc {
	val type : ProjectileType
	fun updatePosition(dtime : Float, player : Player) : Boolean

	fun transformMat(cam : Matrix4f) : Matrix4f
}

enum class ProjectileType {
	water;

	fun getTexture() : Texture {
		return when (this) {
			water -> waterTex
		}
	}

	companion object {
		private lateinit var waterTex : Texture
		fun generateResources() {
			waterTex = Texture("projectile.png")
		}
	}
}
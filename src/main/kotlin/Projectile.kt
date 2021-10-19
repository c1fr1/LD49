import engine.opengl.Texture
import org.joml.Matrix4f
import org.joml.Vector2fc

interface Projectile : Vector2fc {
	val type : ProjectileType

	var travelDistance : Float
	fun updatePosition(dtime : Float, player : Player) : Boolean

	fun transformMat(cam : Matrix4f) : Matrix4f
}

enum class ProjectileType {
	player,
	water,
	spray;

	fun getTexture() : Texture {
		return when (this) {
			player -> playerProjTex
			water -> waterTex
			spray -> sprayTex
		}
	}

	companion object {
		private lateinit var waterTex : Texture
		private lateinit var playerProjTex : Texture
		private lateinit var sprayTex : Texture
		fun generateResources() {
			waterTex = Texture("projectile.png")
			playerProjTex = Texture("playerShot.png")
			sprayTex = Texture("sprinkle.png")
		}
	}
}
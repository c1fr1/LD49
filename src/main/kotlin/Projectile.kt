import org.joml.Vector2fc

interface Projectile : Vector2fc {
	val type : ProjectileType
	fun updatePosition(player : Player)
}

enum class ProjectileType {
	water
}
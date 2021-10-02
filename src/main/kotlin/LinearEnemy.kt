import engine.entities.Orientation2D
import kotlin.math.cos
import kotlin.math.sin

class LinearEnemy(x : Float, y : Float) : Enemy(x, y) {
	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {

	}
}

class LinearProjectile(enemy : LinearEnemy) : Projectile, Orientation2D(enemy.rotation, enemy) {
	override val type : ProjectileType = ProjectileType.water
	override fun updatePosition(player : Player) : Boolean {
		x += cos(rotation)
		y += sin(rotation)
		return player.distance(this) < 5f
	}
}
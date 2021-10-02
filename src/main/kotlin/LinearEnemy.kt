import engine.entities.Orientation2D
import engine.opengl.jomlExtensions.minus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LinearEnemy(x : Float, y : Float) : Enemy(x, y) {
	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		val del = playerPos - this
		playerPos.rotation = atan2(del.y, del.x)
		projectileList.add(LinearProjectile(this))
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
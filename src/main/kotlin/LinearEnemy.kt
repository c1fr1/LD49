import engine.PIf
import engine.entities.Orientation2D
import engine.opengl.jomlExtensions.minus
import org.joml.Matrix4f
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LinearEnemy(x : Float, y : Float) : Enemy(x, y) {
	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		val del = playerPos - this
		rotation = atan2(del.y, del.x)
		projectileList.add(LinearProjectile(this))
	}
}

class LinearProjectile(enemy : LinearEnemy, val speed : Float = 25f) : Projectile, Orientation2D(enemy.rotation, enemy) {
	override val type : ProjectileType = ProjectileType.water
	override fun updatePosition(dtime : Float, player : Player) : Boolean {
		val distance = dtime * speed
		x += cos(rotation) * distance
		y += sin(rotation) * distance
		if (distance(player) < 2f) {
			player.hp -= 0.5f
			return true
		}
		return distance(player) > 200f
	}

	override fun transformMat(cam : Matrix4f) : Matrix4f = cam.translate(x, y, 0f).rotateZ(rotation + PIf / 2)
}
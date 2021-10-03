import engine.PIf
import engine.entities.Orientation2D
import org.joml.Math.abs
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.sign

class HydrantEnemy(x : Float, y : Float) : Enemy(x, y) {
	override val bounty = 30

	override fun update(dtime: Float, world: World, playerPos: Orientation2D) {
		super.update(dtime, world, playerPos)
		if (world.enemies.any { it.y < y }) {
			hp = 0.5f
		}
	}

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		attackTimer = 0.1f
		val dir = if (x > 0) {
			-50f
		} else {
			50f
		}
		projectileList.add(hydrantProjectile(dir, abs(x), this))
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = ty >= worldPos.y - 1
}

class hydrantProjectile(val vel : Float, val max : Float, enemy : Vector2f) : Projectile, Vector2f(enemy) {
	override val type : ProjectileType = ProjectileType.water
	override fun updatePosition(dtime : Float, player : Player): Boolean {
		x += vel * dtime
		if (player.y > y && abs(player.x - x) < 5f) {
			player.hp = -1f
			player.landHit()
			return true
		}
		return abs(x) > max
	}

	override fun transformMat(cam: Matrix4f) : Matrix4f = cam.translate(x, y, 0f).rotateZ( PIf / 2 * sign(vel))

}
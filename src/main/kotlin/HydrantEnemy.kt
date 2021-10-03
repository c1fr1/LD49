import engine.PIf
import engine.entities.Orientation2D
import org.joml.Math.abs
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i

class HydrantEnemy(x : Float, y : Float) : Enemy(x, y) {
	override val bounty = 30

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {

		val dir = if (x > 0) {
			-50f
		} else {
			50f
		}
		projectileList.add(hydrantProjectile(dir, abs(x)))
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = ty >= worldPos.y - 1
}

class hydrantProjectile(val vel : Float, val max : Float) : Projectile, Vector2f() {
	override val type: ProjectileType = ProjectileType.water
	override fun updatePosition(dtime : Float, player : Player): Boolean {
		x += vel * dtime
		if (player.y > y) {
			player.hp = -1f
			player.landHit()
			return true
		}
		return abs(x) > max
	}

	override fun transformMat(cam: Matrix4f): Matrix4f {
		TODO("Not yet implemented")
	}

}
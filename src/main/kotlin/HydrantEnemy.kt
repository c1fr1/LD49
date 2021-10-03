import engine.PIf
import engine.entities.Orientation2D
import org.joml.Vector2i
import kotlin.math.PI

class HydrantEnemy(x : Float, y : Float) : Enemy(x, y) {
	override val bounty = 30

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		if (x > 0) {
			rotation = -PIf
		}
		projectileList.add(LinearProjectile(this))
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = ty >= worldPos.y - 1
}
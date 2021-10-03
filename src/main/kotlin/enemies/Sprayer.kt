package enemies

import Projectile
import engine.PIf
import engine.entities.Orientation2D
import org.joml.Vector2i

class Sprayer(x : Float, y : Float) : Enemy(x, y) {
	override val bounty : Int = 35

	var baseRotation = 0f

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		//playSound(Enemy.sprinklerSounds.random(), 0.85f, Vector2f(0f, 0f), 1f + Math.random().toFloat() / 10f)

		projectileList.add(SprinklerProjectile(this, baseRotation, 50f))
		projectileList.add(SprinklerProjectile(this, baseRotation + PIf / 2f, 50f))
		projectileList.add(SprinklerProjectile(this, baseRotation + PIf, 50f))
		projectileList.add(SprinklerProjectile(this, baseRotation + 1.5f * PIf, 50f))
		baseRotation += PIf / 4f
		attackTimer = 0.25f
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = worldPos.distance(tx, ty) < 2.5f
}
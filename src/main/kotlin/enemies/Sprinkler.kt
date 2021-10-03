package enemies

import Projectile
import engine.compareAngles
import engine.entities.Orientation2D
import engine.opengl.jomlExtensions.minus
import engine.printMatrix
import org.joml.Math.random
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.atan2

class Sprinkler(x : Float, y : Float) : Enemy(x, y) {
	override val bounty : Int = 40

	var rotatingPos = random() > 0.5

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		playSound(sprinklerSounds.random(), 1f, Vector2f(0f, 0f), 1f + random().toFloat() / 10f)

		val del = playerPos - this
		val targRotation = atan2(del.y, del.x)
		if (compareAngles(targRotation, rotation) > 0) {
			rotation += 0.1f + random().toFloat() / 75f
		} else {
			rotation -= 0.1f + random().toFloat() / 75f
		}

		projectileList.add(LinearProjectile(this))
		attackTimer = 0.1f
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = worldPos.distance(tx, ty) < 2.5f
}
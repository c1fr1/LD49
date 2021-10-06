package enemies

import Player
import Projectile
import ProjectileType
import engine.PIf
import engine.TAUf
import engine.entities.Orientation2D
import engine.opengl.jomlExtensions.minus
import engine.opengl.jomlExtensions.times
import org.joml.Math.abs
import org.joml.Math.random
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Sprinkler(x : Float, y : Float) : Enemy(x, y, -PIf / 2) {
	override val bounty : Int = 40

	var rotatingPos = random() > 0.5

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		if (distance(playerPos) > 50f) {
			return
		}
		playSound(sprinklerSounds.random(), 0.85f, Vector2f(0f, 0f), 1f + random().toFloat() / 10f)

		val del = playerPos - this
		val targRotation = atan2(del.y, del.x)
		while (abs(rotation - targRotation) > PIf && rotation > targRotation) {
			rotation -= TAUf
		}
		while (abs(rotation - targRotation) > PIf && rotation < targRotation) {
			rotation += TAUf
		}

		if (rotatingPos && rotation - 0.5f > targRotation) {
			rotatingPos = !rotatingPos
		}
		if (!rotatingPos && rotation + 0.5f < targRotation) {
			rotatingPos = !rotatingPos
		}
		if (rotatingPos) {
			rotation += 0.1f + random().toFloat() / 75f
		} else {
			rotation -= 0.1f + random().toFloat() / 75f
		}

		projectileList.add(SprinklerProjectile(this))
		attackTimer = 0.15f
	}

	override fun protectsTile(worldPos : Vector2i, tx : Int, ty : Int) = worldPos.distance(tx, ty) < 2.5f
}

class SprinklerProjectile(enemy : Vector2f, rotation : Float, val speed : Float = 30f) : Projectile, Orientation2D(rotation, enemy) {

	val sourcePos = enemy

	constructor(enemy : Orientation2D, speed : Float = 30f) : this(enemy, enemy.rotation, speed)
	override val type : ProjectileType = ProjectileType.spray
	override fun updatePosition(dtime : Float, player : Player) : Boolean {
		val distance = dtime * speed
		val vel = Vector2f(cos(rotation), sin(rotation))
		add(vel * distance)
		if (distance(player) < 2f) {
			player.hp -= 0.2f
			player.landHit()
			ParticleManager.requestParticles(this, vel * speed / 2f)
			return true
		}
		return sourcePos.distance(this) > 50f
	}

	override fun transformMat(cam : Matrix4f) : Matrix4f = cam.translate(x, y, 0f)
		.rotateZ(rotation + PIf / 2)
		.scale(1f, 2f, 1f)
}
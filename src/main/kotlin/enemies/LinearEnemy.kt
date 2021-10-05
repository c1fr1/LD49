package enemies

import ParticleManager
import Player
import Projectile
import ProjectileType
import engine.PIf
import engine.entities.Orientation2D
import engine.opengl.jomlExtensions.minus
import engine.opengl.jomlExtensions.times
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LinearEnemy(x : Float, y : Float) : Enemy(x, y) {

	override val bounty : Int = 10

	override fun shootProjectiles(projectileList : ArrayList<Projectile>, playerPos : Orientation2D) {
		attackTimer = 1.75f + Math.random().toFloat() / 4f
		val del = playerPos - this
		rotation = atan2(del.y, del.x)
		projectileList.add(LinearProjectile(this))
		playSound(attackSounds.random(), 0.1f, playerPos)
	}

	override fun protectsTile(worldPos: Vector2i, tx: Int, ty: Int): Boolean {
		return worldPos.distance(tx, ty) < 2.5
	}
}

class LinearProjectile(enemy : Vector2f, rotation : Float, val speed : Float = 40f) : Projectile, Orientation2D(rotation, enemy) {
	constructor(enemy : Orientation2D, speed : Float = 40f) : this(enemy, enemy.rotation, speed)
	override val type : ProjectileType = ProjectileType.water
	override fun updatePosition(dtime : Float, player : Player) : Boolean {
		val distance = dtime * speed
		val vel = Vector2f(cos(rotation), sin(rotation))
		add(vel * distance)
		if (distance(player) < 2f) {
			player.hp -= 0.5f
			player.landHit()
			ParticleManager.requestParticles(this, vel * speed / 2f)
			return true
		}
		return distance(player) > 200f
	}

	override fun transformMat(cam : Matrix4f) : Matrix4f = cam.translate(x, y, 0f).rotateZ(rotation + PIf / 2).scale(1.5f)
}
import engine.opengl.Texture
import org.joml.Vector2f

class Enemy : Vector2f() {
	companion object {
		lateinit var hydrantTex : Texture
		fun generateResources() {
			hydrantTex = Texture("fire extinguisher.png")
		}
	}
}
import engine.opengl.EnigContext
import engine.opengl.EnigWindow
import engine.opengl.GLContextPreset

fun main() {
	EnigContext.init()
	val window = EnigWindow("LD49", GLContextPreset.standard2D)
	val view = GameView(window)

	view.runInGLSafe(window)
	/*while (view.nextView != -1) {
	}*/

	EnigContext.terminate()
}
import engine.opengl.EnigContext
import engine.opengl.EnigWindow
import engine.opengl.GLContextPreset

fun main() {
	EnigContext.init()
	val window = EnigWindow("LD49", GLContextPreset.standard2D)
	val view = MainMenu(window)

	val gameView = GameView(window)
	while (view.nextView != -1) {
		view.runInGLSafe(window)
		if (view.nextView == 1) {
			gameView.runInGLSafe(window)
		}
	}

	EnigContext.terminate()
}
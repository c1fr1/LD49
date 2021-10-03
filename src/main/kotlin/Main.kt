import engine.opengl.EnigContext
import engine.opengl.EnigWindow
import engine.opengl.GLContextPreset

fun main() {
	EnigContext.init()
	val window = EnigWindow("LD49", GLContextPreset.standard2D)
	val view = MainMenu(window)

	while (view.nextView != -1) {
		view.runInGLSafe(window)
	}

	EnigContext.terminate()
}
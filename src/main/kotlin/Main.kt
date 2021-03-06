import engine.opengl.EnigContext
import engine.opengl.EnigWindow
import engine.opengl.GLContextPreset
import views.GameView
import views.MainMenu
import views.SettingsView

fun main() {
	EnigContext.init()
	val window = EnigWindow("LD49", GLContextPreset.standard2D)
	val view = MainMenu(window)

	val gameView = GameView(window)
	val settingsView = SettingsView(window)

	while (view.nextView != -1) {
		view.runInGLSafe(window)
		window.inputHandler.update()
		if (view.nextView == 1) {
			gameView.tutorialManager.step = -1
			gameView.runInGLSafe(window)
			window.inputHandler.update()
		} else if (view.nextView == 2) {
			gameView.tutorialManager.step = 0
			gameView.runInGLSafe(window)
			window.inputHandler.update()
		} else if (view.nextView == 3) {
			settingsView.runInGLSafe(window)
			window.inputHandler.update()
		}
	}

	EnigContext.terminate()
}
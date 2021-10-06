package views

import engine.opengl.*
import engine.opengl.bufferObjects.FBO
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT

class MainMenu(w : EnigWindow) : MenuView(w) {

	var nextView = 0

	val playButton = Button(0.5f, 2.3f, "PLAY")
	val tutorialButton = Button(0.1f, 4.75f, "TUTORIAL")
	val settingsButton = Button(-0.3f, 4.75f, "SETTINGS")
	val quitButton = Button(-0.7f, 2.75f, "QUIT")

	override fun loop(frameBirth: Long, dtime: Float): Boolean {
		FBO.prepareDefaultRender()

		renderButton(playButton, dtime)
		renderButton(tutorialButton, dtime)
		renderButton(settingsButton, dtime)
		renderButton(quitButton, dtime)

		time += dtime

		if (window.inputHandler.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == KeyState.Released) {
			if (playButton.hovering(window)) {
				nextView = 1
				return true
			}
			if (tutorialButton.hovering(window)) {
				nextView = 2
				return true
			}
			if (settingsButton.hovering(window)) {
				nextView = 3
				return true
			}
			if (quitButton.hovering(window)) {
				nextView = -1
				return true
			}
		}
		nextView = -1

		return window.inputHandler.keys[GLFW_KEY_ESCAPE] == KeyState.Released
	}
}
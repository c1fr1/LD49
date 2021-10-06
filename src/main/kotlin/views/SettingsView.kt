package views

import PersistentSettings
import PlayerColor
import engine.opengl.EnigWindow
import engine.opengl.KeyState
import engine.opengl.bufferObjects.FBO
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT

class SettingsView(w : EnigWindow) : MenuView(w) {

	init {
		PersistentSettings.load()
	}

	val colorButton = Button(0.5f, 8.65f, "PLAYER STYLE: ${PersistentSettings.playerColor.displayName}")
	val backButton = Button(-0.8f, 2.6f, "BACK")

	override fun loop(frameBirth : Long, dtime : Float) : Boolean {
		FBO.prepareDefaultRender()
		time += dtime
		renderButton(colorButton, dtime)
		renderButton(backButton, dtime)
		if (window.inputHandler.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == KeyState.Released) {
			if (colorButton.hovering(window)) {
				PersistentSettings.playerColor =
					PlayerColor.values()[(PersistentSettings.playerColor.ordinal + 1) % PlayerColor.values().size]
				colorButton.text = "PLAYER STYLE: ${PersistentSettings.playerColor.displayName}"
				PersistentSettings.save()
			}
			if (backButton.hovering(window)) {
				return true
			}
		}
		return window.inputHandler.keys[GLFW.GLFW_KEY_ESCAPE] == KeyState.Released
	}
}

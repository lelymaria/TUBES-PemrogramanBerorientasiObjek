package com.tubesgame.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.tubesgame.game.WormScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		WormScreen myProgram = new WormScreen();
		LwjglApplication launcher = new LwjglApplication(
				myProgram );
	}
}

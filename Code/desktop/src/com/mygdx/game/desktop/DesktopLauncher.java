package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MultiplayerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.width = MultiplayerGame.WIDTH;
		config.height = MultiplayerGame.HEIGHT;
		config.resizable = false;
		config.title = "8-Bit Arena";
		config.addIcon("windowsIcon.png", Files.FileType.Internal);
		new LwjglApplication(new MultiplayerGame(), config);
	}
}

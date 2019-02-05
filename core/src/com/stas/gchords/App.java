package com.stas.gchords;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;

public class App extends Game {
	private static App instance = null;

	public static void setAppScreen(final Screen screen) {
		Timer.schedule(new Timer.Task(){
			@Override
			public void run() {
				instance.getScreen().dispose();
				instance.setScreen(screen);
			}
		}, 0);
	}

	@Override
	public void create() {
		instance = this;
		Resources.init();
		setScreen(new PlayIntervalTesterScreen());
	}
}

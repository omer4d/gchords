package com.stas.gchords;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import com.stas.gchords.screens.MainScreen;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class App extends Game {
	private static App instance = null;

	MidiDevice midiInDevice;

	public static App getInstance() {
		return instance;
	}

	public void setMidiInDevice(MidiDevice.Info info) {
		if(midiInDevice != null) {
			midiInDevice.close();
		}
		try {
			midiInDevice = MidiSystem.getMidiDevice(info);
			midiInDevice.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public MidiDevice getMidiInDevice() {
		return midiInDevice;
	}

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
		setScreen(new MainScreen());
	}

	public void dispose() {
		if(midiInDevice != null) {
			midiInDevice.close();
		}
		Resources.dispose();
	}
}

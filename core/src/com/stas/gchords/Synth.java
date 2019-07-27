package com.stas.gchords;


import com.badlogic.gdx.utils.Timer;

public class Synth {
    private InstrumentSample[] instrumentSamples;
    private int lastPlayed = 0;

    public Synth(InstrumentSample[] instrumentSamples) {
        this.instrumentSamples = instrumentSamples;
    }

    public void stopAll() {
        for(int i = 0; i < instrumentSamples.length; ++i) {
            if(instrumentSamples[i] != null) {
                instrumentSamples[i].sound.stop();
            }
        }
    }

    public void noteOn(int[] notes) {
        for(int i = 0; i < notes.length; ++i) {
            noteOn(notes[i]);
        }
    }

    public void noteOn(int idx) {
        //instrumentSamples[lastPlayed].sound.stop();
        instrumentSamples[idx].play();
        lastPlayed = idx;
    }

    public void noteOn(int idx, int vel) {
        instrumentSamples[lastPlayed].sound.stop();
        instrumentSamples[idx].play(vel / 127.f);
        lastPlayed = idx;
    }

    public void noteOn(final int idx, final int vel, float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                noteOn(idx, vel);
            }
        }, delay);
    }
}

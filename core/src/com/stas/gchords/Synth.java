package com.stas.gchords;


import com.badlogic.gdx.utils.Timer;

public class Synth {
    private InstrumentSample[] instrumentSamples;
    private int lastPlayed = 0;

    public Synth(InstrumentSample[] instrumentSamples) {
        this.instrumentSamples = instrumentSamples;
    }

    public void noteOn(int idx) {
        instrumentSamples[lastPlayed].sound.stop();
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

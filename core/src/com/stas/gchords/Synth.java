package com.stas.gchords;

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
}

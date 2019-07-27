package com.stas.gchords;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public abstract class MidiNoteTracker {
    private boolean[] noteOn = new boolean[128];

    public void onMessage(MidiMessage midiMessage) {
        final byte[] data = midiMessage.getMessage();

        if(midiMessage.getStatus() == ShortMessage.NOTE_ON && data[2] == 0 ||
                midiMessage.getStatus() == ShortMessage.NOTE_OFF) {
            noteOn[data[1]] = false;
            onChange(heldNotes());
        }

        if(midiMessage.getStatus() == ShortMessage.NOTE_ON && data[2] > 0) {
            noteOn[data[1]] = true;
            onChange(heldNotes());
        }
    }

    public int[] heldNotes() {
        int[] notes = new int[noteCount()];
        int count = 0;

        for(int i = 0; i < noteOn.length; ++i) {
            if(noteOn[i]) {
                notes[count++] = i;
            }
        }

        return notes;
    }

    public int noteCount() {
        int count = 0;

        for(int i = 0; i < noteOn.length; ++i) {
            if(noteOn[i]) {
                ++count;
            }
        }

        return count;
    }

    public abstract void onChange(int[] heldNotes);
}

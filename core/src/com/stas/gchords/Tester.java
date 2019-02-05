package com.stas.gchords;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class Tester implements Receiver {
    private Synth synth;
    private int prev;
    private int expected;
    private int total, correct;
    private Interval[] intervalEnabled;
    private Listener listener;

    public interface Listener {
        void onUpdateTesterStats(int correct, int total);
    }

    public Tester(Synth synth) {
        this.synth = synth;
        this.intervalEnabled = new Interval[Interval.values().length];

        prev = 60;
        expected = 60;
    }

    public void reset() {
        prev = 60;
        expected = 60;
        total = 0;
        correct = 0;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        listener.onUpdateTesterStats(correct, total);
    }

    public void setIntervalEnabled(Interval interval, boolean flag) {
        intervalEnabled[interval.ordinal()] = flag ? interval : null;
    }

    public void next() {
        prev = expected;

        Array<Integer> ivals = new Array<Integer>();
        for(int i = 0; i < intervalEnabled.length; ++i) {
            if(intervalEnabled[i] != null) {
                ivals.add(intervalEnabled[i].semitones);
            }
        }

        if(ivals.size > 0) {
            int e1 = expected;
            do {
                expected = e1 + ivals.random() * Util.randSign();
            } while (expected < 36 || expected > 84);

            synth.noteOn(expected);
        }
    }

    public void replay() {
        synth.noteOn(expected);
    }

    public int getTotal() {
        return total;
    }

    public int getCorrect() {
        return correct;
    }

    public int getExpected() {
        return expected;
    }

    public int getPrevious() {
        return prev;
    }

    @Override
    public void send(final MidiMessage midiMessage, long l) {
        final byte[] data = midiMessage.getMessage();

        System.out.println(midiMessage.getStatus());

        //if(midiMessage.getStatus() == ShortMessage.NOTE_ON && data[2] > 0) {
        //    synth.noteOn(data[1], 40);
        //}

        if(midiMessage.getStatus() == 153) {
            synth.noteOn(prev);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    synth.noteOn(expected);
                }
            }, 0.5f);

        }


        if(midiMessage.getStatus() == ShortMessage.NOTE_ON && data[2] == 0 || midiMessage.getStatus() == ShortMessage.NOTE_OFF) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if(data[1] == expected) {
                        System.out.println("Correct!");
                        ++correct;
                        next();
                    }else {
                        System.out.println("Wrong!");
                        replay();
                    }

                    ++total;

                    if(listener != null) {
                        listener.onUpdateTesterStats(correct, total);
                    }
                }
            }, 0.3f);




//            System.out.println("Note on! " +
//                    data[1] + ", " +
//                    data[2]);
        }





    }

    @Override
    public void close() {
    }
}

package com.stas.gchords.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.stas.gchords.App;
import com.stas.gchords.Consts;
import com.stas.gchords.MidiNoteTracker;
import com.stas.gchords.Resources;
import com.stas.gchords.Synth;
import com.stas.gchords.Util;
import com.stas.gchords.actors.EnumSelector;
import com.stas.gchords.actors.PianoKeyboard;

import java.util.Arrays;
import java.util.function.Function;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public class PlayChordScreen extends AStageScreen implements Receiver {
    private interface M {
        void apply();
    }

    private enum Inversion {
        FirstInversion(0),
        SecondInversion(1),
        ThirdInversion(2);

        public final int shifts;

        Inversion(int shifts) {
            this.shifts = shifts;
        }
    }

    private enum Chord {
        Min(0, 3, 7),
        Maj(0, 4, 7);

        public final int[] notes;

        Chord(int... notes) {
            this.notes = notes;
        }
    }

    private Synth synth;
    private PianoKeyboard pianoKeyboard;
    private MidiNoteTracker noteTracker;
    private Label stats;
    private EnumSelector<Inversion> invSelector;
    private EnumSelector<Chord> chordSelector;

    private boolean ignoreInput;
    private int total, correct;

    private int root;
    private int[] expectedNotes;

    private void refreshView() {
        double ratio = total == 0 ? 1 : (double)correct / total;
        stats.setText(String.format("%d/%d (%.2f%%)",  correct, total, ratio*100));

        pianoKeyboard.clearAllKeyMarks();

        for (int i = 0; i < expectedNotes.length; ++i) {
            if((expectedNotes[i] - root) % 12 == 0) {
                pianoKeyboard.setKeyMarked(expectedNotes[i], true);
            }
        }
    }

    private static void rot(int[] arr) {
        int tmp = arr[0];
        for(int i = 0; i < arr.length - 1; ++i) {
            arr[i] = arr[i + 1];
        }
        arr[arr.length - 1] = tmp;
    }

    private static int[] buildChord(int root, Chord chord, Inversion inversion) {
        int[] notes = new int[chord.notes.length];

        for(int i = 0; i < notes.length; ++i) {
            notes[i] = chord.notes[i] + root;
        }

        for(int i = 0; i < inversion.shifts; ++i) {
            rot(notes);
            notes[notes.length - 1] += 12;
        }

        return notes;
    }

    private boolean notesInRange(int[] notes) {
        for(int i = 0; i < notes.length; ++i) {
            if(!pianoKeyboard.inRange(notes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;

        synth = new Synth(Resources.pianoSamples);
        total = 0;
        correct = 0;

        try {
            App.getInstance().getMidiInDevice().getTransmitter().setReceiver(this);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        stats = new Label("0", skin);
        stats.setPosition(400, 400);
        stage.addActor(stats);

        pianoKeyboard = new PianoKeyboard(
                new TextureRegion(Resources.pianoWhiteKey),
                new TextureRegion(Resources.pianoBlackKey),
                new TextureRegion(Resources.rect),
                36, 84);

        pianoKeyboard.setPosition(200, 450);
        stage.addActor(pianoKeyboard);

        invSelector = new EnumSelector(skin, Inversion.values());
        invSelector.setPosition(20, 300);
        stage.addActor(invSelector);

        chordSelector = new EnumSelector(skin, Chord.values());
        chordSelector.setPosition(170, 300);
        stage.addActor(chordSelector);

        invSelector.setSelected(Inversion.FirstInversion, true);
        chordSelector.setSelected(Chord.Maj, true);


        TextButton back = new TextButton("Back", skin);
        back.setPosition(Consts.IDEAL_SCR_W - 200, 100);
        back.addListener(new ClickListener() {
           public void clicked(InputEvent e, float x, float y) {
               App.setAppScreen(new MainScreen());
           }
        });

        noteTracker = new MidiNoteTracker() {
            @Override
            public void onChange(int[] heldNotes) {
                if(heldNotes.length == 0) {
                    ignoreInput = false;
                }

                if(!ignoreInput && heldNotes.length >= expectedNotes.length) {
                    handleAnswer(heldNotes);
                    ignoreInput = true;
                }
            }
        };

        stage.addActor(back);
        next();

        refreshView();
    }




    private void reset() {
        total = 0;
        correct = 0;

        refreshView();
    }

    private void next() {
        if(invSelector.getSelectionCount() > 0 && chordSelector.getSelectionCount() > 0) {
            //root = Util.randi(pianoKeyboard.startKey, pianoKeyboard.endKey + 1);
            //int e1 = expected;

            do {
                root = Util.randi(60, 72);
                Inversion inv = invSelector.getRandomSelection();
                Chord chord = chordSelector.getRandomSelection();
                expectedNotes = buildChord(root, chord, inv);
            } while (!notesInRange(expectedNotes));

            synth.stopAll();
            synth.noteOn(expectedNotes);
        }
    }

    private void replay() {
        synth.noteOn(expectedNotes);
    }

    private static boolean notesEqual(int[] notes1, int[] notes2) {
        if(notes1.length == notes2.length) {
            Arrays.sort(notes1);
            Arrays.sort(notes2);
            for(int i = 0; i < notes1.length; ++i) {
                if(notes1[i] != notes2[i]) {
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }

    private void handleAnswer(final int[] answer) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(notesEqual(answer, expectedNotes)) {
                    System.out.println("Correct!");
                    ++correct;
                    next();
                }else {
                    System.out.println("Wrong!");
                    replay();
                }

                ++total;

                refreshView();
            }
        }, 0.3f);
    }

    @Override
    public void send(final MidiMessage midiMessage, long l) {
        final byte[] data = midiMessage.getMessage();
        System.out.println(midiMessage.getStatus());
        noteTracker.onMessage(midiMessage);
    }

    @Override
    public void close() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}

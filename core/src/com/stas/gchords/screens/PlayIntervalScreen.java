package com.stas.gchords.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.stas.gchords.App;
import com.stas.gchords.Consts;
import com.stas.gchords.Interval;
import com.stas.gchords.Util;
import com.stas.gchords.actors.IntervalSelector;
import com.stas.gchords.actors.PianoKeyboard;
import com.stas.gchords.Resources;
import com.stas.gchords.Synth;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class PlayIntervalScreen extends AStageScreen implements Receiver {
    private Synth synth;
    private PianoKeyboard pianoKeyboard;
    private Label stats;
    private IntervalSelector intervalSelector;

    private int prev;
    private int expected;
    private int total, correct;

    private void refreshView() {
        double ratio = total == 0 ? 1 : (double)correct / total;
        stats.setText(String.format("%d/%d (%.2f%%)",  correct, total, ratio*100));
        pianoKeyboard.clearAllKeyMarks();
        pianoKeyboard.setKeyMarked(prev, true);
    }

    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;

        synth = new Synth(Resources.pianoSamples);
        prev = 60;
        expected = 60;
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
        pianoKeyboard.setKeyMarked(prev, true);
        stage.addActor(pianoKeyboard);

        intervalSelector = new IntervalSelector(skin);
        intervalSelector.setPosition(20, 300);
        stage.addActor(intervalSelector);

        TextButton back = new TextButton("Back", skin);
        back.setPosition(Consts.IDEAL_SCR_W - 200, 100);
        back.addListener(new ClickListener() {
           public void clicked(InputEvent e, float x, float y) {
               App.setAppScreen(new MainScreen());
           }
        });

        stage.addActor(back);

        refreshView();
    }




    private void reset() {
        prev = 60;
        expected = 60;
        total = 0;
        correct = 0;

        refreshView();
    }

    private void next() {
        prev = expected;

        if(intervalSelector.getSelectionCount() > 0) {
            int e1 = expected;
            do {
                expected = e1 + intervalSelector.getRandomSelection().semitones * Util.randSign();
            } while (expected < 36 || expected > 84);

            synth.stopAll();
            synth.noteOn(expected);
        }
    }

    private void replay() {
        synth.noteOn(expected);
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

                    refreshView();
                }
            }, 0.3f);
        }
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

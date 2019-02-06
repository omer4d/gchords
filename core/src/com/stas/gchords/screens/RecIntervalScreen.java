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
import com.stas.gchords.Interval;
import com.stas.gchords.Resources;
import com.stas.gchords.Synth;
import com.stas.gchords.Util;
import com.stas.gchords.actors.IntervalSelector;
import com.stas.gchords.actors.PianoKeyboard;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class RecIntervalScreen extends AStageScreen {
    private Synth synth;
    private Label stats;
    private IntervalSelector intervalSelector;

    private int intervalNote1, intervalNote2;
    private Interval expected;
    private int total, correct;

    private void refreshView() {
        double ratio = total == 0 ? 1 : (double)correct / total;
        stats.setText(String.format("%d/%d (%.2f%%)",  correct, total, ratio*100));
    }

    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;

        synth = new Synth(Resources.pianoSamples);

        stats = new Label("0", skin);
        stats.setPosition(400, 700);
        stage.addActor(stats);

        intervalSelector = new IntervalSelector(skin);

        final Interval[] intervals = {
                Interval.Min2nd,
                Interval.Min3rd,
                Interval.Min6th,
                Interval.Min7th,

                Interval.Maj2nd,
                Interval.Maj3rd,
                Interval.Maj6th,
                Interval.Maj7th,

                Interval.Fourth,
                Interval.Fifth,

                Interval.Octave,
                Interval.Dim5th,
        };

        for(int i = 0; i < intervals.length; ++i) {
            intervalSelector.setIntervalSelected(intervals[i], true);
        }

        intervalSelector.setPosition(20, 300);
        stage.addActor(intervalSelector);

        String[] intervalNames = {
                "m2",
                "M2",
                "m3",
                "M3",
                "P4",
                "dim5",
                "P5",
                "m6",
                "M6",
                "m7",
                "M7",
                "oct"
        };

        boolean[][] tbl = {
                {true, true, true, true},
                {true, true, true, true},
                {true, true, false, true},
                {false, false, false, true},
        };

        int counter = 0;

        for(int i = 0; i < tbl.length; ++i) {
            for(int j = 0; j < tbl[i].length; ++j) {
                if(tbl[i][j]) {

                    TextButton btn = new TextButton(intervalNames[intervals[counter].ordinal()], skin);
                    btn.setPosition(250 + i * 120, 600 - j * 45);

                    final int finalCounter = counter;
                    btn.addListener(new ClickListener() {
                        public void clicked(InputEvent e, float x, float y) {
                            processAnswer(intervals[finalCounter]);
                        }
                    });

                    btn.setWidth(100);
                    btn.setHeight(40);
                    stage.addActor(btn);
                    ++counter;
                }
            }
        }


        TextButton back = new TextButton("Back", skin);
        back.setPosition(Consts.IDEAL_SCR_W - 200, 100);
        back.addListener(new ClickListener() {
           public void clicked(InputEvent e, float x, float y) {
               App.setAppScreen(new MainScreen());
           }
        });
        stage.addActor(back);

        final TextButton replay = new TextButton("Replay", skin);
        replay.setPosition(Consts.IDEAL_SCR_W - 400, 700);
        replay.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                replay();
            }
        });
        stage.addActor(replay);



        expected = intervalSelector.getRandomSelection();
        total = 0;
        correct = 0;
        intervalNote1 = 60;
        intervalNote2 = 60;

        next();
        replay();

        refreshView();
    }


    private void processAnswer(Interval answer) {
        if(answer == expected) {
            next();
            ++correct;
        }else {
            replay();
        }

        ++total;
        refreshView();
    }


    private void reset() {
        expected = null;
        total = 0;
        correct = 0;
        intervalNote1 = 60;

        refreshView();
    }

    private void next() {
        intervalNote1 = intervalNote2;

        do {
            expected = intervalSelector.getRandomSelection();
            intervalNote2 = intervalNote1 + expected.semitones * Util.randSign();
        }while(intervalNote2 < 36 || intervalNote2 > 96);

        synth.noteOn(intervalNote2, 127, 0.3f);
        //replay();
    }

    private void replay() {
        synth.noteOn(intervalNote1, 127);
        synth.noteOn(intervalNote2, 127, 0.3f);
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

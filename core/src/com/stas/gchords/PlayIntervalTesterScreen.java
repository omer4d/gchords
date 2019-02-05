package com.stas.gchords;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class PlayIntervalTesterScreen extends AStageScreen {
    MidiDevice midiInDevice;

    Synth synth;
    PlayIntervalTester tester;
    PianoKeyboard pianoKeyboard;
    Label stats;

    private static Array<MidiDevice.Info> getMidiInDevices() {
        Array<MidiDevice.Info> inDevs = new Array<MidiDevice.Info>();
        MidiDevice.Info[] devInfos = MidiSystem.getMidiDeviceInfo();

        for(int i = 0; i < devInfos.length; ++i) {
            try {
                MidiDevice dev = MidiSystem.getMidiDevice(devInfos[i]);
                if(dev.getMaxTransmitters() != 0) {
                    inDevs.add(devInfos[i]);
                }
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

        return inDevs;
    }

    private void setMidiInDevice(MidiDevice.Info info) {
        if(midiInDevice != null) {
            midiInDevice.close();
        }
        try {
            midiInDevice = MidiSystem.getMidiDevice(info);
            midiInDevice.open();
            midiInDevice.getTransmitter().setReceiver(tester);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        tester.reset();
        tester.replay();
        refreshView();
    }

    private void refreshView() {
        int correct = tester.getCorrect();
        int total = tester.getTotal();
        double ratio = total == 0 ? 1 : (double)correct / total;
        stats.setText(String.format("%d/%d (%.2f%%)",  correct, total, ratio*100));
        pianoKeyboard.clearAllKeyMarks();
        pianoKeyboard.setKeyMarked(tester.getPrevious(), true);
    }


    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;

        synth = new Synth("samples");
        tester = new PlayIntervalTester(synth);

        stats = new Label("0", skin);
        stats.setPosition(400, 400);
        stage.addActor(stats);


        pianoKeyboard = new PianoKeyboard(
                new TextureRegion(Resources.pianoWhiteKey),
                new TextureRegion(Resources.pianoBlackKey),
                new TextureRegion(Resources.rect),
                36, 84);

        pianoKeyboard.setPosition(200, 450);
        pianoKeyboard.setKeyMarked(tester.getPrevious(), true);
        stage.addActor(pianoKeyboard);




        final SelectBox<MidiDevice.Info> selectBox = new SelectBox<MidiDevice.Info>(skin);
        selectBox.setItems(getMidiInDevices());
        selectBox.setWidth(200);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setMidiInDevice(selectBox.getSelected());
            }
        });
        setMidiInDevice(selectBox.getSelected());


        final Interval[] intervals = Interval.values();

        for(int i = 0; i < intervals.length; ++i) {
            final CheckBox cb = new CheckBox(intervals[i].name(), skin);
            cb.setPosition(100, 300 - i * 20);

            final int finalI = i;
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tester.setIntervalEnabled(intervals[finalI], cb.isChecked());
                }
            });

            stage.addActor(cb);
        }

        tester.setListener(new PlayIntervalTester.Listener() {
            @Override
            public void onUpdateTesterStats(int correct, int total) {
                refreshView();
            }
        });

        stage.addActor(selectBox);
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

    public void dispose() {
        if(midiInDevice != null) {
            midiInDevice.close();
        }

        super.dispose();
    }
}

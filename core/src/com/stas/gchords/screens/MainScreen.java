package com.stas.gchords.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.stas.gchords.App;
import com.stas.gchords.Consts;
import com.stas.gchords.Resources;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public class MainScreen extends AStageScreen {
    Label midiDataLabel;

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

    private void hookupMidiDataTracker() {
        try {
            App.getInstance().getMidiInDevice().getTransmitter().setReceiver(new Receiver() {
                @Override
                public void send(MidiMessage midiMessage, long l) {
                    byte[] msg = midiMessage.getMessage();
                    midiDataLabel.setText(String.format("%x(%d): %x %d %d",
                            midiMessage.getStatus(), midiMessage.getStatus(), msg[0], msg[1], msg[2]));
                }

                @Override
                public void close() {

                }
            });
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;

        VerticalGroup menu = new VerticalGroup();
        menu.setBounds(0, 0, Consts.IDEAL_SCR_W, Consts.IDEAL_SCR_H);
        menu.space(5);
        menu.center();
        stage.addActor(menu);

        TextButton playIntervalTesterScreenBtn = new TextButton("play intervals", skin);
        playIntervalTesterScreenBtn.addListener(new ClickListener() {
           public void clicked(InputEvent e, float x, float y) {
               App.setAppScreen(new PlayIntervalScreen());
           }
        });
        menu.addActor(playIntervalTesterScreenBtn);

        Label selectBoxTitle = new Label("MIDI in:", skin);
        menu.addActor(selectBoxTitle);

        final SelectBox<MidiDevice.Info> selectBox = new SelectBox<MidiDevice.Info>(skin);
        selectBox.setItems(getMidiInDevices());
        selectBox.setWidth(300);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.getInstance().setMidiInDevice(selectBox.getSelected());
                hookupMidiDataTracker();
            }
        });

        if(App.getInstance().getMidiInDevice() == null) {
            App.getInstance().setMidiInDevice(selectBox.getSelected());
        }else{
            selectBox.setSelected(App.getInstance().getMidiInDevice().getDeviceInfo());
        }
        hookupMidiDataTracker();

        menu.addActor(selectBox);

        Label midiDataTitleLabel = new Label("MIDI in data:", skin);
        menu.addActor(midiDataTitleLabel);

        midiDataLabel = new Label("---", skin);
        menu.addActor(midiDataLabel);
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

package com.stas.gchords.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stas.gchords.App;
import com.stas.gchords.Consts;
import com.stas.gchords.Interval;
import com.stas.gchords.Resources;
import com.stas.gchords.Synth;

public class RecIntervalScreen extends AStageScreen {
    private Synth synth;
    private Label stats;
    private Interval[] intervalEnabled;

    int correct;
    int total;

    private void refreshView() {
        double ratio = total == 0 ? 1 : (double)correct / total;
        stats.setText(String.format("%d/%d (%.2f%%)",  correct, total, ratio*100));
    }

    public void setIntervalEnabled(Interval interval, boolean flag) {
        intervalEnabled[interval.ordinal()] = flag ? interval : null;
    }

    @Override
    void init(Stage stage) {
        Skin skin = Resources.skin;
//
//        synth = new Synth(Resources.pianoSamples);
//
//        stats = new Label("0", skin);
//        stats.setPosition(400, 400);
//        stage.addActor(stats);
//
//        final Interval[] intervals = Interval.values();
//
//        for(int i = 0; i < intervals.length; ++i) {
//            final CheckBox cb = new CheckBox(intervals[i].name(), skin);
//            cb.setPosition(100, 300 - i * 20);
//
//            final int finalI = i;
//            cb.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    tester.setIntervalEnabled(intervals[finalI], cb.isChecked());
//                }
//            });
//
//            stage.addActor(cb);
//        }
//
//        tester.setListener(new PlayIntervalTester.Listener() {
//            @Override
//            public void onUpdateTesterStats(int correct, int total) {
//                refreshView();
//            }
//        });
//
        TextButton back = new TextButton("Back", skin);
        back.setPosition(Consts.IDEAL_SCR_W - 200, 100);
        back.addListener(new ClickListener() {
           public void clicked(InputEvent e, float x, float y) {
               App.setAppScreen(new MainScreen());
           }
        });
        stage.addActor(back);
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

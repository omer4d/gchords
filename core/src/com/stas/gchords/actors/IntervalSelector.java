package com.stas.gchords.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.stas.gchords.Interval;

public class IntervalSelector extends Group {
    private Interval[] intervalSelected;
    CheckBox[] checkBoxes;

    private void setIntervalSelectedHelper(Interval interval, boolean flag) {
        intervalSelected[interval.ordinal()] = flag ? interval : null;
    }

    public void setIntervalSelected(Interval interval, boolean flag) {
        setIntervalSelectedHelper(interval, flag);
        checkBoxes[interval.ordinal()].setChecked(flag);
    }

    public IntervalSelector(Skin skin) {
        final Interval[] intervals = Interval.values();
        intervalSelected = new Interval[intervals.length];
        checkBoxes = new CheckBox[intervals.length];

        for(int i = 0; i < intervals.length; ++i) {
            final CheckBox cb = new CheckBox(intervals[i].name(), skin);
            cb.setPosition(0, (intervals.length - i) * 20);

            final int finalI = i;
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    setIntervalSelectedHelper(intervals[finalI], cb.isChecked());
                }
            });

            checkBoxes[i] = cb;
            this.addActor(cb);
        }
    }

    private Array<Interval> getSelections() {
        Array<Interval> ivals = new Array<Interval>();

        for(int i = 0; i < intervalSelected.length; ++i) {
            if(intervalSelected[i] != null) {
                ivals.add(intervalSelected[i]);
            }
        }

        return ivals;
    }

    public int getSelectionCount() {
        return getSelections().size;
    }

    public Interval getRandomSelection() {
        return getSelections().random();
    }
}

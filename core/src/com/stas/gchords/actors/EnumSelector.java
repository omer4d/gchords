package com.stas.gchords.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class EnumSelector<E extends Enum> extends Group {
    private Object[] selected;
    CheckBox[] checkBoxes;

    private void setSelectedHelper(E e, boolean flag) {
        selected[e.ordinal()] = flag ? e : null;
    }

    public void setSelected(E e, boolean flag) {
        setSelectedHelper(e, flag);
        checkBoxes[e.ordinal()].setChecked(flag);
    }

    public EnumSelector(Skin skin, final E[] values) {
        selected = new Object[values.length];
        checkBoxes = new CheckBox[values.length];

        for(int i = 0; i < values.length; ++i) {
            final CheckBox cb = new CheckBox(values[i].name(), skin);
            cb.setPosition(0, (values.length - i) * 20);

            final int finalI = i;
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    setSelectedHelper(values[finalI], cb.isChecked());
                }
            });

            checkBoxes[i] = cb;
            this.addActor(cb);
        }
    }

    private Array<E> getSelections() {
        Array<E> ivals = new Array<E>();

        for(int i = 0; i < selected.length; ++i) {
            if(selected[i] != null) {
                ivals.add((E) selected[i]);
            }
        }

        return ivals;
    }

    public int getSelectionCount() {
        return getSelections().size;
    }

    public E getRandomSelection() {
        return getSelections().random();
    }
}

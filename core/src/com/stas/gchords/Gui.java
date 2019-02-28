package com.stas.gchords;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Gui {
    private static class Event {
        Actor target;

        Event(Actor target) {
            this.target = target;
        }
    }

    private enum LayoutCellFlag {
        WIDTH_AUTO, WIDTH_FIXED, WIDTH_PROP
    }

    private interface LayoutCell {
        void setBounds(float x, float y, float w, float h);
        float contentDesiredHeight();
        float contentDesiredWidth();
    }

    private static class ActorLayoutCell implements LayoutCell {
        private Actor actor;
        private float actorWidth, actorHeight;

        public ActorLayoutCell(Actor actor) {
            this.actor = actor;
            this.actorWidth = actor.getWidth();
            this.actorHeight = actor.getHeight();
        }

        public void setBounds(float x, float y, float w, float h) {
            actor.setSize(w, h);
            actor.setPosition(
                    x + w / 2 - actor.getWidth() / 2,
                    y + h / 2 - actor.getHeight() / 2
            );
        }

        @Override
        public float contentDesiredHeight() {
            return actorWidth;
        }

        @Override
        public float contentDesiredWidth() {
            return actorHeight;
        }
    }

    private static class PaneLayoutCell implements LayoutCell {
        private Pane pane;

        public PaneLayoutCell(Pane pane) {
            this.pane = pane;
        }

        @Override
        public void setBounds(float x, float y, float w, float h) {
            pane.x = x;
            pane.y = y;
            pane.width = w;
            pane.layout();
        }

        @Override
        public float contentDesiredHeight() {
            return pane.getHeight();
        }

        @Override
        public float contentDesiredWidth() {
            return 0;
        }
    }

    private static class Pane {
        private Array<Array<LayoutCell>> rows;
        float x, y, width;
        float spaceX = 2, spaceY = 2;

        Pane() {
            this.rows = new Array<Array<LayoutCell>>();
        }

        public void row() {
            this.rows.add(new Array<LayoutCell>());
        }

        public void add(LayoutCell cell) {
            rows.get(rows.size - 1).add(cell);
        }

        public float getHeight() {
            float height = 0;

            for(Array<LayoutCell> row : rows) {
                float cellHeight = 0;

                for(LayoutCell cell : row) {
                    cellHeight = Math.max(cellHeight, cell.contentDesiredHeight());
                }

                height += cellHeight + spaceY;
            }

            height -= spaceY;
            return height;
        }

        public void layout() {
            float cellY = this.y;

            for(Array<LayoutCell> row : rows) {
                float cellHeight = 0;
                float cellX = this.x;

                for(LayoutCell cell : row) {
                    cellHeight = Math.max(cellHeight, cell.contentDesiredHeight());
                }

                for(LayoutCell cell : row) {
                    float cellWidth = (width - spaceX * (row.size - 1)) / row.size;
                    cell.setBounds(cellX, cellY, cellWidth, cellHeight);
                    cellX += cellWidth + spaceX;
                }

                cellY += cellHeight + spaceY;
            }
        }

        public void clear() {
            rows.clear();
            row();
        }
    }

    public Stage stage;
    private Skin skin;
    private boolean wasTouched;
    private Array<Event> events;
    private Array<Pane> paneStack;

    public Gui() {
        stage = new Stage(new FitViewport(Consts.IDEAL_SCR_W, Consts.IDEAL_SCR_H));
        skin = Resources.skin;
        events = new Array<Event>();
        paneStack = new Array<Pane>();


    }

    public void begin(boolean touched, int pointerX, int pointerY) {
        events.clear();
        paneStack.clear();

        Pane pane = new Pane();
        pane.width = 1024;
        pane.x = 0;
        pane.y = 0;
        pane.rows.add(new Array<LayoutCell>());
        paneStack.add(pane);

        if(touched && !wasTouched) {
            stage.touchDown(pointerX, pointerY, 0, 0);
        }else if(!touched && wasTouched) {
            stage.touchUp(pointerX, pointerY, 0, 0);
        }

        wasTouched = touched;
    }

    public void end() {
        paneStack.get(0).layout();
        stage.act(0.01f);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        stage.getViewport().apply();
        stage.draw();
    }

    public void beginPane() {
        Pane parent = currPane();

        Pane pane = new Pane();
        pane.rows.add(new Array<LayoutCell>());
        paneStack.add(pane);

        parent.add(new PaneLayoutCell(pane));
    }

    public void endPane() {
        paneStack.pop();
    }

    private Pane currPane() {
        return paneStack.get(paneStack.size - 1);
    }

    public void row() {
        currPane().row();
    }

    private <T> T findActor(String id, Class<T> cls) {
        Actor actor = stage.getRoot().findActor(id);
        return actor != null && cls.isInstance(actor) ? (T)actor : null;
    }

    private Event findEvent(String id) {
        for(Event e : events) {
            if(e.target.getName().equals(id)) {
                return e;
            }
        }

        return null;
    }

    private boolean hasEvent(String id) {
        return findEvent(id) != null;
    }

    private void initNewElement(Actor el, String id) {
        el.setName(id);
        el.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                events.add(new Event(actor));
            }
        });

        stage.addActor(el);
        Pane pane = currPane();
        //pane.rows.get(pane.rows.size - 1).add(new ActorLayoutCell(el));
        pane.add(new ActorLayoutCell(el));
    }

    public boolean textButton(String id, String text) {
        TextButton el = findActor(id, TextButton.class);

        if(el == null) {
            el = new TextButton(text, skin);
            initNewElement(el, id);
        }

        el.setText(text);
        return hasEvent(id);
    }

    public boolean checkBox(String id, String text, boolean checked) {
        CheckBox el = findActor(id, CheckBox.class);

        if(el == null) {
            el = new CheckBox(text, skin);
            initNewElement(el, id);
        }

        el.setChecked(hasEvent(id) ? !checked : checked);
        el.setText(text);
        return el.isChecked();
    }

    public void label(String id, String text) {
        Label el = findActor(id, Label.class);

        if(el == null) {
            el = new Label(text, skin);
            initNewElement(el, id);
        }

        el.setText(text);
    }

    public <T> T selectBox(String id, Array<T> items, T selection) {
        SelectBox<T> el = findActor(id, SelectBox.class);

        if(el == null) {
            el = new SelectBox<T>(skin);
            initNewElement(el, id);
        }

        if(!hasEvent(id)) {
            el.setSelected(selection);
        }

        el.setItems(items);

        return el.getSelected();
    }
}

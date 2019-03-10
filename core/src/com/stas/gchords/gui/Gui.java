package com.stas.gchords.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.stas.gchords.Consts;
import com.stas.gchords.Resources;

import java.util.HashMap;

public class Gui {
    private static class Event {
        Actor target;

        Event(Actor target) {
            this.target = target;
        }
    }

    public Stage stage;
    private Skin skin;
    private boolean wasTouched;
    private Array<Event> events;
    private Array<Pane> paneStack;
    private HashMap<String, Actor> actors;

    public Gui() {
        stage = new Stage(new FitViewport(Consts.IDEAL_SCR_W, Consts.IDEAL_SCR_H));
        skin = Resources.skin;
        events = new Array<Event>();
        paneStack = new Array<Pane>();
        actors = new HashMap<String, Actor>();
    }

    public void begin(boolean touched, int pointerX, int pointerY) {
        events.clear();
        paneStack.clear();

        Pane pane = new Pane();
        pane.setWidth(1024);
        pane.setPosition(0, 0);
        pane.row();
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

        Camera camera = stage.getCamera();
        camera.update();

        Batch batch = stage.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        paneStack.get(0).draw(batch, 1.f);
        batch.end();

        //stage.draw();
    }

    public void beginPane() {
        Pane parent = currPane();

        Pane pane = new Pane();
        pane.row();
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
    }

    public boolean textButton(String id, String text) {
        TextButton el = findActor(id, TextButton.class);

        if(el == null) {
            el = new TextButton(text, skin);
            initNewElement(el, id);
        }

        currPane().add(new ActorLayoutCell(el));
        el.setText(text);
        return hasEvent(id);
    }

    public boolean checkBox(String id, String text, boolean checked) {
        CheckBox el = findActor(id, CheckBox.class);

        if(el == null) {
            el = new CheckBox(text, skin);
            initNewElement(el, id);
        }

        currPane().add(new ActorLayoutCell(el));
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

        currPane().add(new ActorLayoutCell(el));
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

        currPane().add(new ActorLayoutCell(el));
        el.setItems(items);

        return el.getSelected();
    }
}

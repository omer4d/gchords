package com.stas.gchords.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

class ActorLayoutCell implements LayoutCell {
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
        return actorHeight;
    }

    @Override
    public float contentDesiredWidth() {
        return actorWidth;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        actor.draw(batch, parentAlpha);
    }
}

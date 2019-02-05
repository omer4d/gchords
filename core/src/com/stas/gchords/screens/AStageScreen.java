package com.stas.gchords.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.stas.gchords.Consts;

/**
 * Created by Desktop-P1NKA on 9/26/2017.
 */

public abstract class AStageScreen implements Screen {
    private Stage stage;

    public AStageScreen() {
        stage = new Stage(new FitViewport(Consts.IDEAL_SCR_W, Consts.IDEAL_SCR_H));
        init(stage);
    }

    abstract void init(Stage stage);

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        stage.act(dt);
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

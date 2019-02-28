package com.stas.gchords.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.stas.gchords.gui.Gui;

public class GuiTestScreen implements Screen {
    Gui gui;
    public GuiTestScreen() {
        gui = new Gui();
    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(gui.stage);
    }

    boolean check = false;
    String sel = null;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        gui.begin(Gdx.input.isTouched(), Gdx.input.getX(), Gdx.input.getY());

        gui.beginPane();
        gui.textButton("foo1", "one");
        gui.textButton("foo2", "one");
        gui.textButton("foo3", "one"); gui.row();
        gui.textButton("foo4", "one"); gui.row();
        gui.endPane();
        gui.beginPane();
        gui.textButton("foo5", "two"); gui.row();
        check = gui.checkBox("cb", "foo", check);
        gui.endPane();


//        gui.textButton("foo", "one");
//        gui.row();
//        gui.textButton("foo2", "one");
//        gui.textButton("foo3", "one");
//        gui.row();
//        gui.textButton("foo4", "one");
//        gui.textButton("foo5", "one");
//        check = gui.checkBox("cb", "foo", check);
//        gui.textButton("foo6", "one");

//            if(gui.textButton("btn", "foo")) {
////                System.out.println("click!");
////            }
////
////            gui.label("lbl", "fugg");
////
////            check = gui.checkBox("cb", "foo", check);
////
////
////            Array<String> items = new Array<String>();
////            items.add("foo");
////
////            if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
////                items.add("okay");
////            }
////
////            items.add("bar");
////            items.add("baz");
////
////            sel = gui.selectBox("sel", items, sel);


        gui.end();
    }

    @Override
    public void resize(int width, int height) {

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

    @Override
    public void dispose() {

    }
}

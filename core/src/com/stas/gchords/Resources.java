package com.stas.gchords;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Resources {
    public static Texture pianoWhiteKey, pianoBlackKey, rect;
    public static Skin skin;
    public static InstrumentSample[] pianoSamples;

    private static Texture loadTexture(String path) {
        Texture tex = new Texture(path);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return tex;
    }

    public static void init() {
        pianoWhiteKey = loadTexture("piano-white-key.png");
        pianoBlackKey = loadTexture("piano-black-key.png");
        rect = loadTexture("rect.png");

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        pianoSamples =  InstrumentSample.load("samples");
    }

    public static void dispose() {
        pianoBlackKey.dispose();
        pianoBlackKey.dispose();
        rect.dispose();
        skin.dispose();

        for(int i = 0; i < pianoSamples.length; ++i) {
            if(pianoSamples[i] != null) {
                pianoSamples[i].sound.dispose();
            }
        }
    }
}

package com.stas.gchords.actors;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PianoKeyboard extends Group {
    Group[] keys = new Group[128];

    public PianoKeyboard(TextureRegion pianoWhiteKey, TextureRegion pianoBlackKey, TextureRegion keyMark, int startKey, int endKey) {
        String patt = "wbwbwwbwbwbw";
        float whiteX = 0;
        Group whiteKeys = new Group();
        Group blackKeys = new Group();
        this.addActor(whiteKeys);
        this.addActor(blackKeys);

        for(int i = 0; i < endKey - startKey + 1; ++i) {
            boolean isWhite = patt.charAt((i + startKey) % patt.length()) == 'w';
            Image key = new Image(isWhite ? pianoWhiteKey : pianoBlackKey);
            Image marker = new Image(keyMark);
            marker.setSize(key.getWidth(), key.getHeight());
            marker.setColor(1, 0, 0, 0.4f);
            marker.setVisible(false);
            marker.setName("marker");

            Group keyGroup = new Group();
            float x = isWhite ? whiteX : whiteX - pianoBlackKey.getRegionWidth() / 2;
            float y = isWhite ? 0 : pianoWhiteKey.getRegionHeight() - pianoBlackKey.getRegionHeight() + 3;
            keyGroup.addActor(key);
            keyGroup.addActor(marker);

            keyGroup.setPosition(x, y);
            (isWhite ? whiteKeys : blackKeys).addActor(keyGroup);
            whiteX += isWhite ? pianoWhiteKey.getRegionWidth() : 0;

            keys[i + startKey] = keyGroup;
        }
    }

    public void setKeyMarked(int idx, boolean marked) {
        if(keys[idx] != null) {
            keys[idx].findActor("marker").setVisible(marked);
        }
    }

    public void clearAllKeyMarks() {
        for(int i = 0; i < keys.length; ++i) {
            setKeyMarked(i, false);
        }
    }
}

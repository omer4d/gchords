package com.stas.gchords.gui;

import com.badlogic.gdx.graphics.g2d.Batch;

interface LayoutCell {
    void setBounds(float x, float y, float w, float h);
    float contentDesiredHeight();
    float contentDesiredWidth();
    void draw(Batch batch, float parentAlpha);
}

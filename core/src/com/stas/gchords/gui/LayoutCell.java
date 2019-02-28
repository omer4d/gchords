package com.stas.gchords.gui;

interface LayoutCell {
    void setBounds(float x, float y, float w, float h);
    float contentDesiredHeight();
    float contentDesiredWidth();
}

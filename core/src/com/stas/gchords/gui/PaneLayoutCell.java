package com.stas.gchords.gui;

class PaneLayoutCell implements LayoutCell {
    private Pane pane;

    public PaneLayoutCell(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void setBounds(float x, float y, float w, float h) {
        pane.setPosition(x, y);
        pane.setWidth(w);
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

package com.stas.gchords.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.stas.gchords.Resources;

class Pane {
    private Array<Array<LayoutCell>> rows;
    private float x, y, width;
    private float spaceX = 2, spaceY = 2;
    private float padX = 3, padY = 3;

    public Pane() {
        this.rows = new Array<Array<LayoutCell>>();
    }

    public void row() {
        this.rows.add(new Array<LayoutCell>());
    }

    public void add(LayoutCell cell) {
        rows.get(rows.size - 1).add(cell);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
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
        return height + padY * 2;
    }

    public void layout() {
        float cellY = this.y + padY;

        for(Array<LayoutCell> row : rows) {
            float cellHeight = 0;
            float cellX = this.x + padX;

            for(LayoutCell cell : row) {
                cellHeight = Math.max(cellHeight, cell.contentDesiredHeight());
            }

            for(LayoutCell cell : row) {
                float cellWidth = (width - spaceX * (row.size - 1) - padX * 2) / row.size;
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

    public void draw(Batch batch, float parentAlpha) {
        Resources.skin.getDrawable("window-noborder").draw(batch, x, y, width, getHeight());

        for(Array<LayoutCell> row : rows) {
            for(LayoutCell cell : row) {
                cell.draw(batch, parentAlpha);
            }
        }
    }
}

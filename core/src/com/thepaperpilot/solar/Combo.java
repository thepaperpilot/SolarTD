package com.thepaperpilot.solar;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thepaperpilot.solar.Levels.Level;

public enum Combo {
    // Max: 6
    // or else they won't fit
    MINE(1, 0, 0, Level.Resource.RED) {
        public void fire() {

        }
    };

    public final Table table;
    public final int red;
    public final int blue;
    public final int yellow;
    public final Level.Resource type;

    Combo(int red, int blue, int yellow, Level.Resource type) { // values do not include the tower firing
        this.red = red;
        this.blue = blue;
        this.yellow = yellow;
        this.type = type;
        table = new Table(Main.skin);
        table.left().pad(4).add(new Label(name(), Main.skin)).left().spaceLeft(2).colspan(1 + red + blue + yellow).row();
        table.add(new Image(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "redStoreDown" : type == Level.Resource.BLUE ? "blueStoreDown" : "yellowStoreDown")))).size(32);
        for (int i = 0; i < red; i++) {
            table.add(new Image(Main.getDrawable("towers/redStore"))).size(32);
        }
        for (int i = 0; i < blue; i++) {
            table.add(new Image(Main.getDrawable("towers/blueStore"))).size(32);
        }
        for (int i = 0; i < yellow; i++) {
            table.add(new Image(Main.getDrawable("towers/yellowStore"))).size(32);
        }
        table.setBackground(Main.skin.getDrawable("default-rect"));
    }

    public abstract void fire();
}

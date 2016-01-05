package com.thepaperpilot.solar;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thepaperpilot.solar.Levels.Level;

public enum Combo {
    // Max: 6
    // or else they won't fit
    MINE(new Level.Resource[]{Level.Resource.RED, Level.Resource.RED}) {
        void fire() {

        }
    };

    public final Table table;
    private final Level.Resource[] towers;

    Combo(Level.Resource[] towers) {
        this.towers = towers;
        table = new Table(Main.skin);
        table.left().pad(4).add(new Label(name(), Main.skin)).left().spaceLeft(2).colspan(towers.length).row();
        table.add(new Image(Main.getDrawable("towers/" + (towers[0] == Level.Resource.RED ? "redStoreDown" : towers[0] == Level.Resource.BLUE ? "blueStoreDown" : "yellowStoreDown")))).size(32);
        for (int i = 1; i < towers.length; i++) {
            table.add(new Image(Main.getDrawable("towers/" + (towers[i] == Level.Resource.RED ? "redStore" : towers[i] == Level.Resource.BLUE ? "blueStore" : "yellowStore")))).size(32);
        }
        table.setBackground(Main.skin.getDrawable("default-rect"));
    }

    abstract void fire();
}

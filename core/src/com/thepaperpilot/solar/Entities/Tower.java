package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Tower extends Image {
    public final Circle area;
    private final Type type;
    boolean comboUpgrade;
    float damage;
    float speed;
    float range;

    public Tower(float x, float y, Type type, final Level level) {
        area = new Circle(x + Main.TOWER_RADIUS, y + Main.TOWER_RADIUS, Main.TOWER_RADIUS);
        setPosition(x, y);
        this.type = type;
        switch (type) {
            default:
            case RED:
                setDrawable(Main.getDrawable("towers/red"));
                break;
            case BLUE:
                setDrawable(Main.getDrawable("towers/blue"));
                break;
            case YELLOW:
                setDrawable(Main.getDrawable("towers/yellow"));
                break;
        }
        setSize(Main.TOWER_RADIUS * 2, Main.TOWER_RADIUS * 2);

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level.selected = Tower.this;
            }
        });
    }

    public enum Type {
        RED,
        BLUE,
        YELLOW
    }
}

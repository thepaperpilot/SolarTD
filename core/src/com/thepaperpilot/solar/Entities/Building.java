package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Interface.Menu;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public abstract class Building extends Image {

    public final Circle area;
    public final Level.Resource type;
    final Level level;
    public float redValue;
    public float blueValue;
    public float yellowValue;

    Building(float x, float y, float radius, final Level level, Level.Resource resource) {
        this.area = new Circle(x + radius, y + radius, radius);
        this.level = level;
        this.type = resource;
        setPosition(x, y);
        setSize(radius * 2, radius * 2);
        redValue = Main.SELL_RATE * (this instanceof Tower ? Tower.getRedCost(type) : Generator.getRedCost(type));
        blueValue = Main.SELL_RATE * (this instanceof Tower ? Tower.getBlueCost(type) : Generator.getBlueCost(type));
        yellowValue = Main.SELL_RATE * (this instanceof Tower ? Tower.getYellowCost(type) : Generator.getYellowCost(type));

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.movingBuilding) return;
                if (!level.placingBuilding) {
                    level.selectedBuilding = (level.selectedBuilding == Building.this ? null : Building.this);
                    level.movingBuilding = false;
                    Menu.select();
                }
                event.reset();
            }
        });
    }

    public String getName() {
        return "Building";
    }

    public void sell() {
        level.redResource += redValue;
        level.blueResource += blueValue;
        level.yellowResource += yellowValue;
        level.buildings.remove(this);
        remove();
        level.selectedBuilding = null;
        level.movingBuilding = false;
        Menu.deselect();
    }
}

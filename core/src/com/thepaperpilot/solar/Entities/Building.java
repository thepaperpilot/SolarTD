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
    public Level.Resource type;
    protected Level level;

    Building(float x, float y, float radius, final Level level, Level.Resource resource) {
        this.area = new Circle(x + radius, y + radius, radius);
        this.level = level;
        this.type = resource;
        setPosition(x, y);
        setSize(radius * 2, radius * 2);

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
        level.redResource += Main.SELL_RATE * (this instanceof Tower ? Tower.getRedCost(type) : Generator.getRedCost(type));
        level.blueResource += Main.SELL_RATE * (this instanceof Tower ? Tower.getBlueCost(type) : Generator.getBlueCost(type));
        level.yellowResource += Main.SELL_RATE * (this instanceof Tower ? Tower.getYellowCost(type) : Generator.getYellowCost(type));
        level.buildings.remove(this);
        remove();
        level.selectedBuilding = null;
        level.movingBuilding = false;
        Menu.deselect();
    }
}

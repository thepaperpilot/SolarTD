package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Generator extends Building {
    private final Level.Resource type;
    private final Level level;
    private float time;
    private float amount = 1;
    private float speed = 4;

    public Generator(float x, float y, Level.Resource type, final Level level) {
        super(x, y, 2 * Main.TOWER_RADIUS);
        setOrigin(Align.center);
        this.type = type;
        this.level = level;
        switch (type) {
            default:
            case RED:
                setDrawable(Main.getDrawable("towers/redGen"));
                break;
            case BLUE:
                setDrawable(Main.getDrawable("towers/blueGen"));
                break;
            case YELLOW:
                setDrawable(Main.getDrawable("towers/yellowGen"));
                break;
        }

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!level.placingBuilding)
                    level.selectedBuilding = (level.selectedBuilding == Generator.this ? null : Generator.this);
                event.reset();
            }
        });
    }

    public static boolean pay(Level.Resource type, Level level) {
        if (level.redResource >= getRedCost(type) && level.blueResource >= getBlueCost(type) && level.yellowResource >= getYellowCost(type)) {
            level.redResource -= getRedCost(type);
            level.blueResource -= getBlueCost(type);
            level.yellowResource -= getYellowCost(type);
            return true;
        }
        return false;
    }

    public static int getRedCost(Level.Resource type) {
        return type == Level.Resource.RED ? 100 : 50;
    }

    public static int getBlueCost(Level.Resource type) {
        return type == Level.Resource.BLUE ? 100 : 50;
    }

    public static int getYellowCost(Level.Resource type) {
        return type == Level.Resource.YELLOW ? 100 : 50;
    }

    public void act(float delta) {
        time += delta;
        rotateBy(10 * delta);
        while (time >= speed) {
            time -= speed;
            final Label increase = new Label("" + (int) amount, Main.skin);
            increase.setPosition(getX() + 2 * Main.TOWER_RADIUS, getY() + 2 * Main.TOWER_RADIUS);
            increase.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, 20, .5f), Actions.fadeOut(.5f)), Actions.run(new Runnable() {
                @Override
                public void run() {
                    increase.remove();
                }
            })));
            switch (type) {
                default:
                case RED:
                    level.redResource += amount;
                    increase.setColor(1, 0, 0, 1);
                    break;
                case BLUE:
                    level.blueResource += amount;
                    increase.setColor(0, 0, 1, 1);
                    break;
                case YELLOW:
                    level.yellowResource += amount;
                    increase.setColor(1, 1, 0, 1);
                    break;
            }
            level.stage.addActor(increase);
        }
    }
}

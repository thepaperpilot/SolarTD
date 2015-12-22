package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Generator extends Building {
    private final Type type;
    private final Level level;
    private float time;
    private float amount = 1;
    private float speed = 4;

    public Generator(float x, float y, Type type, final Level level) {
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
                if (!level.placingBuilding) level.selected = (level.selected == Generator.this ? null : Generator.this);
                event.reset();
            }
        });
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

    public enum Type {
        RED,
        BLUE,
        YELLOW
    }

    ;
}

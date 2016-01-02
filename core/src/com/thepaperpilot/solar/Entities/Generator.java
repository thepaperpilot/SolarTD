package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Generator extends Building {
    private static final int[] extractorCosts = new int[]{100, 150, 200, 250, 300, 350, 400, 450, 500};
    private static final int[] efficiencyCosts = new int[]{200, 300, 400, 500, 600, 700, 800, 900, 1000};

    private float time;
    private int amount = 0;
    private int speed = 4;

    public Generator(float x, float y, Level.Resource type, final Level level) {
        super(x, y, 2 * Main.TOWER_RADIUS, level, type);
        setOrigin(Align.center);
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
        return type == Level.Resource.RED ? 50 : 25;
    }

    public static int getBlueCost(Level.Resource type) {
        return type == Level.Resource.BLUE ? 50 : 25;
    }

    public static int getYellowCost(Level.Resource type) {
        return type == Level.Resource.YELLOW ? 50 : 25;
    }

    public static String getDescription(Level.Resource type) {
        return "Generates additional " + type.name().toLowerCase() + " resources";
    }

    public void act(float delta) {
        time += delta * speed;
        rotateBy(10 * delta);
        while (time >= Main.GENERATOR_SPEED) {
            time -= Main.GENERATOR_SPEED;
            final Label increase = new Label("+" + (amount + 1), Main.skin);
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
                    level.redResource += amount + 1;
                    increase.setColor(1, 0, 0, 1);
                    break;
                case BLUE:
                    level.blueResource += amount + 1;
                    increase.setColor(0, 0, 1, 1);
                    break;
                case YELLOW:
                    level.yellowResource += amount + 1;
                    increase.setColor(1, 1, 0, 1);
                    break;
            }
            level.stage.addActor(increase);
        }
    }

    public String getName() {
        return type.name() + " GENERATOR";
    }

    public int getExtractors() {
        return amount;
    }

    public int getExtractorCost() {
        return amount < 9 ? extractorCosts[amount] : -1;
    }

    public void upgradeExtractors() {
        if (amount < 9) {
            switch (type) {
                case RED:
                    if (level.redResource >= extractorCosts[amount]) {
                        redValue += Main.SELL_RATE * extractorCosts[amount];
                        level.redResource -= extractorCosts[amount];
                        amount++;
                    }
                    break;
                case BLUE:
                    if (level.blueResource >= extractorCosts[amount]) {
                        blueValue += Main.SELL_RATE * extractorCosts[amount];
                        level.blueResource -= extractorCosts[amount];
                        amount++;
                    }
                    break;
                case YELLOW:
                    if (level.yellowResource >= extractorCosts[amount]) {
                        yellowValue += Main.SELL_RATE * extractorCosts[amount];
                        level.yellowResource -= extractorCosts[amount];
                        amount++;
                    }
                    break;
            }
        }
    }

    public int getEfficiency() {
        return speed;
    }

    public int getEfficiencyIndex() {
        return speed - 4;
    }

    public int getEfficiencyCost() {
        return getEfficiencyIndex() < 9 ? efficiencyCosts[getEfficiencyIndex()] : -1;
    }

    public void upgradeEfficiency() {
        if (getEfficiencyIndex() < 9) {
            switch (type) {
                case RED:
                    if (level.redResource >= efficiencyCosts[getEfficiencyIndex()]) {
                        redValue += Main.SELL_RATE * efficiencyCosts[getEfficiencyIndex()];
                        level.redResource -= efficiencyCosts[getEfficiencyIndex()];
                        speed++;
                    }
                    break;
                case BLUE:
                    if (level.blueResource >= efficiencyCosts[getEfficiencyIndex()]) {
                        blueValue += Main.SELL_RATE * efficiencyCosts[getEfficiencyIndex()];
                        level.blueResource -= efficiencyCosts[getEfficiencyIndex()];
                        speed++;
                    }
                    break;
                case YELLOW:
                    if (level.yellowResource >= efficiencyCosts[getEfficiencyIndex()]) {
                        yellowValue += Main.SELL_RATE * efficiencyCosts[getEfficiencyIndex()];
                        level.yellowResource -= efficiencyCosts[getEfficiencyIndex()];
                        speed++;
                    }
                    break;
            }
        }
    }
}

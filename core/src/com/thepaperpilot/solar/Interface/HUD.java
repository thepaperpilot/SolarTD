package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Levels.Wave;
import com.thepaperpilot.solar.Main;

public class HUD {
    private static final Button menu = new TextButton("MENU", Main.skin, "large");
    private static final TextButton pause = new TextButton("PAUSE", Main.skin, "large");
    private static final Button red = new Button(Main.getDrawable("towers/redStore"), Main.getDrawable("towers/redStoreDown"), Main.getDrawable("towers/redStoreDown"));
    private static final Button blue = new Button(Main.getDrawable("towers/blueStore"), Main.getDrawable("towers/blueStoreDown"), Main.getDrawable("towers/blueStoreDown"));
    private static final Button yellow = new Button(Main.getDrawable("towers/yellowStore"), Main.getDrawable("towers/yellowStoreDown"), Main.getDrawable("towers/yellowStoreDown"));
    private static final Button redGen = new Button(Main.getDrawable("towers/redGen"), Main.getDrawable("towers/redGenStoreDown"), Main.getDrawable("towers/redGenStoreDown"));
    private static final Button blueGen = new Button(Main.getDrawable("towers/blueGen"), Main.getDrawable("towers/blueGenStoreDown"), Main.getDrawable("towers/blueGenStoreDown"));
    private static final Button yellowGen = new Button(Main.getDrawable("towers/yellowGen"), Main.getDrawable("towers/yellowGenStoreDown"), Main.getDrawable("towers/yellowGenStoreDown"));
    private static final Label redRes = new Label("", Main.skin, "large");
    private static final Label blueRes = new Label("", Main.skin, "large");
    private static final Label yellowRes = new Label("", Main.skin, "large");
    private static final Label redCost = new Label("", Main.skin, "large");
    private static final Label blueCost = new Label("", Main.skin, "large");
    private static final Label yellowCost = new Label("", Main.skin, "large");
    private static final Label description = new Label("", Main.skin);
    private static final Label livesLabel = new Label("", Main.skin, "large");
    private static final Label wavesLabel = new Label("", Main.skin, "large");
    private static final Label timeLabel = new Label("", Main.skin, "large");
    private static final Table enemyTable = new Table(Main.skin);
    private static final ButtonGroup buildings = new ButtonGroup(red, blue, yellow, redGen, blueGen, yellowGen);
    private static final Table resourcesTable = new Table(Main.skin); // we need a reference to this in order to get its x position
    private static final Table cost = new Table(Main.skin);
    private static final Table ui = new Table(Main.skin);

    private static Level level;

    static {
        buildings.setMinCheckCount(0);
        ui.setSize(Main.UI_WIDTH, 64);
        ui.setBackground(Main.skin.getDrawable("default-round"));
        ui.setPosition(0, 8);

        Table buttonsTable = new Table(Main.skin);
        menu.pad(10);
        buttonsTable.add(menu).expandY().fill().spaceBottom(8).row();
        pause.pad(10);
        buttonsTable.add(pause).width(new GlyphLayout(Main.skin.getFont("large"), "RESUME").width + 10).expandY().fill();
        ui.add(buttonsTable).expandY().fillY().spaceLeft(4);

        Table towersTable = new Table(Main.skin);
        towersTable.setBackground(Main.skin.getDrawable("default-round"));
        towersTable.add(new Label("Towers", Main.skin, "large")).colspan(3).row();
        towersTable.add(red).size(32);
        towersTable.add(blue).size(32);
        towersTable.add(yellow).size(32);
        ui.add(towersTable).spaceLeft(4).uniformY();

        Table generatorsTable = new Table(Main.skin);
        generatorsTable.setBackground(Main.skin.getDrawable("default-round"));
        generatorsTable.add(new Label("Generators", Main.skin, "large")).colspan(3).row();
        generatorsTable.add(redGen).size(32);
        generatorsTable.add(blueGen).size(32);
        generatorsTable.add(yellowGen).size(32);
        ui.add(generatorsTable).spaceLeft(8).uniformY();

        resourcesTable.setBackground(Main.skin.getDrawable("default-round"));
        resourcesTable.add(new Label("Resources", Main.skin, "large")).colspan(3).row();
        Table redTable = new Table(Main.skin);
        redRes.setColor(.5f, 0, 0, 1);
        redTable.add(redRes);
        resourcesTable.add(redTable).minWidth(40).height(28).pad(2);
        Table blueTable = new Table(Main.skin);
        blueRes.setColor(0, 0, .5f, 1);
        blueTable.add(blueRes);
        resourcesTable.add(blueTable).minWidth(40).height(28).pad(2);
        Table yellowTable = new Table(Main.skin);
        yellowRes.setColor(.5f, .5f, 0, 1);
        yellowTable.add(yellowRes);
        resourcesTable.add(yellowTable).minWidth(40).height(28).pad(2);
        ui.add(resourcesTable).spaceLeft(8).uniformY();

        Table lifeTable = new Table(Main.skin);
        lifeTable.setBackground(Main.skin.getDrawable("default-round"));
        lifeTable.add(new Label("Lives", Main.skin, "large")).row();
        lifeTable.add(livesLabel).height(32);
        ui.add(lifeTable).spaceLeft(8).uniformY();

        Table waveTable = new Table(Main.skin);
        waveTable.setBackground(Main.skin.getDrawable("default-round"));
        waveTable.add(new Label("Wave", Main.skin, "large")).row();
        waveTable.add(wavesLabel).height(32);
        ui.add(waveTable).spaceLeft(8).uniformY();

        Table timerTable = new Table(Main.skin);
        timerTable.setBackground(Main.skin.getDrawable("default-round"));
        timerTable.add(new Label("Next Wave", Main.skin, "large")).row();
        enemyTable.setBackground(Main.getDrawable("alien"));
        enemyTable.add(timeLabel);
        timerTable.add(enemyTable).size(32);
        timerTable.setTouchable(Touchable.enabled);
        ui.add(timerTable).spaceLeft(8).uniformY();

        cost.setColor(1, 1, 1, .5f);
        cost.setTouchable(Touchable.disabled);
        cost.setBackground(Main.skin.getDrawable("default-round"));
        cost.setVisible(false);
        cost.setPosition(resourcesTable.getX(), 72);
        cost.setHeight(40);
        cost.add(description).colspan(3).spaceBottom(4).row();
        redCost.setColor(1, 0, 0, 1);
        blueCost.setColor(0, 0, 1, 1);
        yellowCost.setColor(1, 1, 0, 1);
        cost.add(redCost).expand().uniform();
        cost.add(blueCost).expand().uniform();
        cost.add(yellowCost).expand().uniform();

        menu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Menu.toggle();
                Main.getSound("select").play(Main.volume);
            }
        });
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });
        red.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.RED, true);
                Main.getSound("select").play(Main.volume);
            }
        });
        blue.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.BLUE, true);
                Main.getSound("select").play(Main.volume);
            }
        });
        yellow.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.YELLOW, true);
                Main.getSound("select").play(Main.volume);
            }
        });
        redGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.RED, false);
                Main.getSound("select").play(Main.volume);
            }
        });
        blueGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.BLUE, false);
                Main.getSound("select").play(Main.volume);
            }
        });
        yellowGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.YELLOW, false);
                Main.getSound("select").play(Main.volume);
            }
        });
        timerTable.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level.nextWave();
                Main.getSound("select").play(Main.volume);
            }
        });
    }

    public static void pressButton(int button) {
        switch (button) {
            case 1:
                red.toggle();
                updateStore(Level.Resource.RED, true);
                break;
            case 2:
                blue.toggle();
                updateStore(Level.Resource.BLUE, true);
                break;
            case 3:
                yellow.toggle();
                updateStore(Level.Resource.YELLOW, true);
                break;
            case 4:
                redGen.toggle();
                updateStore(Level.Resource.RED, false);
                break;
            case 5:
                blueGen.toggle();
                updateStore(Level.Resource.BLUE, false);
                break;
            case 6:
                yellowGen.toggle();
                updateStore(Level.Resource.YELLOW, false);
                break;
        }
    }

    private static void updateStore(Level.Resource resource, boolean tower) {
        Button button;
        if (tower) button = resource == Level.Resource.RED ? red : resource == Level.Resource.BLUE ? blue : yellow;
        else button = resource == Level.Resource.RED ? redGen : resource == Level.Resource.BLUE ? blueGen : yellowGen;

        if (level != null) {
            level.placingBuilding = button.isChecked();
            if (level.placingBuilding) {
                level.movingBuilding = false;
            }
            level.selectedType = tower ? 1 : 2;
            level.selectedResource = resource;
        }

        cost.setVisible(button.isChecked());
    }

    public static void deselect() {
        buildings.uncheckAll();
        cost.setVisible(false);
    }

    public static void init(Level level) {
        HUD.level = level;

        level.ui.addActor(ui);
        level.ui.addActor(cost);
    }

    public static void update() {
        redRes.setText("" + level.redResource);
        blueRes.setText("" + level.blueResource);
        yellowRes.setText("" + level.yellowResource);
        livesLabel.setText("" + level.population);
        wavesLabel.setText("" + level.wave);
        timeLabel.setText("" + Math.round(level.currWave.getTime() + Main.WAVE_INTERVAL - level.time));

        if (!cost.isVisible()) return;

        int type = level.selectedType;
        Level.Resource resource = level.selectedResource;
        description.setText(type == 1 ? Tower.getDescription(resource) : Generator.getDescription(resource));
        cost.setWidth(description.getPrefWidth() + 8);
        cost.setX(resourcesTable.getX() - (cost.getWidth() - resourcesTable.getWidth()) / 2);
        redCost.setText("" + (int) (type == 1 ? Tower.getRedCost(resource) : Generator.getRedCost(resource)));
        redCost.setColor(1, 0, 0, level.redResource >= (type == 1 ? Tower.getRedCost(resource) : Generator.getRedCost(resource)) ? 1 : .5f);
        blueCost.setText("" + (int) (type == 1 ? Tower.getBlueCost(resource) : Generator.getBlueCost(resource)));
        blueCost.setColor(0, 0, 1, level.blueResource >= (type == 1 ? Tower.getBlueCost(resource) : Generator.getBlueCost(resource)) ? 1 : .5f);
        yellowCost.setText("" + (int) (type == 1 ? Tower.getYellowCost(resource) : Generator.getYellowCost(resource)));
        yellowCost.setColor(1, 1, 0, level.yellowResource >= (type == 1 ? Tower.getYellowCost(resource) : Generator.getYellowCost(resource)) ? 1 : .5f);
    }

    public static void updateWaves() {
        Wave wave = new Wave(level.waves[(level.wave) % level.waves.length], level);
        if (wave.enemies.size() > 0)
            enemyTable.setBackground(Main.getDrawable(wave.enemies.get(0).name));
    }

    public static void pause() {
        level.paused = !level.paused;
        pause.setText(level.paused ? "RESUME" : "PAUSE");
    }
}

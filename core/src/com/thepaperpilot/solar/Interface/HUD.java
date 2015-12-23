package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class HUD {
    private static final Button menu = new TextButton("MENU", Main.skin, "large");
    private static final TextButton pause = new TextButton("PAUSE", Main.skin, "large");
    private static final Button red = new Button(Main.getDrawable("towers/redStore"), Main.getDrawable("towers/redStoreDown"), Main.getDrawable("towers/redStoreDown"));
    private static final Button blue = new Button(Main.getDrawable("towers/blueStore"), Main.getDrawable("towers/blueStoreDown"), Main.getDrawable("towers/blueStoreDown"));
    private static final Button yellow = new Button(Main.getDrawable("towers/yellowStore"), Main.getDrawable("towers/yellowStoreDown"), Main.getDrawable("towers/yellowStoreDown"));
    private static final Button redGen = new Button(Main.getDrawable("towers/redGenStore"), Main.getDrawable("towers/redGenStoreDown"), Main.getDrawable("towers/redGenStoreDown"));
    private static final Button blueGen = new Button(Main.getDrawable("towers/blueGenStore"), Main.getDrawable("towers/blueGenStoreDown"), Main.getDrawable("towers/blueGenStoreDown"));
    private static final Button yellowGen = new Button(Main.getDrawable("towers/yellowGenStore"), Main.getDrawable("towers/yellowGenStoreDown"), Main.getDrawable("towers/yellowGenStoreDown"));
    private static final Label redRes = new Label("", Main.skin, "large");
    private static final Label blueRes = new Label("", Main.skin, "large");
    private static final Label yellowRes = new Label("", Main.skin, "large");
    private static final Label redCost = new Label("", Main.skin, "large");
    private static final Label blueCost = new Label("", Main.skin, "large");
    private static final Label yellowCost = new Label("", Main.skin, "large");
    private static final Label livesLabel = new Label("", Main.skin, "large");
    private static final Label wavesLabel = new Label("", Main.skin, "large");
    private static final Label timeLabel = new Label("", Main.skin, "large");
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
        resourcesTable.add(redTable).size(32);
        Table blueTable = new Table(Main.skin);
        blueRes.setColor(0, 0, .5f, 1);
        blueTable.add(blueRes);
        resourcesTable.add(blueTable).size(32);
        Table yellowTable = new Table(Main.skin);
        yellowRes.setColor(.5f, .5f, 0, 1);
        yellowTable.add(yellowRes);
        resourcesTable.add(yellowTable).size(32);
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
        timerTable.add(new Label("Next Enemy", Main.skin, "large")).row();
        Table enemyTable = new Table(Main.skin);
        enemyTable.setBackground(Main.getDrawable("alien"));
        enemyTable.add(timeLabel);
        timerTable.add(enemyTable).size(32);
        ui.add(timerTable).spaceLeft(8).uniformY();

        cost.setColor(1, 1, 1, .5f);
        cost.setTouchable(Touchable.disabled);
        cost.setBackground(Main.skin.getDrawable("default-round"));
        cost.setVisible(false);
        cost.setPosition(resourcesTable.getX(), 72);
        cost.setSize(resourcesTable.getPrefWidth(), 40);
        cost.add(new Label("Cost", Main.skin)).colspan(3).row();
        cost.add(redCost).expand().uniform();
        cost.add(blueCost).expand().uniform();
        cost.add(yellowCost).expand().uniform();

        menu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Menu.toggle();
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
            }
        });
        blue.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.BLUE, true);
            }
        });
        yellow.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.YELLOW, true);
            }
        });
        redGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.RED, false);
            }
        });
        blueGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.BLUE, false);
            }
        });
        yellowGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                updateStore(Level.Resource.YELLOW, false);
            }
        });
    }

    private static void updateStore(Level.Resource resource, boolean tower) {
        Button button;
        if (tower) button = resource == Level.Resource.RED ? red : resource == Level.Resource.BLUE ? blue : yellow;
        else button = resource == Level.Resource.RED ? redGen : resource == Level.Resource.BLUE ? blueGen : yellowGen;

        if (level != null) {
            level.placingBuilding = button.isChecked();
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
        wavesLabel.setText("" + level.currWave);
        timeLabel.setText("" + Math.round(level.time <= 0 ? Math.abs(level.time) : (level.currWave < level.waves.length ? level.waves[level.currWave].enemyDistance : level.finalWave.enemyDistance) - level.time));
        cost.setX(resourcesTable.getX());

        int type = level.selectedType;
        Level.Resource resource = level.selectedResource;
        redCost.setText("" + (type == 1 ? Tower.getRedCost(resource) : Generator.getRedCost(resource)));
        redCost.setColor(level.redResource >= (type == 1 ? Tower.getRedCost(resource) : Generator.getRedCost(resource)) ? Color.GREEN : Color.RED);
        blueCost.setText("" + (type == 1 ? Tower.getBlueCost(resource) : Generator.getBlueCost(resource)));
        blueCost.setColor(level.blueResource >= (type == 1 ? Tower.getBlueCost(resource) : Generator.getBlueCost(resource)) ? Color.GREEN : Color.RED);
        yellowCost.setText("" + (type == 1 ? Tower.getYellowCost(resource) : Generator.getYellowCost(resource)));
        yellowCost.setColor(level.yellowResource >= (type == 1 ? Tower.getYellowCost(resource) : Generator.getYellowCost(resource)) ? Color.GREEN : Color.RED);
    }

    public static void pause() {
        level.paused = !level.paused;
        pause.setText(level.paused ? "RESUME" : "PAUSE");
    }
}

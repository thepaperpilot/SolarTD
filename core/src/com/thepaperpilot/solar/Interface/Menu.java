package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Combo;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;
import com.thepaperpilot.solar.MenuScreen;

public class Menu {
    private static final Label buildingLabel = new Label("Building", Main.skin);
    private static final TextButton settingsButton = new TextButton("Settings", Main.skin, "toggle");
    private static final TextButton comboButton = new TextButton("Combos", Main.skin, "toggle");
    private static final TextButton generalButton = new TextButton("General", Main.skin, "toggle");
    private static final TextButton towerComboButton = new TextButton("Combo", Main.skin, "toggle");
    private static final Table buildingTable = new Table(Main.skin);
    private static final Table towerTable = new Table(Main.skin);
    private static final Label damageLabel = new Label("0", Main.skin);
    private static final ProgressBar damageBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final TextButton damageUpgrade = new TextButton("0", Main.skin);
    private static final Label rangeLabel = new Label("0", Main.skin);
    private static final ProgressBar rangeBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final TextButton rangeUpgrade = new TextButton("0", Main.skin);
    private static final Label speedLabel = new Label("0", Main.skin);
    private static final ProgressBar speedBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final TextButton speedUpgrade = new TextButton("0", Main.skin);
    private static final TextButton nearestButton = new TextButton("Nearest", Main.skin, "toggle");
    private static final TextButton firstButton = new TextButton("First", Main.skin, "toggle");
    private static final TextButton lastButton = new TextButton("Last", Main.skin, "toggle");
    private static final TextButton strongestButton = new TextButton("Strongest", Main.skin, "toggle");
    private static final TextButton weakestButton = new TextButton("Weakest", Main.skin, "toggle");
    private static final Table comboUpgrade = new Table(Main.skin);
    private static final Table generatorTable = new Table(Main.skin);
    private static final Label extractorsLabel = new Label("0", Main.skin);
    private static final ProgressBar extractorsBar = new ProgressBar(0, 9, 1, false, Main.skin);
    private static final TextButton extractorsUpgrade = new TextButton("0", Main.skin);
    private static final Label efficiencyLabel = new Label("0", Main.skin);
    private static final ProgressBar efficiencyBar = new ProgressBar(0, 9, 1, false, Main.skin);
    private static final TextButton efficiencyUpgrade = new TextButton("0", Main.skin);
    private static final Table generalTable = new Table(Main.skin);
    private static final StatsCircle towerCircle = new StatsCircle();
    private static final Label totalKillsLabel = new Label("", Main.skin);
    private static final Label fpsLabel = new Label("", Main.skin);
    private static final Label towerKillsLabel = new Label("", Main.skin);
    private static final Label towerShotsLabel = new Label("", Main.skin);
    private static final Label generatedLabel = new Label("", Main.skin);
    private static final Table settingsTable = new Table(Main.skin);
    private static final ScrollPane comboPane;
    private static final Table comboTable = new Table(Main.skin);
    private static final ProgressBar comboBar = new ProgressBar(0, 100, 1, false, Main.skin);
    private static final Label comboLabel = new Label("0%", Main.skin);
    private static final ScrollPane towerComboPane;
    private static final Table towerComboTable = new Table(Main.skin);
    private static final Window menu = new Window("Settings", Main.skin, "large");
    private static Table currentTab;

    private static Level level;

    static {
        new ButtonGroup(settingsButton, comboButton, generalButton, towerComboButton);
        new ButtonGroup(nearestButton, firstButton, lastButton, strongestButton, weakestButton);

        menu.setVisible(false);
        menu.setSize(300, 226);
        menu.setPosition(20, 200);
        menu.setColor(1, 1, 1, 0);

        Table buttonsTable = new Table(Main.skin);
        buttonsTable.setBackground(Main.skin.getDrawable("default-round"));
        buttonsTable.top().add(settingsButton).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(comboButton).expandX().fill().spaceBottom(4).row();
        buildingLabel.setAlignment(Align.center);
        buildingTable.center().add(buildingLabel).expandX().fill().spaceBottom(4).row();
        buildingTable.add(generalButton).expandX().fill().spaceBottom(4).row();
        buildingTable.add(towerComboButton).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(buildingTable).spaceTop(8).expandX().fill().row();
        settingsButton.setChecked(true);

        settingsTable.setName("Settings");
        Button restart = new TextButton("Restart Level", Main.skin);
        settingsTable.top().add(restart).expandX().fill().row();
        Button main = new TextButton("Main Menu", Main.skin);
        settingsTable.add(main).expandX().fill().row();
        Table statsTable = new Table(Main.skin);
        statsTable.add(new Label("Total Kills: ", Main.skin));
        statsTable.add(totalKillsLabel).row();
        statsTable.add(new Label("FPS: ", Main.skin));
        statsTable.add(fpsLabel).row();
        settingsTable.add(statsTable);
        currentTab = settingsTable;

        Button sellButton = new TextButton("Sell Tower", Main.skin);
        Button moveButton = new TextButton("Move Tower", Main.skin);
        towerTable.pad(2);
        Table upgradesTable = new Table(Main.skin);
        upgradesTable.add(new Label("Damage: ", Main.skin)).right();
        damageLabel.setColor(1, 0, 0, 1);
        upgradesTable.add(damageLabel).right();
        damageBar.setColor(1, 0, 0, 1);
        upgradesTable.add(damageBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        damageUpgrade.setColor(1, 0, 0, 1);
        upgradesTable.add(damageUpgrade).row();
        upgradesTable.add(new Label("Range: ", Main.skin)).right();
        rangeLabel.setColor(0, 0, 1, 1);
        upgradesTable.add(rangeLabel).right();
        rangeBar.setColor(0, 0, 1, 1);
        upgradesTable.add(rangeBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        rangeUpgrade.setColor(0, 0, 1, 1);
        upgradesTable.add(rangeUpgrade).row();
        upgradesTable.add(new Label("Speed: ", Main.skin)).right();
        speedLabel.setColor(1, 1, 0, 1);
        upgradesTable.add(speedLabel).right();
        speedBar.setColor(1, 1, 0, 1);
        upgradesTable.add(speedBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        speedUpgrade.setColor(1, 1, 0, 1);
        upgradesTable.add(speedUpgrade).row();
        towerTable.add(upgradesTable).row();
        Table targetingTable = new Table(Main.skin);
        targetingTable.add(nearestButton);
        targetingTable.add(firstButton);
        targetingTable.add(lastButton);
        targetingTable.add(strongestButton);
        targetingTable.add(weakestButton);
        towerTable.add(targetingTable).row();
        comboUpgrade.right().add(new Label("Enable Combo-ing", Main.skin)).colspan(3).row();
        Label redLabel = new Label("50", Main.skin);
        redLabel.setAlignment(Align.center);
        redLabel.setColor(1, 0, 0, 1);
        comboUpgrade.add(redLabel).expand().fill();
        Label blueLabel = new Label("50", Main.skin);
        blueLabel.setAlignment(Align.center);
        blueLabel.setColor(0, 0, 1, 1);
        comboUpgrade.add(blueLabel).expand().fill();
        Label yellowLabel = new Label("50", Main.skin);
        yellowLabel.setAlignment(Align.center);
        yellowLabel.setColor(1, 1, 0, 1);
        comboUpgrade.add(yellowLabel).expand().fill();
        towerTable.add(comboUpgrade).row();
        Table towerStatsTable = new Table(Main.skin);
        towerStatsTable.add(new Label("Kills: ", Main.skin));
        towerStatsTable.add(towerKillsLabel).row();
        towerStatsTable.add(new Label("Shots: ", Main.skin));
        towerStatsTable.add(towerShotsLabel).row();
        towerTable.add(towerStatsTable).row();
        generatorTable.pad(2);
        Table generatorUpgrades = new Table(Main.skin);
        generatorUpgrades.add(new Label("Extractors: ", Main.skin)).right();
        generatorUpgrades.add(extractorsLabel).right();
        generatorUpgrades.add(extractorsBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        generatorUpgrades.add(extractorsUpgrade).row();
        generatorUpgrades.add(new Label("Efficiency: ", Main.skin)).right();
        generatorUpgrades.add(efficiencyLabel).right();
        generatorUpgrades.add(efficiencyBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        generatorUpgrades.add(efficiencyUpgrade).row();
        generatorTable.add(generatorUpgrades).row();
        Table generatorStatsTable = new Table(Main.skin);
        generatorStatsTable.add(new Label("Generated Resources: ", Main.skin));
        generatorStatsTable.add(generatedLabel).row();
        generatorTable.add(generatorStatsTable);
        generalTable.top().add(towerTable).fill().row();
        generalTable.add(towerCircle).expandY().size(StatsCircle.SIZE).row();
        generalTable.add(sellButton).expandX().fill().row();
        generalTable.add(moveButton).expandX().fill().row();
        buildingTable.setVisible(false);

        comboTable.setName("Combos");
        Table combos = new Table(Main.skin);
        combos.top();
        for (Combo combo : Combo.values()) {
            combos.add(combo.table).spaceBottom(2).expandX().fill().row();
        }
        comboPane = new ScrollPane(combos, Main.skin);
        comboPane.setSmoothScrolling(true);
        comboPane.setScrollingDisabled(true, false);
        comboPane.setFadeScrollBars(false);
        comboTable.add(comboPane).spaceTop(2).expand().fill();

        towerComboTable.setName("Combo Tower");
        towerComboTable.add(comboBar).minWidth(1).expandX().fill();
        towerComboTable.add(comboLabel).width(25).spaceLeft(2).row();
        Table temp = new Table(Main.skin);
        temp.top();
        towerComboPane = new ScrollPane(temp, Main.skin);
        towerComboPane.setSmoothScrolling(true);
        towerComboPane.setScrollingDisabled(true, false);
        towerComboPane.setFadeScrollBars(false);
        towerComboTable.add(towerComboPane).colspan(2).spaceTop(2).expand().fill();

        menu.left().add(buttonsTable).expandY().fill();
        menu.add(settingsTable).expand().fill();

        menu.addListener(new ClickListener());
        restart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen(new Level(level.prototype));
            }
        });
        main.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen(MenuScreen.instance);
            }
        });
        sellButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level.selectedBuilding.sell();
            }
        });
        moveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level.movingBuilding = !level.movingBuilding;
            }
        });
        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(settingsTable);
            }
        });
        comboButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(comboTable);
            }
        });
        generalButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(generalTable);
            }
        });
        towerComboButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(towerComboTable);
            }
        });
        damageUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.selectedBuilding instanceof Tower) {
                    ((Tower) level.selectedBuilding).upgradeDamage();
                    select();
                }
            }
        });
        rangeUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.selectedBuilding instanceof Tower) {
                    ((Tower) level.selectedBuilding).upgradeRange();
                    select();
                }
            }
        });
        speedUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.selectedBuilding instanceof Tower) {
                    ((Tower) level.selectedBuilding).upgradeSpeed();
                    select();
                }
            }
        });
        nearestButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).targeting = Tower.Targeting.NEAREST;
            }
        });
        firstButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).targeting = Tower.Targeting.FIRST;
            }
        });
        lastButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).targeting = Tower.Targeting.LAST;
            }
        });
        strongestButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).targeting = Tower.Targeting.STRONGEST;
            }
        });
        weakestButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).targeting = Tower.Targeting.WEAKEST;
            }
        });
        comboUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Tower) level.selectedBuilding).comboUpgrade();
                select();
            }
        });
        extractorsUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.selectedBuilding instanceof Generator) {
                    ((Generator) level.selectedBuilding).upgradeExtractors();
                    select();
                }
            }
        });
        efficiencyUpgrade.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (level.selectedBuilding instanceof Generator) {
                    ((Generator) level.selectedBuilding).upgradeEfficiency();
                    select();
                }
            }
        });
    }

    private static void switchTab(Table tab) {
        menu.getTitleLabel().setText(tab.getName());
        menu.getCell(currentTab).setActor(tab);
        currentTab = tab;
    }

    public static void deselect() {
        buildingTable.setVisible(false);
        if (currentTab == generalTable || currentTab == towerComboTable) {
            settingsButton.setChecked(true);
            switchTab(settingsTable);
        }
    }

    public static void select() {
        if (level.selectedBuilding == null) {
            deselect();
            return;
        }
        buildingTable.setVisible(true);
        buildingLabel.setText(level.selectedBuilding instanceof Tower ? "Tower" : "Generator");
        towerCircle.update(level.selectedBuilding);
        if (level.selectedBuilding instanceof Tower) {
            Tower tower = ((Tower) level.selectedBuilding);
            damageLabel.setText("" + (int) tower.getDamage());
            damageBar.setValue(tower.getDamageIndex());
            damageUpgrade.setText("" + (tower.getDamageCost() == -1 ? "infinity" : tower.getDamageCost()));
            rangeLabel.setText("" + (int) tower.getRange());
            rangeBar.setValue(tower.getRangeIndex());
            rangeUpgrade.setText("" + (tower.getRangeCost() == -1 ? "infinity" : tower.getRangeCost()));
            speedLabel.setText("" + (int) tower.getSpeed());
            speedBar.setValue(tower.getSpeedIndex());
            speedUpgrade.setText("" + (tower.getSpeedCost() == -1 ? "infinity" : tower.getSpeedCost()));
            switch(tower.targeting) {
                case NEAREST:
                    nearestButton.setChecked(true);
                    break;
                case FIRST:
                    firstButton.setChecked(true);
                    break;
                case LAST:
                    lastButton.setChecked(true);
                    break;
                case STRONGEST:
                    strongestButton.setChecked(true);
                    break;
                case WEAKEST:
                    weakestButton.setChecked(true);
                    break;
            }
            comboUpgrade.setBackground(Main.skin.getDrawable(tower.comboUpgrade ? "default-round-down" : "default-round"));
            if (generalTable.getCell(generatorTable) != null)
                generalTable.getCell(generatorTable).setActor(towerTable);
            towerComboButton.setVisible(tower.comboUpgrade);
            if (!tower.comboUpgrade && currentTab == towerComboTable) {
                settingsButton.setChecked(true);
                switchTab(settingsTable);
            }
            Table combos = (Table) towerComboPane.getWidget();
            combos.clearChildren();
            for (Combo combo : tower.getCombos()) {
                combos.add(combo.table).spaceBottom(2).expandX().fill().row();
            }
        } else {
            Generator generator = ((Generator) level.selectedBuilding);
            Color color = generator.type == Level.Resource.RED ? Color.RED : generator.type == Level.Resource.BLUE ? Color.BLUE : Color.YELLOW;
            extractorsLabel.setText("" + (generator.getExtractors() + 1));
            extractorsLabel.setColor(color);
            extractorsBar.setValue(generator.getExtractors());
            extractorsBar.setColor(color);
            extractorsUpgrade.setText("" + (generator.getExtractorCost() == -1 ? "infinity" : generator.getExtractorCost()));
            extractorsUpgrade.setColor(color);
            efficiencyLabel.setText("" + (generator.getEfficiency()));
            efficiencyLabel.setColor(color);
            efficiencyBar.setValue(generator.getEfficiencyIndex());
            efficiencyBar.setColor(color);
            efficiencyUpgrade.setText("" + (generator.getEfficiencyCost() == -1 ? "infinity" : generator.getEfficiencyCost()));
            efficiencyUpgrade.setColor(color);
            if (generalTable.getCell(towerTable) != null)
                generalTable.getCell(towerTable).setActor(generatorTable);
            towerComboButton.setVisible(false);
        }
        generalTable.setName(level.selectedBuilding.getName());
        if (currentTab == generalTable) menu.getTitleLabel().setText(level.selectedBuilding.getName());
    }

    public static void update() {
        totalKillsLabel.setText("" + level.totalKills);
        fpsLabel.setText("" + Gdx.graphics.getFramesPerSecond());
        if (level.selectedBuilding != null && level.selectedBuilding instanceof Tower) {
            Tower tower = ((Tower) level.selectedBuilding);
            towerKillsLabel.setText("" + tower.kills);
            towerShotsLabel.setText("" + tower.shots);
            if (((Tower) level.selectedBuilding).comboUpgrade) {
                float time = ((Tower) level.selectedBuilding).comboTimer;
                comboLabel.setText(time >= 100 ? "READY" : (int) time + "%");
                comboBar.setValue(time);
            }
        } else if (level.selectedBuilding != null){
            Generator generator = ((Generator) level.selectedBuilding);
            generatedLabel.setText("" + generator.generated);
        }
    }

    public static void init(Level level) {
        Menu.level = level;
        towerCircle.init(level);

        level.ui.addActor(menu);
    }

    public static void toggle() {
        menu.act(1);
        if (menu.isVisible()) {
            menu.addAction(Actions.sequence(Actions.alpha(0, .25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    menu.setVisible(false);
                }
            })));
        } else {
            menu.addAction(Actions.sequence(Actions.run(new Runnable() {
                @Override
                public void run() {
                    menu.setVisible(true);
                }
            }), Actions.alpha(.5f, .25f)));
        }
    }
}

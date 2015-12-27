package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Menu {
    private static final Label buildingLabel = new Label("Building", Main.skin);
    private static final TextButton settingsButton = new TextButton("Settings", Main.skin, "toggle");
    private static final TextButton generalButton = new TextButton("General", Main.skin, "toggle");
    private static final Table buildingTable = new Table(Main.skin);
    private static final Table towerTable = new Table(Main.skin);
    private static final Label damageLabel = new Label("0", Main.skin);
    private static final TextButton damageUpgrade = new TextButton("Upgrade", Main.skin);
    private static final Label rangeLabel = new Label("0", Main.skin);
    private static final TextButton rangeUpgrade = new TextButton("Upgrade", Main.skin);
    private static final Label speedLabel = new Label("0", Main.skin);
    private static final TextButton speedUpgrade = new TextButton("Upgrade", Main.skin);
    private static final ProgressBar damageBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final ProgressBar rangeBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final ProgressBar speedBar = new ProgressBar(0, 11, 1, false, Main.skin);
    private static final Table settingsTable = new Table(Main.skin);
    private static final Table generalTable = new Table(Main.skin);
    private static final Window menu = new Window("Settings", Main.skin, "large");
    private static Table currentTab;

    private static Level level;

    static {
        new ButtonGroup(settingsButton, generalButton);

        menu.setVisible(false);
        menu.setSize(300, 200);
        menu.setPosition(20, 200);
        menu.setColor(1, 1, 1, 0);

        Table buttonsTable = new Table(Main.skin);
        buttonsTable.setBackground(Main.skin.getDrawable("default-round"));
        buttonsTable.top().add(settingsButton).expandX().fill().row();
        buildingLabel.setAlignment(Align.center);
        buildingTable.center().add(buildingLabel).expandX().fill().spaceBottom(4).row();
        buildingTable.add(generalButton).expandX().fill();
        buttonsTable.add(buildingTable).spaceTop(8).expandX().fill().row();
        settingsButton.setChecked(true);

        settingsTable.setName("Settings");
        Button restart = new TextButton("Restart Level", Main.skin);
        settingsTable.top().add(restart).expandX().fill();
        currentTab = settingsTable;

        Button sellButton = new TextButton("Sell Tower", Main.skin);
        generalTable.top().add(sellButton).colspan(3).expandX().fill().row();
        towerTable.pad(2);
        towerTable.add(new Label("Damage: ", Main.skin)).right();
        damageLabel.setColor(1, 0, 0, 1);
        towerTable.add(damageLabel).right();
        damageBar.setColor(1, 0, 0, 1);
        towerTable.add(damageBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        damageUpgrade.setColor(1, 0, 0, 1);
        towerTable.add(damageUpgrade).row();
        towerTable.add(new Label("Range: ", Main.skin)).right();
        rangeLabel.setColor(0, 0, 1, 1);
        towerTable.add(rangeLabel).right();
        rangeBar.setColor(0, 0, 1, 1);
        towerTable.add(rangeBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        rangeUpgrade.setColor(0, 0, 1, 1);
        towerTable.add(rangeUpgrade).row();
        towerTable.add(new Label("Speed: ", Main.skin)).right();
        speedLabel.setColor(1, 1, 0, 1);
        towerTable.add(speedLabel).right();
        speedBar.setColor(1, 1, 0, 1);
        towerTable.add(speedBar).minWidth(1).space(0, 2, 0, 2).expandX().fill();
        speedUpgrade.setColor(1, 1, 0, 1);
        towerTable.add(speedUpgrade).row();
        generalTable.add(towerTable).fill();
        towerTable.setVisible(false);
        buildingTable.setVisible(false);

        menu.left().add(buttonsTable).expandY().fill();
        menu.add(settingsTable).expand().fill();

        restart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen(new Level(level.prototype));
            }
        });
        sellButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level.selectedBuilding.sell();
            }
        });
        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(settingsTable);
            }
        });
        generalButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(generalTable);
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
    }

    private static void switchTab(Table tab) {
        menu.getTitleLabel().setText(tab.getName());
        menu.getCell(currentTab).setActor(tab);
        currentTab = tab;
    }

    public static void deselect() {
        buildingTable.setVisible(false);
        if (currentTab == generalTable) {
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
        towerTable.setVisible(level.selectedBuilding instanceof Tower);
        if (level.selectedBuilding instanceof Tower) {
            Tower tower = ((Tower) level.selectedBuilding);
            damageLabel.setText("" + (int) tower.getDamage());
            damageBar.setValue(tower.getDamageIndex());
            damageUpgrade.setText("" + tower.getDamageCost());
            rangeLabel.setText("" + (int) tower.getRange());
            rangeBar.setValue(tower.getRangeIndex());
            rangeUpgrade.setText("" + tower.getRangeCost());
            speedLabel.setText("" + (int) tower.getSpeed());
            speedBar.setValue(tower.getSpeedIndex());
            speedUpgrade.setText("" + tower.getSpeedCost());
        }
        generalTable.setName(level.selectedBuilding.getName());
        if (currentTab == generalTable) menu.getTitleLabel().setText(level.selectedBuilding.getName());
    }

    public static void init(Level level) {
        Menu.level = level;

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

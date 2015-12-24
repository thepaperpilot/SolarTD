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
    private static final Table towerTable = new Table(Main.skin);
    private static final Table settingsTable = new Table(Main.skin);
    private static final Table generalTable = new Table(Main.skin);
    private static final ButtonGroup tabs = new ButtonGroup(settingsButton, generalButton);
    private static final Window menu = new Window("Settings", Main.skin, "large");
    private static Table currentTab;

    private static Level level;

    static {
        menu.setVisible(false);
        menu.setSize(300, 200);
        menu.setPosition(20, 200);
        menu.setColor(1, 1, 1, 0);

        Table buttonsTable = new Table(Main.skin);
        buttonsTable.setBackground(Main.skin.getDrawable("default-round"));
        buttonsTable.top().add(settingsButton).expandX().fill().row();
        buildingLabel.setAlignment(Align.center);
        towerTable.center().add(buildingLabel).expandX().fill().spaceBottom(4).row();
        towerTable.add(generalButton).expandX().fill();
        buttonsTable.add(towerTable).spaceTop(8).expandX().fill().row();
        settingsButton.setChecked(true);

        settingsTable.setName("Settings");
        Button restart = new TextButton("Restart Level", Main.skin);
        settingsTable.top().add(restart).expandX().fill();
        currentTab = settingsTable;

        Button sellButton = new TextButton("Sell Tower", Main.skin);
        generalTable.top().add(sellButton).expandX().fill();
        towerTable.setVisible(false);

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
    }

    private static void switchTab(Table tab) {
        menu.getTitleLabel().setText(tab.getName());
        menu.getCell(currentTab).setActor(tab);
        currentTab = tab;
    }

    public static void deselect() {
        towerTable.setVisible(false);
        if (currentTab == generalTable) {
            settingsButton.setChecked(true);
            switchTab(settingsTable);
        }
    }

    public static void select() {
        towerTable.setVisible(true);
        buildingLabel.setText(level.selectedBuilding instanceof Tower ? "Tower" : "Generator");
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

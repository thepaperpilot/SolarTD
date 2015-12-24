package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Menu {
    private static final TextButton settingsButton = new TextButton("Settings", Main.skin, "toggle");
    private static final ButtonGroup tabs = new ButtonGroup(settingsButton);
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
        settingsButton.setChecked(true);

        final Table settingsTable = new Table(Main.skin);
        settingsTable.setName("Settings");
        Button restart = new TextButton("Restart Level", Main.skin);
        settingsTable.top().add(restart).expandX().fill();
        currentTab = settingsTable;

        menu.left().add(buttonsTable).expandY().fill();
        menu.add(settingsTable).expand().fill();

        restart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen(new Level(level.prototype));
            }
        });
        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(settingsTable);
            }
        });
    }

    private static void switchTab(Table tab) {
        menu.getTitleLabel().setText(tab.getName());
        menu.getCell(currentTab).setActor(tab);
        currentTab = tab;
    }

    public static void deselect() {
    }

    public static void select() {
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

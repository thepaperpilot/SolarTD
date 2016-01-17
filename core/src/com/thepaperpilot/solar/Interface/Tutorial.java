package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Main;

public class Tutorial {
    public static final Table tutorial = new Table(Main.skin);
    private static final TextButton generalP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton hudP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton menuP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton enemiesP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton towersP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton generatorsP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton combosP1Button = new TextButton("Page 1", Main.skin, "toggle");
    private static final TextButton returnButton = new TextButton("Return", Main.skin);
    private static final Table generalP1Table = new Table(Main.skin);
    private static final Table hudP1Table = new Table(Main.skin);
    private static final Table menuP1Table = new Table(Main.skin);
    private static final Table enemiesP1Table = new Table(Main.skin);
    private static final Table towersP1Table = new Table(Main.skin);
    private static final Table generatorsP1Table = new Table(Main.skin);
    private static final Table combosP1Table = new Table(Main.skin);

    private static Table currentTab = generalP1Table;

    static {
        new ButtonGroup(generalP1Button, hudP1Button, menuP1Button, enemiesP1Button, towersP1Button, generatorsP1Button, combosP1Button);

        tutorial.setVisible(false);
        tutorial.setFillParent(true);

        Table buttonsTable = new Table(Main.skin);
        buttonsTable.setBackground(Main.skin.getDrawable("default-round"));
        buttonsTable.top().add(new Label("General", Main.skin)).row();
        buttonsTable.add(generalP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("HUD", Main.skin)).row();
        buttonsTable.add(hudP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("Menu", Main.skin)).row();
        buttonsTable.add(menuP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("Enemies", Main.skin)).row();
        buttonsTable.add(enemiesP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("Towers", Main.skin)).row();
        buttonsTable.add(towersP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("Generators", Main.skin)).row();
        buttonsTable.add(generatorsP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(new Label("Combos", Main.skin)).row();
        buttonsTable.add(combosP1Button).expandX().fill().spaceBottom(4).row();
        buttonsTable.add(returnButton).expand().fillX().bottom();
        generalP1Button.setChecked(true);

        generalP1Table.setBackground(Main.skin.getDrawable("default-round"));
        hudP1Table.setBackground(Main.skin.getDrawable("default-round"));
        menuP1Table.setBackground(Main.skin.getDrawable("default-round"));
        enemiesP1Table.setBackground(Main.skin.getDrawable("default-round"));
        towersP1Table.setBackground(Main.skin.getDrawable("default-round"));
        generatorsP1Table.setBackground(Main.skin.getDrawable("default-round"));
        combosP1Table.setBackground(Main.skin.getDrawable("default-round"));

        // generalP1
        generalP1Table.top().add(new Label("Solar Tower Defense", Main.skin, "large")).left().spaceBottom(4).row();
        Label intro = new Label("Welcome to Solar TD! The goal in this game, as in most Tower Defense games, is to defeat enemies along a path before they reach the end.\n\n" +
                "You can accomplish this by placing towers such that they can attack the enemies along the path. But, in order to place these towers, as well as do many other things, you need resources.\n\n" +
                "In this game there are three types of resources- they are red, blue, and yellow. Each resource corresponds to a different type of tower, and a different stat you can upgrade.\n\n" +
                "You will slowly gather resources over time. But, you can use generators to augment your resource gathering. But, generators are expensive. Make sure you have adequate defenses before building generators!", Main.skin);
        intro.setWrap(true);
        generalP1Table.add(intro).expandX().fill().row();
        generalP1Table.add(new Label("Good Luck!", Main.skin, "large")).spaceTop(8);

        // hudP1
        hudP1Table.top().add(new Label("HUD", Main.skin, "large")).left().spaceBottom(4).row();
        Label hudLabel = new Label("While playing a Heads Up Display will appear at the bottom of the screen. It will have several important buttons and monitors.\n\n" +
                "The menu button opens and closes the menu. The pause button toggles whether the game is paused or resumed. You can also use the ESC or SPACE keys instead of those buttons, respectively.\n\n" +
                "The towers and generators allow you to select what building you are currently placing. Similarly you can press the number keys 1-6 instead.\n\n" +
                "The resources show you how much you have of each of the three resource types. The lives and wave displays show you how many lives you have, and what wave you're on.\n\n" +
                "The next wave display shows you what enemy is coming next, and how long until it comes. Clicking this display will send the next wave immediately.", Main.skin);
        hudLabel.setWrap(true);
        hudP1Table.add(hudLabel).expandX().fill().spaceTop(8).row();

        // menuP1
        menuP1Table.top().add(new Label("Menu", Main.skin, "large")).left().spaceBottom(4).row();
        Label menuLabel = new Label("The menu is where you can change your game settings, look at all the combos, monitor and configure your towers, and more. It is incredibly useful, and should be open most of the time. You can open and close it using the menu button in the HUD, or by pressing the ESC button on your keyboard.\n\n" +
                "The menu has tabs on the left for the various things you can do in the menu, similar to this tutorial.\n\n" +
                "The settings tab is where you can restart or quit a level, as well as change the game settings, and look at your game statistics.\n\n" +
                "The combos tab shows you a list of all the combos in the game, as a reference.\n\n" +
                "If you have a building selected, you will see a tab for it where you can configure the building, upgrade it, and see its statistics.\n\n" +
                "If you have a tower selected with the combo ability, there will be a tab which will show you all the combos that tower can currently do, as well as see what combo it will fire next and when.\n\n" +
                "The final tab shows you what enemies are in the current wave, and the next one.", Main.skin);
        menuLabel.setWrap(true);
        menuP1Table.top().add(menuLabel).expandX().fill();

        // enemiesP1
        enemiesP1Table.top().add(new Label("Enemies", Main.skin, "large")).left().spaceBottom(4).row();
        Table enemyTypes = new Table(Main.skin);
        enemyTypes.add(new Image(Main.getDrawable("alien"))).size(32).expandX().spaceBottom(4);
        enemyTypes.add(new Image(Main.getDrawable("angry"))).size(32).expandX().spaceBottom(4);
        enemyTypes.add(new Image(Main.getDrawable("speed"))).size(32).expandX().spaceBottom(4).row();
        enemyTypes.add(new Label("Normal", Main.skin), new Label("Heavy", Main.skin), new Label("Light", Main.skin));
        enemyTypes.row();
        Label alienLabel = new Label("Average health and speed, comes in moderately sized waves", Main.skin);
        alienLabel.setAlignment(Align.top);
        alienLabel.setWrap(true);
        enemyTypes.add(alienLabel).pad(8).expandX().fill();
        Label angryLabel = new Label("Lots of health but very slow. Generally comes alone", Main.skin);
        angryLabel.setAlignment(Align.top);
        angryLabel.setWrap(true);
        enemyTypes.add(angryLabel).pad(8).expandX().fill();
        Label speedLabel = new Label("Very low health, very fast. Comes in swarms", Main.skin);
        speedLabel.setAlignment(Align.top);
        speedLabel.setWrap(true);
        enemyTypes.add(speedLabel).pad(8).expandX().fill();
        enemiesP1Table.add(enemyTypes).expandX().fill().row();
        Label enemyLabel = new Label("Enemies will enter the stage on the green end of the path, and exit through the right end. When they exit you will lose lives. When your lives go below 0, you will lose. Fortunately you gain lives every wave, but not many. \n\n" +
                "Different towers and combos are more effective at killing different enemies. For example, a fast tower is better against light enemies. \n\n" +
                "Every enemy will have a circle beneath them to represent their health. The less of the circle that remains, the closer to death the enemy is. \n\n" +
                "In the menu, under the next wave tab, you can see what types of enemies the next wave will send, as well as their health, speed, and numbers.", Main.skin);
        enemyLabel.setWrap(true);
        enemiesP1Table.add(enemyLabel).expandX().fill().spaceTop(8);

        // towersP1
        towersP1Table.top().add(new Label("Towers", Main.skin, "large")).left().spaceBottom(4).row();
        Table towerTypes = new Table(Main.skin);
        towerTypes.add(new Image(Main.getDrawable("towers/red"))).size(32).right().expandX().spaceBottom(4);
        towerTypes.add(new Image(Main.getDrawable("towers/redUp"))).size(32).left().expandX().spaceBottom(4);
        towerTypes.add(new Image(Main.getDrawable("towers/blue"))).size(32).right().expandX().spaceBottom(4);
        towerTypes.add(new Image(Main.getDrawable("towers/blueUp"))).size(32).left().expandX().spaceBottom(4);
        towerTypes.add(new Image(Main.getDrawable("towers/yellow"))).size(32).right().expandX().spaceBottom(4);
        towerTypes.add(new Image(Main.getDrawable("towers/yellowUp"))).size(32).left().expandX().spaceBottom(4).row();
        Label redLabel = new Label("The red tower deals immediate damage to enemies and has a moderate rate of fire and range. It's special ability allows it to deal splash damage around the target.", Main.skin);
        redLabel.setAlignment(Align.top);
        redLabel.setWrap(true);
        towerTypes.add(redLabel).colspan(2).pad(8).expandX().fill();
        Label blueLabel = new Label("The blue tower shoots missiles across a far range that deal large amounts of damage to enemies. It has a slow rate of fire. It's special ability allows it to keep missiles in a holding pattern when there aren't any enemies within range.", Main.skin);
        blueLabel.setAlignment(Align.top);
        blueLabel.setWrap(true);
        towerTypes.add(blueLabel).colspan(2).pad(8).expandX().fill();
        Label yellowLabel = new Label("The yellow tower applies a slowing effect to all enemies in an area. It has a high rate of fire but very small range. Damage effects how much it slows, and speed how long the slow effect lasts. Its special ability allows it to deal damage as well as slow.", Main.skin);
        yellowLabel.setAlignment(Align.top);
        yellowLabel.setWrap(true);
        towerTypes.add(yellowLabel).colspan(2).pad(8).expandX().fill();
        towersP1Table.add(towerTypes).expandX().fill().row();
        Label towerLabel = new Label("Towers can be upgraded in terms of damage, range, and speed in the menu. A circle underneath each tower shows how much each stat is upgraded.\n\n" +
                "When you upgrade the stat corresponding to the type of tower it is, you enable the tower's special ability.\n\n" +
                "There is also an upgrade that allows it to combo with nearby towers for more advanced attacks. A tower with the combo ability has a golden star in its center instead of a black dot.\n\n" +
                "You can also sell or move towers in the menu, or by pressing S or M, respectively.", Main.skin);
        towerLabel.setWrap(true);
        towersP1Table.add(towerLabel).expandX().fill();

        // generatorsP1
        generatorsP1Table.top().add(new Label("Generators", Main.skin, "large")).left().spaceBottom(4).row();
        Table generatorTypes = new Table(Main.skin);
        generatorTypes.add(new Image(Main.getDrawable("towers/redGen"))).size(64).expandX().spaceBottom(4);
        generatorTypes.add(new Image(Main.getDrawable("towers/blueGen"))).size(64).expandX().spaceBottom(4);
        generatorTypes.add(new Image(Main.getDrawable("towers/yellowGen"))).size(64).expandX().spaceBottom(4);
        generatorsP1Table.add(generatorTypes).expandX().fill().row();
        Label genLabel = new Label("Generators allow you to gather resources at a higher than normal rate. However, they take up a lot of space and cost a lot.\n\n" +
                "They have 2 upgrades. One improves how many resources it gathers at a time, and the other improves its gathering frequency.", Main.skin);
        genLabel.setWrap(true);
        generatorsP1Table.add(genLabel).expandX().fill();

        // combosP1
        combosP1Table.top().add(new Label("Combos", Main.skin, "large")).left().spaceBottom(4).row();
        Label combosLabel = new Label("Combos are vital if you want to last a long time in the game. In the menu you can go to the combos tab for a reference on how to make all the combos.\n\n" +
                "To make a combo, a primary tower, which is the first one when looking at the reference sheet, must have several other towers within its combo radius. All towers involved must also have the combo ability.\n\n" +
                "When placing or moving towers, lines appear between towers to illustrate they are within combo range. There is also a light circle around the selected tower showing the same thing.\n\n" +
                "There is a timer for combos on each tower. The timer is longer for combos with more towers. After a successful combo, the tower chooses a random one from all combos it qualifies for.\n\n" +
                "Moving a tower or selling a nearby tower will reset a tower's combo timer.", Main.skin);
        combosLabel.setWrap(true);
        combosP1Table.add(combosLabel).expandX().fill();

        tutorial.left().add(buttonsTable).expandY().fill();
        tutorial.add(generalP1Table).expand().fill();

        generalP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(generalP1Table);
            }
        });
        hudP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(hudP1Table);
            }
        });
        menuP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(menuP1Table);
            }
        });
        enemiesP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(enemiesP1Table);
            }
        });
        towersP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(towersP1Table);
            }
        });
        generatorsP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(generatorsP1Table);
            }
        });
        combosP1Button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                switchTab(combosP1Table);
            }
        });
        returnButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                tutorial.setVisible(false);
                Main.getSound("select").play(Main.volume);
            }
        });
    }

    private static void switchTab(Table tab) {
        Main.getSound("select").play(Main.volume);
        tutorial.getCell(currentTab).setActor(tab);
        currentTab = tab;
    }
}

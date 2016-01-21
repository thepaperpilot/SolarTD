package com.thepaperpilot.solar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Enemy;
import com.thepaperpilot.solar.Interface.Tutorial;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Levels.Wave;

import java.util.ArrayList;

public class MenuScreen implements Screen{
    public static final MenuScreen instance = new MenuScreen();

    private final Stage stage;
    private final Stage ui;
    private final ParticleEffect stars;

    private int level = 1;

    public MenuScreen() {
        stage = new Stage(new StretchViewport(320, 180));
        ui = new Stage(new StretchViewport(480, 270));
        ui.addActor(Tutorial.tutorial);

        Image bg = new Image(Main.getDrawable("title"));
        bg.setScale(.25f);
        stage.addActor(bg);

        Table ui = new Table(Main.skin);
        ui.setFillParent(true);
        ui.bottom().pad(8);

        Table levels = new Table(Main.skin);
        final TextButton level1 = new TextButton("1", Main.skin, "toggle-large");
        level1.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 1;
                Main.getSound("select").play(Main.volume);
            }
        });
        levels.add(level1).spaceRight(4);
        final TextButton level2 = new TextButton("2", Main.skin, "toggle-large");
        level2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 2;
                Main.getSound("select").play(Main.volume);
            }
        });
        levels.add(level2).spaceRight(4);
        final TextButton level3 = new TextButton("3", Main.skin, "toggle-large");
        level3.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 3;
                Main.getSound("select").play(Main.volume);
            }
        });
        levels.add(level3).spaceRight(4);
        final TextButton level4 = new TextButton("4", Main.skin, "toggle-large");
        level4.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 4;
                Main.getSound("select").play(Main.volume);
            }
        });
        levels.add(level4).spaceRight(4);
        final TextButton level5 = new TextButton("5", Main.skin, "toggle-large");
        level5.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 5;
                Main.getSound("select").play(Main.volume);
            }
        });
        levels.add(level5).spaceRight(4);
        ui.add(new Label("Map:", Main.skin)).left().row();
        ui.add(levels).spaceBottom(4).row();

        Table difficulty = new Table(Main.skin);
        TextButton easy = new TextButton("Easy", Main.skin, "toggle");
        easy.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Level.difficulty = -1;
                Main.getSound("select").play(Main.volume);
            }
        });
        TextButton normal = new TextButton("Normal", Main.skin, "toggle");
        normal.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Level.difficulty = 0;
                Main.getSound("select").play(Main.volume);
            }
        });
        TextButton hard = new TextButton("Hard", Main.skin, "toggle");
        hard.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Level.difficulty = 1;
                Main.getSound("select").play(Main.volume);
            }
        });
        difficulty.add(easy).spaceRight(4);
        difficulty.add(normal).spaceRight(4);
        difficulty.add(hard).spaceRight(4).row();
        new ButtonGroup<>(easy, normal, hard);
        if (Level.difficulty == 1) hard.setChecked(true);
        else if (Level.difficulty == 0) normal.setChecked(true);
        else easy.setChecked(true);
        ui.add(new Label("Difficulty:", Main.skin)).left().row();
        ui.add(difficulty).spaceBottom(4).row();

        Button tutorial = new TextButton("Tutorial", Main.skin);
        tutorial.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Tutorial.tutorial.setVisible(true);
                Main.getSound("select").play(Main.volume);
            }
        });
        ui.add(tutorial).spaceBottom(4).row();

        Button start = new TextButton("Start Game", Main.skin, "large");
        start.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Enemy.EnemyPrototype normal1 = new Enemy.EnemyPrototype();
                normal1.name = "alien";
                normal1.speed = 1;
                normal1.count = 12;
                normal1.health = 1;
                Enemy.EnemyPrototype normal2 = new Enemy.EnemyPrototype();
                normal2.name = "alien";
                normal2.speed = 1;
                normal2.count = 8;
                normal2.health = 2;
                Enemy.EnemyPrototype fast1 = new Enemy.EnemyPrototype();
                fast1.name = "speed";
                fast1.speed = 1.5f;
                fast1.count = 16;
                fast1.health = .5f;
                Enemy.EnemyPrototype boss1 = new Enemy.EnemyPrototype();
                boss1.name = "angry";
                boss1.speed = .5f;
                boss1.count = 1;
                boss1.health = 12;
                Enemy.EnemyPrototype boss2 = new Enemy.EnemyPrototype();
                boss2.name = "angry";
                boss2.speed = .5f;
                boss2.count = 3;
                boss2.health = 6;
                ArrayList<Wave.WavePrototype> wavePrototypes = new ArrayList<>();
                Wave.WavePrototype wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{fast1};
                wavePrototype.enemyDistance = .5f;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal2};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{boss1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{fast1};
                wavePrototype.enemyDistance = .5f;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{fast1};
                wavePrototype.enemyDistance = .5f;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{boss2};
                wavePrototype.enemyDistance = 4;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal2};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{fast1};
                wavePrototype.enemyDistance = .75f;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{normal1};
                wavePrototype.enemyDistance = 1;
                wavePrototypes.add(wavePrototype);
                wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{boss1, normal1, fast1};
                wavePrototype.enemyDistance = 1.5f;
                wavePrototypes.add(wavePrototype);
                Level.LevelPrototype levelPrototype = new Level.LevelPrototype();
                levelPrototype.waves = wavePrototypes.toArray(new Wave.WavePrototype[wavePrototypes.size()]);
                switch (level) {
                    default:case 1:
                        levelPrototype.width = 1280;
                        levelPrototype.height = 720;
                        levelPrototype.path = new float[]{140, 720, 140, 200, 340, 200, 340, 520, 540, 520, 540, 200, 740, 200, 740, 520, 940, 520, 940, 200, 1140, 200, 1140, 720};
                        break;
                    case 2:
                        levelPrototype.width = 1280;
                        levelPrototype.height = 720;
                        levelPrototype.path = new float[]{0, 620, 1180, 620, 1180, 200, 100, 200, 100, 720};
                        break;
                    case 3:
                        levelPrototype.width = 1280;
                        levelPrototype.height = 720;
                        levelPrototype.path = new float[]{1080, 720, 1080, 200, 200, 200, 200, 620, 880, 620, 880, 400, 400, 400, 400, 510, 1280, 510};
                        break;
                    case 4:
                        levelPrototype.width = 980;
                        levelPrototype.height = 540;
                        levelPrototype.path = new float[]{980, 200, 500, 200, 300, 300, 300, 200, 100, 400, 980, 400};
                        break;
                    case 5:
                        levelPrototype.width = 1920;
                        levelPrototype.height = 1080;
                        levelPrototype.path = new float[]{0, 200, 980, 690, 1920, 980};
                        break;
                }
                Main.changeScreen(new Level(levelPrototype));
                Main.getSound("select").play(Main.volume);
            }
        });
        ui.add(start);

        new ButtonGroup(level1, level2, level3, level4, level5);
        level1.setChecked(true);

        stage.addActor(ui);

        stars = new ParticleEffect();
        stars.load(Gdx.files.internal("particles/menu.p"), Gdx.files.internal("particles/"));
        stars.setPosition(160, 90);
        stars.scaleEffect(.25f);
        for (int i = 0; i < 100; i++) {
            stars.update(.1f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, stage));
    }

    @Override
    public void render(float delta) {
        Batch batch = stage.getBatch();
        batch.begin();
        stars.draw(batch, delta);
        batch.end();

        stage.act();
        stage.draw();

        ui.act();
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        ui.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

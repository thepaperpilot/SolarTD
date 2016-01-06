package com.thepaperpilot.solar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Enemy;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Levels.Wave;

public class MenuScreen implements Screen{
    public static final MenuScreen instance = new MenuScreen();

    private final Stage stage;
    private final ParticleEffect stars;

    private int level = 1;

    public MenuScreen() {
        stage = new Stage(new StretchViewport(320, 180));

        Image bg = new Image(Main.getDrawable("title"));
        bg.setScale(.25f);
        stage.addActor(bg);

        Table levels = new Table(Main.skin);
        levels.setFillParent(true);
        levels.bottom().pad(8);
        TextButton level1 = new TextButton("1", Main.skin, "toggle-large");
        level1.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                level = 1;
            }
        });
        levels.add(level1).spaceBottom(4);

        levels.row();
        Button start = new TextButton("Start Game", Main.skin);
        start.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Level.LevelPrototype levelPrototype = new Level.LevelPrototype();
                levelPrototype.width = 980;
                levelPrototype.height = 540;
                levelPrototype.path = new float[]{980, 200, 500, 200, 300, 300, 300, 200, 100, 400, 980, 400};
                Wave.WavePrototype wavePrototype = new Wave.WavePrototype();
                Enemy.EnemyPrototype enemyPrototype = new Enemy.EnemyPrototype();
                enemyPrototype.name = "alien";
                enemyPrototype.speed = 1;
                enemyPrototype.count = 10;
                enemyPrototype.health = 1;
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{enemyPrototype};
                wavePrototype.enemyDistance = 1;
                levelPrototype.waves = new Wave.WavePrototype[]{wavePrototype};
                Main.changeScreen(new Level(levelPrototype));
                // Not working with GWT for some reason
                // Main.changeScreen(Level.readLevel("level" + level + ".json"));
            }
        });
        levels.add(start);

        new ButtonGroup(level1);
        level1.setChecked(true);

        stage.addActor(levels);

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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Batch batch = stage.getBatch();
        batch.begin();
        stars.draw(batch, delta);
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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

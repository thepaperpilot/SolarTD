package com.thepaperpilot.solar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Levels.Level;

public class MenuScreen implements Screen{
    public static final MenuScreen instance = new MenuScreen();

    private final Batch batch;
    private final Stage stage;
    private final ParticleEffect stars;

    private int level = 1;

    public MenuScreen() {
        batch = new SpriteBatch();
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
                Main.changeScreen(Level.readLevel("level" + level + ".json"));
            }
        });
        levels.add(start);

        new ButtonGroup(level1);
        level1.setChecked(true);

        stage.addActor(levels);

        stars = new ParticleEffect();
        stars.load(Gdx.files.internal("particles/stars.p"), Gdx.files.internal("particles/"));
        stars.setPosition(320, 180);
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
        final Matrix4 trans = new Matrix4();
        trans.scale(Gdx.graphics.getWidth() / 1280, Gdx.graphics.getHeight() / 720, 1);
        batch.setTransformMatrix(trans);
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
        batch.dispose();
        stage.dispose();
    }
}

package com.thepaperpilot.solar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Levels.Wave;

public class MenuScreen implements Screen{
    Batch batch;
    Stage stage;

    ParticleEffect stars;

    public MenuScreen() {
        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(1280, 720));

        stage.addActor(new Image(Main.getDrawable("title")));

        // TODO level select
        Button start = new TextButton("Start Game", Main.skin, "large");
        start.align(Align.center);
        start.addListener(new ClickListener() {
           public void clicked(InputEvent event, float x, float y) {
               Level.LevelPrototype levelPrototype = new Level.LevelPrototype();
               levelPrototype.width = 980;
               levelPrototype.height = 540;
               levelPrototype.path = new float[]{980, 200, 500, 200, 300, 300, 300, 200, 100, 400, 980, 400};
               levelPrototype.waves = new Wave.WavePrototype[]{};
               Main.changeScreen(new Level(levelPrototype));
           }
        });
        start.setPosition(640 - start.getWidth() / 2, 40);
        stage.addActor(start);

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

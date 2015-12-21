package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Level implements Screen {
    private final static Json json = new Json();
    ArrayList<Tower> towers = new ArrayList<>();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    float width;
    float height;
    Stage stage;
    Stage ui;
    private boolean placingTower = true;
    private Tower selected;

    public Level(LevelPrototype levelPrototype) {
        width = levelPrototype.width;
        height = levelPrototype.height;
        stage = new Stage(new StretchViewport(width, height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(1920, 1080));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                Vector2 coords = new Vector2(x, y);
                if(placingTower) {
                    for (Tower tower : towers) {
                        if(tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) { // TODO Make a selected tower thing, and look up its radius
                            return;
                        }
                    }
                    // TODO check if you're clicking the path
                    // TODO check for resources
                    towers.add(new Tower(new Circle(coords, Main.TOWER_RADIUS)));
                    // TODO placingTower = false;
                } else {
                    for (Tower tower : towers) {
                        if(tower.area.contains(coords)) {
                            selected = tower;
                            return;
                        }
                    }
                    // TODO selecting enemies
                }
            }
        });

        // TODO ui

        // TODO enemies

        // TODO resources
    }

    public static Level readLevel(String fileName) {
        // read the level from a JSON file
        return new Level(json.fromJson(LevelPrototype.class, Gdx.files.internal(fileName)));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, stage));
    }

    @Override
    public void render(float delta) {
        // update and render everything
        stage.act(delta);
        stage.draw();
        ui.act(delta);
        ui.draw();

        if(selected != null || placingTower) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Matrix4 transform = new Matrix4();
            transform.scale(Gdx.graphics.getWidth() / width, Gdx.graphics.getHeight() / height, 1);
            shapeRenderer.setTransformMatrix(transform);
            if (selected != null) {
                Gdx.gl20.glLineWidth(4);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 1, .5f);
                shapeRenderer.circle(selected.area.x, selected.area.y, Main.TOWER_RADIUS + 4);
                shapeRenderer.end();
                Gdx.gl20.glLineWidth(1);
            }
            if (placingTower) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1, 0, 0, .2f);
                for (Tower tower : towers)
                    shapeRenderer.circle(tower.area.x, tower.area.y, tower.area.radius);
                shapeRenderer.setColor(0, 1, 0, .4f);
                Vector2 coords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                for (Tower tower : towers)
                    if (tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) shapeRenderer.setColor(1, 0, 0, .4f);
                shapeRenderer.circle(coords.x, coords.y, Main.TOWER_RADIUS);
                shapeRenderer.end();
            }
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO not working properly
        stage.getViewport().update(width, height);
        ui.getViewport().update(width, height, true);
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
        ui.dispose();
    }


    public static class LevelPrototype {
        // TODO when you make the generators make these local again
        public float width;
        public float height;

        Point.PointPrototype[] path;
        Wave.WavePrototype[] waves;

        public LevelPrototype(){

        }
    }
}

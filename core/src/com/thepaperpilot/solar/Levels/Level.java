package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
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
    private Point[] path;

    public Level(LevelPrototype levelPrototype) {
        width = levelPrototype.width;
        height = levelPrototype.height;
        path = new Point[levelPrototype.path.length];
        for (int i = 0; i < path.length; i++) {
            path[i] = new Point(levelPrototype.path[i]);
        }

        stage = new Stage(new StretchViewport(width, height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(1920, 1080));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                selected = null;
                Vector2 coords = new Vector2(x, y);
                if(placingTower) {
                    // TODO placingTower = false;
                    for (Tower tower : towers) {
                        if(tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) { // TODO Make a selected tower thing, and look up its radius
                            return;
                        }
                    }
                    for (int i = 0; i < path.length - 1; i++) {
                        if(Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i+1].x, path[i+1].y, x, y) < Main.TOWER_RADIUS + 8) {
                            return;
                        }
                    }
                    // TODO check for resources
                    towers.add(new Tower(new Circle(coords, Main.TOWER_RADIUS)));
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

        // TODO make the path into a particle effect
        // Make the first segment green, middle segments blue, and the last segment red
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Matrix4 transform = new Matrix4();
        transform.scale(Gdx.graphics.getWidth() / width, Gdx.graphics.getHeight() / height, 1);
        shapeRenderer.setTransformMatrix(transform);

        // omg that is a lot of work for making the paths
        // paths themselves
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, .805f, .816f, .6f);
        for (int i = 0; i < path.length - 1; i++) {
            Vector2 start = new Vector2(path[i].x, path[i].y);
            Vector2 end = new Vector2(path[i + 1].x, path[i + 1].y);
            shapeRenderer.rectLine(start, end, 8);
        }
        shapeRenderer.end();

        // circles at corners
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, .805f, .816f, 1f);
        for (int i = 1; i < path.length - 1; i++) {
            shapeRenderer.circle(path[i].x, path[i].y, 5);
        }

        // arrow showing where enemies enter
        shapeRenderer.setColor(0, 1, 0, .5f);
        float dx = path[1].x - path[0].x;
        float dy = path[1].y - path[0].y;
        float angle = MathUtils.atan2(dy, dx);
        shapeRenderer.triangle(path[0].x + 8 * MathUtils.cos(angle - .5f), path[0].y + 8 * MathUtils.sin(angle - .5f), path[0].x + 16 * MathUtils.cos(angle), path[0].y + 16 * MathUtils.sin(angle), path[0].x + 8 * MathUtils.cos(angle + .5f), path[0].y + 8 * MathUtils.sin(angle + .5f));

        // arrow showing where enemies leave
        shapeRenderer.setColor(1, 0, 0, .5f);
        dx = path[path.length - 2].x - path[path.length - 1].x;
        dy = path[path.length - 2].y - path[path.length - 1].y;
        angle = MathUtils.atan2(dy, dx);
        Point end = path[path.length - 1];
        shapeRenderer.triangle(end.x + 16 * MathUtils.cos(angle - .25f), end.y + 16 * MathUtils.sin(angle - .25f), end.x + 8 * MathUtils.cos(angle), end.y + 8 * MathUtils.sin(angle), end.x + 16 * MathUtils.cos(angle + .25f), end.y + 16 * MathUtils.sin(angle + .25f));
        shapeRenderer.end();

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
            shapeRenderer.setColor(1, 1, 1, .2f);
            for (Tower tower : towers)
                shapeRenderer.circle(tower.area.x, tower.area.y, tower.area.radius);
            shapeRenderer.setColor(0, 1, 0, .4f);
            Vector2 coords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            for (Tower tower : towers)
                if (tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) {
                    shapeRenderer.setColor(1, 0, 0, .4f);
                    break;
                }
            for (int i = 0; i < path.length - 1; i++)
                if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, coords.x, coords.y) < Main.TOWER_RADIUS + 8) {
                    shapeRenderer.setColor(1, 0, 0, .4f);
                    break;
                }
            shapeRenderer.circle(coords.x, coords.y, Main.TOWER_RADIUS);
            shapeRenderer.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
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

        public Point.PointPrototype[] path;
        Wave.WavePrototype[] waves;

        public LevelPrototype(){

        }
    }
}

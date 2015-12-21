package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
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
    private final Batch batch;
    private final ParticleEffect pathParticles;
    private final ArrayList<Tower> towers = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final float width;
    private final float height;
    private final Stage stage;
    private final Stage ui;
    private final boolean placingTower = true;
    private final Point[] path;
    private Tower selected;

    public Level(LevelPrototype levelPrototype) {
        width = levelPrototype.width;
        height = levelPrototype.height;
        path = new Point[levelPrototype.path.length];
        for (int i = 0; i < path.length; i++) {
            path[i] = new Point(levelPrototype.path[i]);
        }

        batch = new SpriteBatch();

        pathParticles = new ParticleEffect();
        pathParticles.load(Gdx.files.internal("particles/path.p"), Gdx.files.internal("particles/"));
        for (int i = 0; i < path.length - 1; i++) {
            if (i != 0) pathParticles.getEmitters().add(new ParticleEmitter(pathParticles.getEmitters().first()));
            ParticleEmitter emitter = pathParticles.getEmitters().get(i);
            emitter.getTint().setColors(new float[]{0, 0, 1f});
            emitter.getAngle().setHigh(new Vector2(path[i + 1].x - path[i].x, path[i + 1].y - path[i].y).angle());
            emitter.setPosition(path[i].x, path[i].y);
            emitter.getLife().setHigh(10 * Vector2.len(path[i + 1].x - path[i].x, path[i + 1].y - path[i].y));
        }
        pathParticles.getEmitters().first().getTint().setColors(new float[]{0, 1f, 0, 0, 0, 1f});
        pathParticles.getEmitters().first().getTint().setTimeline(new float[]{0, 1});
        pathParticles.getEmitters().get(pathParticles.getEmitters().size - 1).getTint().setColors(new float[]{0, 0, 1f, 1f, 0, 0});
        pathParticles.getEmitters().get(pathParticles.getEmitters().size - 1).getTint().setTimeline(new float[]{0, 1});
        for (int i = 0; i < 100; i++) {
            pathParticles.update(.1f);
        }

        stage = new Stage(new StretchViewport(width, height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(1920, 1080));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                selected = null;
                Vector2 coords = new Vector2(x, y);
                if (placingTower) {
                    // TODO placingTower = false;
                    for (Tower tower : towers) {
                        if (tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) { // TODO Make a selected tower thing, and look up its radius
                            return;
                        }
                    }
                    for (int i = 0; i < path.length - 1; i++) {
                        if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, x, y) < Main.TOWER_RADIUS + 8) {
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

        Matrix4 transform = new Matrix4();
        transform.scale(Gdx.graphics.getWidth() / width, Gdx.graphics.getHeight() / height, 1);
        batch.setTransformMatrix(transform);
        batch.begin();
        pathParticles.draw(batch, delta * .2f);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
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
        batch.dispose();
    }


    public static class LevelPrototype {
        // TODO when you make the generators make these local again
        public float width;
        public float height;

        public Point.PointPrototype[] path;
        Wave.WavePrototype[] waves;

        public LevelPrototype() {

        }
    }
}

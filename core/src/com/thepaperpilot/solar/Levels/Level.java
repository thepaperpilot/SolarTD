package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Building;
import com.thepaperpilot.solar.Entities.Enemy;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Interface.HUD;
import com.thepaperpilot.solar.Interface.Menu;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Level implements Screen {
    private final static Json json = new Json();
    public final LevelPrototype prototype;
    public final Stage stage;
    public final Vector2[] path;
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final ArrayList<ParticleEffect> particles = new ArrayList<>();
    public final Stage ui;
    public final Wave[] waves;
    private final ParticleEffect pathParticles;
    private final ArrayList<Building> buildings = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    public Building selectedBuilding;
    public boolean placingBuilding;
    public int redResource = 100;
    public int blueResource = 100;
    public int yellowResource = 100;
    public int population = 1;
    public Wave finalWave;
    public boolean paused;
    public float time = -10;
    public int currWave;
    public Resource selectedResource = Resource.RED;
    public int selectedType = 1; // 1 is tower, 2 is generator
    private float resourceTime = -10;

    public Level(LevelPrototype levelPrototype) {
        prototype = levelPrototype;
        path = new Vector2[levelPrototype.path.length / 2];
        for (int i = 0; i < levelPrototype.path.length - 1; i += 2) {
            path[i / 2] = new Vector2(levelPrototype.path[i], levelPrototype.path[i + 1]);
        }
        waves = new Wave[levelPrototype.waves.length];
        for (int i = 0; i < levelPrototype.waves.length; i++) {
            waves[i] = new Wave(levelPrototype.waves[i]);
        }

        pathParticles = new ParticleEffect();
        pathParticles.load(Gdx.files.internal("particles/path.p"), Gdx.files.internal("particles/"));
        for (int i = 0; i < path.length - 1; i++) {
            if (i != 0) pathParticles.getEmitters().add(new ParticleEmitter(pathParticles.getEmitters().first()));
            ParticleEmitter emitter = pathParticles.getEmitters().get(i);
            emitter.getTint().setColors(new float[]{0, 0, 1f});
            emitter.getAngle().setHigh(path[i+1].cpy().sub(path[i]).angle());
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

        stage = new Stage(new StretchViewport(levelPrototype.width, levelPrototype.height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(Main.UI_WIDTH, Main.UI_WIDTH * 9f / 16f));

        stage.addActor(new Image(Main.getDrawable("bg")));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                float uiHeight = ui.stageToScreenCoordinates(new Vector2(0, ui.getHeight() - ui.getActors().first().getY() - ui.getActors().first().getHeight())).y;
                uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - selectedType * Main.TOWER_RADIUS)).y;
                if (Gdx.input.getY() > Gdx.graphics.getHeight() - uiHeight) return;
                selectedBuilding = null;
                Menu.deselect();
                if (!placingBuilding) return;
                Vector2 coords = new Vector2(x, y);
                for (Building building : buildings) {
                    if (building.area.overlaps(new Circle(coords, selectedType * Main.TOWER_RADIUS))) {
                        return;
                    }
                }
                for (int i = 0; i < path.length - 1; i++) {
                    if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, x, y) < selectedType * Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                        return;
                    }
                }
                boolean paid = false;
                if (selectedType == 1 && Tower.pay(selectedResource, Level.this)) paid = true;
                if (selectedType == 2 && Generator.pay(selectedResource, Level.this)) paid = true;
                if (!paid) {
                    final Label message = new Label("Insufficient Resources", Main.skin);
                    message.setPosition(coords.x, coords.y, Align.center);
                    message.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, 20, 1), Actions.fadeOut(1)), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            message.remove();
                        }
                    })));
                    stage.addActor(message);
                    return;
                }
                if (selectedType == 1) {
                    Tower tower = new Tower(coords.x - Main.TOWER_RADIUS, coords.y - Main.TOWER_RADIUS, selectedResource, Level.this);
                    buildings.add(tower);
                    stage.addActor(tower);
                } else {
                    Generator generator = new Generator(coords.x - 2 * Main.TOWER_RADIUS, coords.y - 2 * Main.TOWER_RADIUS, selectedResource, Level.this);
                    buildings.add(generator);
                    stage.addActor(generator);
                }
                if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                    placingBuilding = false;
                    HUD.deselect();
                }
            }
        });

        HUD.init(this);
        Menu.init(this);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    HUD.pause();
                } else if (keycode == Input.Keys.ESCAPE) {
                    Menu.toggle();
                }
                return true;
            }
        });
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
        time += delta;
        resourceTime += delta;
        while (resourceTime > 4) {
            resourceTime -= 4;
            redResource++;
            blueResource++;
            yellowResource++;
        }
        if (currWave < waves.length) {
            if (time >= waves[currWave].enemyDistance) {
                time -= waves[currWave].enemyDistance;
                // TODO enemy pool
                final Enemy enemy = waves[currWave].getEnemy(this);
                if (waves[currWave].isEmpty()) {
                    currWave++;
                    population += currWave;
                    time -= 10;
                }
                addEnemy(enemy);
            }
        } else {
            if (finalWave == null || finalWave.isEmpty()) {
                Wave.WavePrototype wavePrototype = new Wave.WavePrototype();
                wavePrototype.enemyDistance = 1;
                Enemy.EnemyPrototype enemyPrototype = new Enemy.EnemyPrototype();
                enemyPrototype.speed = 1;
                enemyPrototype.health = (float) Math.pow(currWave, 1.2f);
                enemyPrototype.count = 10;
                enemyPrototype.name = "alien";
                wavePrototype.enemies = new Enemy.EnemyPrototype[]{enemyPrototype};
                finalWave = new Wave(wavePrototype);
            }
            if (time >= finalWave.enemyDistance) {
                time -= finalWave.enemyDistance;
                final Enemy enemy = finalWave.getEnemy(this);
                if (finalWave.isEmpty()) {
                    currWave++;
                    population += currWave;
                    time -= 10;
                }
                addEnemy(enemy);
            }
        }

        if (!paused) stage.act(delta);
        stage.draw();

        Batch batch = stage.getBatch();
        batch.begin();
        pathParticles.draw(batch, paused ? 0 : delta * .2f);
        for (int i = 0; i < particles.size(); ) {
            ParticleEffect effect = particles.get(i);
            effect.draw(batch, paused ? 0 : delta);
            if (effect.isComplete() && !effect.getEmitters().first().isContinuous()) {
                particles.remove(effect);
                if (effect instanceof ParticleEffectPool.PooledEffect)
                    ((ParticleEffectPool.PooledEffect) effect).free();
            } else i++;
        }
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        if (selectedBuilding != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, .5f);
            shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, selectedBuilding.area.radius + 2);
            shapeRenderer.end();
            if (selectedBuilding instanceof Tower) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(0, 1, 0, .5f);
                shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, ((Tower) selectedBuilding).range);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, .25f);
                shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, ((Tower) selectedBuilding).range);
                shapeRenderer.end();
            }
        }

        if (placingBuilding) {
            float range = 0;
            if (selectedType == 1) {
                range = Tower.getBaseRange(selectedResource);
            }
            float uiHeight = ui.stageToScreenCoordinates(new Vector2(0, ui.getHeight() - ui.getActors().first().getY() - ui.getActors().first().getHeight())).y;
            uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - selectedType * Main.TOWER_RADIUS)).y;
            if (Gdx.input.getY() < Gdx.graphics.getHeight() - uiHeight) {
                Vector2 coords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 1, .5f);
                shapeRenderer.circle(coords.x, coords.y, range);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1, 1, 1, .25f);
                shapeRenderer.circle(coords.x, coords.y, range);
                shapeRenderer.setColor(1, 1, 1, .5f);
                for (Building building : buildings)
                    shapeRenderer.circle(building.area.x, building.area.y, building.area.radius);
                shapeRenderer.setColor(0, 1, 0, .5f);
                for (Building building : buildings)
                    if (building.area.overlaps(new Circle(coords, selectedType * Main.TOWER_RADIUS))) {
                        shapeRenderer.setColor(1, 0, 0, .5f);
                        break;
                    }
                for (int i = 0; i < path.length - 1; i++)
                    if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, coords.x, coords.y) < selectedType * Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                        shapeRenderer.setColor(1, 0, 0, .5f);
                        break;
                    }
                shapeRenderer.circle(coords.x, coords.y, selectedType * Main.TOWER_RADIUS);
                shapeRenderer.end();
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        HUD.update();

        ui.act(delta);
        ui.draw();
    }

    private void addEnemy(final Enemy enemy) {
        stage.addActor(enemy);
        enemy.setPosition(path[0].x - Main.ENEMY_SIZE / 2, path[0].y - Main.ENEMY_SIZE / 2);
        enemies.add(enemy);
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
        ui.dispose();
    }

    public enum Resource {
        RED,
        BLUE,
        YELLOW
    }

    public static class LevelPrototype {
        // TODO when you make the generators make these local again
        public float width;
        public float height;

        public float[] path;
        public Wave.WavePrototype[] waves;
    }
}

package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
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
import com.thepaperpilot.solar.Interface.StatsCircle;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Level implements Screen {
    private final static Json json = new Json();
    public static int difficulty = 0;
    public final LevelPrototype prototype;
    public final Stage stage;
    public final Vector2[] path;
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final ArrayList<ParticleEffect> particles = new ArrayList<>();
    public final Stage ui;
    public final Wave.WavePrototype[] waves;
    public final ArrayList<Building> buildings = new ArrayList<>();
    private final Image bg;
    private final ParticleEffect pathParticles;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final ParticleEffect stars;
    public Building selectedBuilding;
    public boolean placingBuilding;
    public boolean movingBuilding;
    public int redResource = 100;
    public int blueResource = 100;
    public int yellowResource = 100;
    public int population = 10;
    public boolean paused;
    public float time;
    public int wave;
    public Wave currWave;
    public Resource selectedResource = Resource.RED;
    public int selectedType = 1; // 1 is tower, 2 is generator
    public int totalKills = 0;
    private float resourceTime = -10;

    public Level(LevelPrototype levelPrototype) {
        prototype = levelPrototype;
        path = new Vector2[levelPrototype.path.length / 2];
        for (int i = 0; i < levelPrototype.path.length - 1; i += 2) {
            path[i / 2] = new Vector2(levelPrototype.path[i], levelPrototype.path[i + 1]);
        }
        waves = levelPrototype.waves;

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

        stars = new ParticleEffect();
        stars.load(Gdx.files.internal("particles/stars.p"), Gdx.files.internal("particles/"));
        for (int i = 0; i < 100; i++) {
            stars.update(.1f);
        }

        stage = new Stage(new StretchViewport(levelPrototype.width, levelPrototype.height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(Main.UI_WIDTH, Main.UI_WIDTH * 9f / 16f));

        bg = new Image(Main.getDrawable("bg"));
        bg.setScale(prototype.width / Gdx.graphics.getWidth());
        bg.setPosition(MathUtils.random(bg.getImageWidth() - prototype.width), MathUtils.random(bg.getImageHeight() - prototype.height));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                float uiHeight = ui.stageToScreenCoordinates(new Vector2(0, ui.getHeight() - ui.getActors().first().getY() - ui.getActors().first().getHeight())).y;
                if (placingBuilding) uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - selectedType * Main.TOWER_RADIUS)).y;
                else uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - (selectedBuilding instanceof Tower ? 1 : 2) * Main.TOWER_RADIUS)).y;
                float towerSize = stage.stageToScreenCoordinates(new Vector2((((movingBuilding ? selectedBuilding instanceof Tower : selectedType == 1) ? 1 : 2) * Main.TOWER_RADIUS), 0)).x;
                if (!(Gdx.input.getY() < Gdx.graphics.getHeight() - uiHeight && Gdx.input.getY() > towerSize && Gdx.input.getX() > towerSize && Gdx.input.getX() < Gdx.graphics.getWidth() - towerSize)) return;
                if (!movingBuilding) {
                    selectedBuilding = null;
                    Menu.deselect();
                }
                if (!placingBuilding && !movingBuilding) return;
                Vector2 coords = new Vector2(x, y);
                for (Building building : buildings) {
                    if (building.area.overlaps(new Circle(coords, selectedType * Main.TOWER_RADIUS))) {
                        if(movingBuilding && selectedBuilding == building) continue;
                        placingBuilding = false;
                        movingBuilding = false;
                        HUD.deselect();
                        return;
                    }
                }
                for (int i = 0; i < path.length - 1; i++) {
                    if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, x, y) < selectedType * Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                        placingBuilding = false;
                        movingBuilding = false;
                        HUD.deselect();
                        return;
                    }
                }
                if (movingBuilding) {
                    selectedBuilding.setPosition(coords.x - (selectedBuilding instanceof Tower ? 1 : 2) * Main.TOWER_RADIUS, coords.y - (selectedBuilding instanceof Tower ? 1 : 2) * Main.TOWER_RADIUS);
                    selectedBuilding.area.setPosition(coords.x, coords.y);
                    movingBuilding = false;
                    if (selectedBuilding instanceof Tower) {
                        Tower.refreshNeighbors(Level.this);
                    }
                    return;
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
                    Tower.refreshNeighbors(Level.this);
                } else {
                    Generator generator = new Generator(coords.x - 2 * Main.TOWER_RADIUS, coords.y - 2 * Main.TOWER_RADIUS, selectedResource, Level.this);
                    buildings.add(generator);
                    stage.addActor(generator);
                }
                if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                    placingBuilding = false;
                    HUD.deselect();
                }
                Menu.select();
            }
        });

        currWave = new Wave(new Wave.WavePrototype(), this);
        time = -Main.WAVE_INTERVAL;
        HUD.init(this);
        Menu.init(this);
        HUD.deselect();
        Menu.deselect();
        Menu.updateWaves();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                        HUD.pause();
                        break;
                    case Input.Keys.ESCAPE:
                        Menu.toggle();
                        break;
                    case Input.Keys.M:
                        if (movingBuilding) movingBuilding = false;
                        else if (selectedBuilding != null) movingBuilding = true;
                        break;
                    case Input.Keys.S:
                        if (selectedBuilding != null) selectedBuilding.sell();
                        break;
                    case Input.Keys.NUM_1:
                    case Input.Keys.NUM_2:
                    case Input.Keys.NUM_3:
                    case Input.Keys.NUM_4:
                    case Input.Keys.NUM_5:
                    case Input.Keys.NUM_6:
                        HUD.pressButton(Integer.parseInt(Input.Keys.toString(keycode)));
                        break;
                }
                return true;
            }
        });
    }

    public static float getHealthRate() {
        return difficulty == -1 ? Main.HEALTH_RATE * .9f : difficulty == 0 ? Main.HEALTH_RATE : Main.HEALTH_RATE * 1.2f;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, stage));
    }

    @Override
    public void render(float delta) {
        if (!paused) {
            time += delta;
            resourceTime += delta;
        }
        while (resourceTime > 2) {
            resourceTime -= 2;
            redResource++;
            blueResource++;
            yellowResource++;
        }
        if (time >= currWave.getTime() + Main.WAVE_INTERVAL) {
            nextWave();
        }

        Menu.update();

        if (!paused) stage.act(delta);

        Batch batch = stage.getBatch();
        batch.begin();
        bg.draw(batch, 1);
        stars.draw(batch, delta);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Building building : buildings) {
            if (building instanceof Generator) {
                Color color = building.type == Level.Resource.RED ? Color.RED : building.type == Level.Resource.BLUE ? Color.BLUE : Color.YELLOW;
                shapeRenderer.setColor(color.r, color.g, color.b, color.a * batch.getColor().a);
            }
            StatsCircle.drawBottom(shapeRenderer, new Vector2(building.area.x, building.area.y), building, 1f, Main.TOWER_RADIUS);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.draw();

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
        if (selectedBuilding != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, .5f);
            shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, selectedBuilding.area.radius + 2);
            shapeRenderer.end();
            if (selectedBuilding instanceof Tower) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(0, 1, 0, .5f);
                shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, ((Tower) selectedBuilding).getRange());
                shapeRenderer.setColor(1, 1, 0, .5f);
                shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, 4 * Main.TOWER_RADIUS);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, .25f);
                shapeRenderer.circle(selectedBuilding.area.x, selectedBuilding.area.y, ((Tower) selectedBuilding).getRange());
                shapeRenderer.end();
            }
        }

        if (placingBuilding || movingBuilding) {
            float range = 0;
            if (movingBuilding) {
                if (selectedBuilding instanceof Tower) range = ((Tower) selectedBuilding).getRange();
            } else if (selectedType == 1) {
                range = Tower.getBaseRange(selectedResource);
            }
            float uiHeight = ui.stageToScreenCoordinates(new Vector2(0, ui.getHeight() - ui.getActors().first().getY() - ui.getActors().first().getHeight())).y;
            if (placingBuilding) uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - selectedType * Main.TOWER_RADIUS)).y;
            else uiHeight += stage.stageToScreenCoordinates(new Vector2(0, stage.getHeight() - (selectedBuilding instanceof Tower ? 1 : 2) * Main.TOWER_RADIUS)).y;
            float towerSize = stage.stageToScreenCoordinates(new Vector2((((movingBuilding ? selectedBuilding instanceof Tower : selectedType == 1) ? 1 : 2) * Main.TOWER_RADIUS), 0)).x;
            if (Gdx.input.getY() < Gdx.graphics.getHeight() - uiHeight && Gdx.input.getY() > towerSize && Gdx.input.getX() > towerSize && Gdx.input.getX() < Gdx.graphics.getWidth() - towerSize) {
                Vector2 coords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 1, .5f);
                shapeRenderer.circle(coords.x, coords.y, range);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1, 1, 1, .25f);
                shapeRenderer.circle(coords.x, coords.y, range);
                shapeRenderer.setColor(1, 1, 1, .5f);
                for (Building building : buildings) {
                    if (!(movingBuilding && selectedBuilding == building))
                        shapeRenderer.circle(building.area.x, building.area.y, building.area.radius);
                    if (building instanceof Tower && coords.dst(building.area.x, building.area.y) <= 4 * Main.TOWER_RADIUS) {
                        if (placingBuilding && selectedType == 1) {
                            shapeRenderer.setColor(1, 1, ((Tower) building).comboUpgrade ? 0 : 1, .75f);
                            shapeRenderer.line(coords, new Vector2(building.area.x, building.area.y));
                            shapeRenderer.setColor(1, 1, 1, .5f);
                        } else if (movingBuilding && selectedBuilding != null && selectedBuilding != building && selectedBuilding instanceof Tower) {
                            shapeRenderer.setColor(1, 1, ((Tower) building).comboUpgrade ? 0 : 1, .75f);
                            shapeRenderer.rectLine(coords, new Vector2(building.area.x, building.area.y), ((Tower) selectedBuilding).comboUpgrade ? 2 : 1);
                            shapeRenderer.setColor(1, 1, 1, .5f);
                        }
                    }
                }
                shapeRenderer.setColor(0, placingBuilding ? 1 : 0, movingBuilding ? 1 : 0, .5f);
                for (Building building : buildings)
                    if (building.area.overlaps(new Circle(coords, selectedType * Main.TOWER_RADIUS))) {
                        if (movingBuilding && selectedBuilding == building) continue;
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

    public void nextWave() {
        resourceTime += (2 * Main.WAVE_INTERVAL + currWave.getTime() - time) * 1.5f;
        for (Building building : buildings) {
            if (building instanceof Generator) ((Generator) building).time += (2 * Main.WAVE_INTERVAL + currWave.getTime() - time) * 1.5f;
        }
        time = 0;
        wave++;
        population += 10;
        Wave newWave = new Wave(waves[(wave - 1) % waves.length], this);
        newWave.setPosition(path[0].x, path[0].y);
        stage.addActor(newWave);
        currWave = newWave;
        Menu.updateWaves();
    }

    public void addEnemy(final Enemy enemy) {
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
        public float width;
        public float height;

        public float[] path;
        public Wave.WavePrototype[] waves;
    }
}

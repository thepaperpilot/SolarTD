package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Building;
import com.thepaperpilot.solar.Entities.Enemy;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Level implements Screen {
    private final static Json json = new Json();
    public final Stage stage;
    public final Vector2[] path;
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final ArrayList<ParticleEffect> particles = new ArrayList<>();
    private final ParticleEffect pathParticles;
    private final ArrayList<Building> buildings = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Stage ui;
    private final Wave[] waves;
    private final Button red;
    private final Button blue;
    private final Button yellow;
    private final Button redGen;
    private final Button blueGen;
    private final Button yellowGen;
    private final Label redRes;
    private final Label blueRes;
    private final Label yellowRes;
    private final Label redCost;
    private final Label blueCost;
    private final Label yellowCost;
    private final Label livesLabel;
    private final Label wavesLabel;
    private final Label timeLabel;
    private final Table cost;
    private final Table resourcesTable;
    public Building selectedBuilding;
    public boolean placingBuilding;
    public int redResource = 100;
    public int blueResource = 100;
    public int yellowResource = 100;
    public int population = 1;
    private Wave finalWave;
    private boolean paused;
    private float time = -10;
    private float resourceTime = -10;
    private int currWave;
    private Resource selectedResource = Resource.RED;
    private int selectedType = 1; // 1 is tower, 2 is generator

    public Level(LevelPrototype levelPrototype) {
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
        ui = new Stage(new StretchViewport(648, 360)); //640x360 is 16:9, try to stay as close as possible

        stage.addActor(new Image(Main.getDrawable("bg")));

        red = new Button(Main.getDrawable("towers/redStore"), Main.getDrawable("towers/redStoreDown"), Main.getDrawable("towers/redStoreDown"));
        blue = new Button(Main.getDrawable("towers/blueStore"), Main.getDrawable("towers/blueStoreDown"), Main.getDrawable("towers/blueStoreDown"));
        yellow = new Button(Main.getDrawable("towers/yellowStore"), Main.getDrawable("towers/yellowStoreDown"), Main.getDrawable("towers/yellowStoreDown"));
        redGen = new Button(Main.getDrawable("towers/redGenStore"), Main.getDrawable("towers/redGenStoreDown"), Main.getDrawable("towers/redGenStoreDown"));
        blueGen = new Button(Main.getDrawable("towers/blueGenStore"), Main.getDrawable("towers/blueGenStoreDown"), Main.getDrawable("towers/blueGenStoreDown"));
        yellowGen = new Button(Main.getDrawable("towers/yellowGenStore"), Main.getDrawable("towers/yellowGenStoreDown"), Main.getDrawable("towers/yellowGenStoreDown"));
        redRes = new Label("" + redResource, Main.skin);
        redRes.setColor(.5f, 0, 0, 1);
        blueRes = new Label("" + blueResource, Main.skin);
        blueRes.setColor(0, 0, .5f, 1);
        yellowRes = new Label("" + yellowResource, Main.skin);
        yellowRes.setColor(.5f, .5f, 0, 1);
        redCost = new Label("0", Main.skin);
        blueCost = new Label("0", Main.skin);
        yellowCost = new Label("0", Main.skin);
        livesLabel = new Label("" + population, Main.skin);
        wavesLabel = new Label("" + currWave, Main.skin);
        timeLabel = new Label("" + Math.abs(time), Main.skin);

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                if (!placingBuilding) return;
                if (stage.stageToScreenCoordinates(new Vector2(x, stage.getHeight() - y)).y < 256 + selectedType * Main.TOWER_RADIUS)
                    return;
                selectedBuilding = null;
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
                    red.setChecked(false);
                    blue.setChecked(false);
                    yellow.setChecked(false);
                    redGen.setChecked(false);
                    blueGen.setChecked(false);
                    yellowGen.setChecked(false);
                    cost.setVisible(false);
                }
            }
        });

        Table table = new Table(Main.skin);
        table.setSize(ui.getWidth(), 64);
        table.setBackground(Main.skin.getDrawable("default-round"));
        table.setPosition(0, 8);

        Table buttonsTable = new Table(Main.skin);
        Button menuToggle = new TextButton("MENU", Main.skin);
        menuToggle.pad(10);
        buttonsTable.add(menuToggle).expandY().fill().spaceBottom(8).row();
        final TextButton pause = new TextButton("PAUSE", Main.skin);
        pause.pad(10);
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
                pause.setText(paused ? "RESUME" : "PAUSE");
            }
        });
        buttonsTable.add(pause).width(new GlyphLayout(Main.skin.getFont("font"), "RESUME").width + 10).expandY().fill();
        table.add(buttonsTable).expandY().fillY().spaceLeft(4);

        red.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (red.isChecked()) {
                    blue.setChecked(false);
                    yellow.setChecked(false);
                    redGen.setChecked(false);
                    blueGen.setChecked(false);
                    yellowGen.setChecked(false);
                }
                placingBuilding = red.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 1;
                selectedResource = Resource.RED;
            }
        });
        blue.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (blue.isChecked()) {
                    red.setChecked(false);
                    yellow.setChecked(false);
                    redGen.setChecked(false);
                    blueGen.setChecked(false);
                    yellowGen.setChecked(false);
                }
                placingBuilding = blue.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 1;
                selectedResource = Resource.BLUE;
            }
        });
        yellow.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (yellow.isChecked()) {
                    red.setChecked(false);
                    blue.setChecked(false);
                    redGen.setChecked(false);
                    blueGen.setChecked(false);
                    yellowGen.setChecked(false);
                }
                placingBuilding = yellow.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 1;
                selectedResource = Resource.YELLOW;
            }
        });

        Table towersTable = new Table(Main.skin);
        towersTable.setBackground(Main.skin.getDrawable("default-round"));
        towersTable.add(new Label("Towers", Main.skin)).colspan(3).row();
        towersTable.add(red).size(32);
        towersTable.add(blue).size(32);
        towersTable.add(yellow).size(32);
        table.add(towersTable).spaceLeft(4).uniformY();

        redGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (redGen.isChecked()) {
                    red.setChecked(false);
                    blue.setChecked(false);
                    yellow.setChecked(false);
                    blueGen.setChecked(false);
                    yellowGen.setChecked(false);
                }
                placingBuilding = redGen.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 2;
                selectedResource = Resource.RED;
            }
        });
        blueGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (blueGen.isChecked()) {
                    red.setChecked(false);
                    blue.setChecked(false);
                    yellow.setChecked(false);
                    redGen.setChecked(false);
                    yellowGen.setChecked(false);
                }
                placingBuilding = blueGen.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 2;
                selectedResource = Resource.BLUE;
            }
        });
        yellowGen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (yellowGen.isChecked()) {
                    red.setChecked(false);
                    blue.setChecked(false);
                    yellow.setChecked(false);
                    redGen.setChecked(false);
                    blueGen.setChecked(false);
                }
                placingBuilding = yellowGen.isChecked();
                cost.setVisible(placingBuilding);
                selectedType = 3;
                selectedResource = Resource.YELLOW;
            }
        });

        Table generatorsTable = new Table(Main.skin);
        generatorsTable.setBackground(Main.skin.getDrawable("default-round"));
        generatorsTable.add(new Label("Generators", Main.skin)).colspan(3).row();
        generatorsTable.add(redGen).size(32);
        generatorsTable.add(blueGen).size(32);
        generatorsTable.add(yellowGen).size(32);
        table.add(generatorsTable).spaceLeft(8).uniformY();

        resourcesTable = new Table(Main.skin);
        resourcesTable.setBackground(Main.skin.getDrawable("default-round"));
        resourcesTable.add(new Label("Resources", Main.skin)).colspan(3).row();
        Table redTable = new Table(Main.skin);
        redTable.add(redRes);
        resourcesTable.add(redTable).size(32);
        Table blueTable = new Table(Main.skin);
        blueTable.add(blueRes);
        resourcesTable.add(blueTable).size(32);
        Table yellowTable = new Table(Main.skin);
        yellowTable.add(yellowRes);
        resourcesTable.add(yellowTable).size(32);
        table.add(resourcesTable).spaceLeft(8).uniformY();

        Table lifeTable = new Table(Main.skin);
        lifeTable.setBackground(Main.skin.getDrawable("default-round"));
        lifeTable.add(new Label("Life", Main.skin)).row();
        lifeTable.add(livesLabel).height(32);
        table.add(lifeTable).spaceLeft(8).uniformY();

        Table waveTable = new Table(Main.skin);
        waveTable.setBackground(Main.skin.getDrawable("default-round"));
        waveTable.add(new Label("Wave", Main.skin)).row();
        waveTable.add(wavesLabel).height(32);
        table.add(waveTable).spaceLeft(8).uniformY();

        Table timerTable = new Table(Main.skin);
        timerTable.setBackground(Main.skin.getDrawable("default-round"));
        timerTable.add(new Label("Next Enemy", Main.skin)).row();
        Table enemyTable = new Table(Main.skin);
        enemyTable.setBackground(Main.getDrawable("alien"));
        enemyTable.add(timeLabel);
        timerTable.add(enemyTable).size(32);
        table.add(timerTable).spaceLeft(8).uniformY();

        cost = new Table(Main.skin);
        cost.setColor(1, 1, 1, .5f);
        cost.setTouchable(Touchable.disabled);
        cost.setBackground(Main.skin.getDrawable("default-round"));
        cost.setVisible(false);
        cost.setPosition(resourcesTable.getX(), 72);
        cost.setSize(resourcesTable.getPrefWidth(), 40);
        cost.add(new Label("Cost", Main.skin)).colspan(3).row();
        cost.add(redCost).expand().uniform();
        cost.add(blueCost).expand().uniform();
        cost.add(yellowCost).expand().uniform();

        ui.addActor(table);
        ui.addActor(cost);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    paused = !paused;
                    pause.setText(paused ? "RESUME" : "PAUSE");
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

        redRes.setText("" + redResource);
        blueRes.setText("" + blueResource);
        yellowRes.setText("" + yellowResource);
        livesLabel.setText("" + population);
        wavesLabel.setText("" + currWave);
        timeLabel.setText("" + Math.round(time <= 0 ? Math.abs(time) : (currWave < waves.length ? waves[currWave].enemyDistance : finalWave.enemyDistance) - time));
        cost.setX(resourcesTable.getX());

        redCost.setText("" + (selectedType == 1 ? Tower.getRedCost(selectedResource) : Generator.getRedCost(selectedResource)));
        redCost.setColor(redResource >= (selectedType == 1 ? Tower.getRedCost(selectedResource) : Generator.getRedCost(selectedResource)) ? Color.GREEN : Color.RED);
        blueCost.setText("" + (selectedType == 1 ? Tower.getBlueCost(selectedResource) : Generator.getBlueCost(selectedResource)));
        blueCost.setColor(blueResource >= (selectedType == 1 ? Tower.getBlueCost(selectedResource) : Generator.getBlueCost(selectedResource)) ? Color.GREEN : Color.RED);
        yellowCost.setText("" + (selectedType == 1 ? Tower.getYellowCost(selectedResource) : Generator.getYellowCost(selectedResource)));
        yellowCost.setColor(yellowResource >= (selectedType == 1 ? Tower.getYellowCost(selectedResource) : Generator.getYellowCost(selectedResource)) ? Color.GREEN : Color.RED);

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

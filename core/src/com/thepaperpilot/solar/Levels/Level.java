package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private final Label livesLabel;
    private final Label timeLabel;
    public Building selected;
    public boolean placingBuilding;
    public int redResource = 25;
    public int blueResource = 25;
    public int yellowResource = 25;
    private Wave finalWave;
    private boolean paused;
    private float time = -10;
    private float resourceTime = -10;
    private int currWave;
    private int population = 1;

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
        ui = new Stage(new StretchViewport(640, 360));

        stage.addActor(new Image(Main.getDrawable("bg")));

        red = new Button(Main.getDrawable("towers/redStore"), Main.getDrawable("towers/redStoreDown"), Main.getDrawable("towers/redStoreDown"));
        blue = new Button(Main.getDrawable("towers/blueStore"), Main.getDrawable("towers/blueStoreDown"), Main.getDrawable("towers/blueStoreDown"));
        yellow = new Button(Main.getDrawable("towers/yellowStore"), Main.getDrawable("towers/yellowStoreDown"), Main.getDrawable("towers/yellowStoreDown"));
        redGen = new Button(Main.getDrawable("towers/redGenStore"), Main.getDrawable("towers/redGenStoreDown"), Main.getDrawable("towers/redGenStoreDown"));
        blueGen = new Button(Main.getDrawable("towers/blueGenStore"), Main.getDrawable("towers/blueGenStoreDown"), Main.getDrawable("towers/blueGenStoreDown"));
        yellowGen = new Button(Main.getDrawable("towers/yellowGenStore"), Main.getDrawable("towers/yellowGenStoreDown"), Main.getDrawable("towers/yellowGenStoreDown"));
        redRes = new Label("" + redResource, Main.skin);
        blueRes = new Label("" + blueResource, Main.skin);
        yellowRes = new Label("" + yellowResource, Main.skin);
        livesLabel = new Label("" + population, Main.skin);
        timeLabel = new Label("" + Math.abs(time), Main.skin);

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                Tower.Type type = null;
                Generator.Type type1 = null;
                if (red.isChecked()) type = Tower.Type.RED;
                else if (blue.isChecked()) type = Tower.Type.BLUE;
                else if (yellow.isChecked()) type = Tower.Type.YELLOW;
                else if (redGen.isChecked()) type1 = Generator.Type.RED;
                else if (blueGen.isChecked()) type1 = Generator.Type.BLUE;
                else if (yellowGen.isChecked()) type1 = Generator.Type.YELLOW;
                if (type == null && type1 == null) return;
                if (stage.stageToScreenCoordinates(new Vector2(x, stage.getHeight() - y)).y < 256 + (type == null ? 2 : 1) * Main.TOWER_RADIUS)
                    return;
                selected = null;
                Vector2 coords = new Vector2(x, y);
                if (placingBuilding) {
                    for (Building building : buildings) {
                        if (building.area.overlaps(new Circle(coords, (type == null ? 2 : 1) * Main.TOWER_RADIUS))) {
                            return;
                        }
                    }
                    for (int i = 0; i < path.length - 1; i++) {
                        if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, x, y) < (type == null ? 2 : 1) * Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                            return;
                        }
                    }
                    boolean paid = false;
                    if (type != null) {
                        switch (type) {
                            default:
                            case RED:
                                if (redResource >= 25) {
                                    redResource -= 25;
                                    paid = true;
                                }
                                break;
                            case BLUE:
                                if (blueResource >= 25) {
                                    blueResource -= 25;
                                    paid = true;
                                }
                                break;
                            case YELLOW:
                                if (yellowResource >= 25) {
                                    yellowResource -= 25;
                                    paid = true;
                                }
                                break;
                        }
                    } else {
                        switch (type1) {
                            default:
                            case RED:
                                if (redResource >= 100) {
                                    redResource -= 100;
                                    paid = true;
                                }
                                break;
                            case BLUE:
                                if (blueResource >= 100) {
                                    blueResource -= 100;
                                    paid = true;
                                }
                                break;
                            case YELLOW:
                                if (yellowResource >= 100) {
                                    yellowResource -= 100;
                                    paid = true;
                                }
                                break;
                        }
                    }
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
                    if (type != null) {
                        Tower tower = new Tower(coords.x - Main.TOWER_RADIUS, coords.y - Main.TOWER_RADIUS, type, Level.this);
                        buildings.add(tower);
                        stage.addActor(tower);
                    } else {
                        Generator generator = new Generator(coords.x - 2 * Main.TOWER_RADIUS, coords.y - 2 * Main.TOWER_RADIUS, type1, Level.this);
                        buildings.add(generator);
                        stage.addActor(generator);
                    }
                    if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                        placingBuilding = false;
                        red.setChecked(false);
                        blue.setChecked(false);
                        yellow.setChecked(false);
                    }
                }
            }
        });

        Table table = new Table(Main.skin);
        table.setSize(ui.getWidth(), 64);
        table.setBackground(Main.skin.getDrawable("default-round"));
        table.setPosition(0, 8);

        Table table1 = new Table(Main.skin);
        Button menuToggle = new TextButton("MENU", Main.skin);
        menuToggle.pad(10);
        table1.add(menuToggle).expandY().fill().spaceBottom(8).row();
        final TextButton pause = new TextButton("PAUSE", Main.skin);
        pause.pad(10);
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
                pause.setText(paused ? "RESUME" : "PAUSE");
            }
        });
        table1.add(pause).width(new GlyphLayout(Main.skin.getFont("font"), "RESUME").width + 10).expandY().fill();
        table.left().add(table1).expandY().fillY().spaceLeft(4);

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
            }
        });

        Table table2 = new Table(Main.skin);
        table2.setBackground(Main.skin.getDrawable("default-round"));
        table2.add(new Label("Towers", Main.skin)).colspan(3).row();
        table2.add(red).size(32);
        table2.add(blue).size(32);
        table2.add(yellow).size(32);
        table.add(table2).spaceLeft(4).uniformY();

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
            }
        });

        Table table3 = new Table(Main.skin);
        table3.setBackground(Main.skin.getDrawable("default-round"));
        table3.add(new Label("Generators", Main.skin)).colspan(3).row();
        table3.add(redGen).size(32);
        table3.add(blueGen).size(32);
        table3.add(yellowGen).size(32);
        table.add(table3).spaceLeft(8).uniformY();

        Table table4 = new Table(Main.skin);
        table4.setBackground(Main.skin.getDrawable("default-round"));
        table4.add(new Label("Resources", Main.skin)).colspan(3).row();
        Table redTable = new Table(Main.skin);
        redTable.setBackground(Main.getDrawable("towers/redGen"));
        redTable.add(redRes);
        table4.add(redTable).size(32);
        Table blueTable = new Table(Main.skin);
        blueTable.setBackground(Main.getDrawable("towers/blueGen"));
        blueTable.add(blueRes);
        table4.add(blueTable).size(32);
        Table yellowTable = new Table(Main.skin);
        yellowTable.setBackground(Main.getDrawable("towers/yellowGen"));
        yellowTable.add(yellowRes);
        table4.add(yellowTable).size(32);
        table.add(table4).spaceLeft(8).uniformY();

        Table table5 = new Table(Main.skin);
        table5.setBackground(Main.skin.getDrawable("default-round"));
        table5.add(new Label("Population", Main.skin)).row();
        table5.add(livesLabel).height(32);
        table.add(table5).spaceLeft(8).uniformY();

        Table table6 = new Table(Main.skin);
        table6.setBackground(Main.skin.getDrawable("default-round"));
        table6.add(new Label("Next Enemy", Main.skin)).row();
        Table enemyTable = new Table(Main.skin);
        enemyTable.setBackground(Main.getDrawable("alien"));
        enemyTable.add(timeLabel);
        table6.add(enemyTable).size(32);
        table.add(table6).spaceLeft(8).uniformY();

        ui.addActor(table);

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
                enemyPrototype.health = (int) Math.pow(currWave, 2);
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

        if (selected != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, .5f);
            shapeRenderer.circle(selected.area.x, selected.area.y, selected.area.radius + 2);
            shapeRenderer.end();
            if (selected instanceof Tower) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(0, 1, 0, .5f);
                shapeRenderer.circle(selected.area.x, selected.area.y, ((Tower) selected).range);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, .25f);
                shapeRenderer.circle(selected.area.x, selected.area.y, ((Tower) selected).range);
                shapeRenderer.end();
            }
        }

        if (placingBuilding) {

            float range = 0;
            boolean generator = false;
            if (red.isChecked()) range = 100; // TODO sync this with the value in Tower
            else if (blue.isChecked()) range = 150;
            else if (yellow.isChecked()) range = 50;
            else generator = true;
            if (Gdx.input.getY() < Gdx.graphics.getHeight() - 256 - (generator ? 2 : 1) * Main.TOWER_RADIUS) {
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
                    if (building.area.overlaps(new Circle(coords, (generator ? 2 : 1) * Main.TOWER_RADIUS))) {
                        shapeRenderer.setColor(1, 0, 0, .5f);
                        break;
                    }
                for (int i = 0; i < path.length - 1; i++)
                    if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, coords.x, coords.y) < (generator ? 2 : 1) * Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                        shapeRenderer.setColor(1, 0, 0, .5f);
                        break;
                    }
                shapeRenderer.circle(coords.x, coords.y, (generator ? 2 : 1) * Main.TOWER_RADIUS);
                shapeRenderer.end();
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        redRes.setText("" + redResource);
        blueRes.setText("" + blueResource);
        yellowRes.setText("" + yellowResource);
        livesLabel.setText("" + population);
        timeLabel.setText("" + Math.round(time <= 0 ? Math.abs(time) : (currWave < waves.length ? waves[currWave].enemyDistance : finalWave.enemyDistance) - time));

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


    public static class LevelPrototype {
        // TODO when you make the generators make these local again
        public float width;
        public float height;

        public float[] path;
        public Wave.WavePrototype[] waves;
    }
}

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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thepaperpilot.solar.Entities.Enemy;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Level implements Screen {
    private final static Json json = new Json();
    public final Stage stage;
    private final Batch batch;
    private final ParticleEffect pathParticles;
    private final ArrayList<Tower> towers = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final float width;
    private final float height;
    private final Stage ui;
    private final Vector2[] path;
    private final Wave[] waves;
    public Tower selected;
    public boolean placingTower;
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<ParticleEffect> particles = new ArrayList<>();
    private Wave finalWave;
    private boolean paused;
    private float time = -10;
    private int currWave;

    public Level(LevelPrototype levelPrototype) {
        width = levelPrototype.width;
        height = levelPrototype.height;
        path = new Vector2[levelPrototype.path.length / 2];
        for (int i = 0; i < levelPrototype.path.length - 1; i += 2) {
            path[i / 2] = new Vector2(levelPrototype.path[i], levelPrototype.path[i + 1]);
        }
        waves = new Wave[levelPrototype.waves.length];
        for (int i = 0; i < levelPrototype.waves.length; i++) {
            waves[i] = new Wave(levelPrototype.waves[i]);
        }

        batch = new SpriteBatch();

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

        stage = new Stage(new StretchViewport(width, height));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new Stage(new StretchViewport(1280, 720));

        stage.addActor(new Image(Main.getDrawable("bg")));

        final Button red = new Button(Main.getDrawable("towers/redStore"), Main.getDrawable("towers/redStoreDown"), Main.getDrawable("towers/redStoreDown"));
        final Button blue = new Button(Main.getDrawable("towers/blueStore"), Main.getDrawable("towers/blueStoreDown"), Main.getDrawable("towers/blueStoreDown"));
        final Button yellow = new Button(Main.getDrawable("towers/yellowStore"), Main.getDrawable("towers/yellowStoreDown"), Main.getDrawable("towers/yellowStoreDown"));

        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                if(stage.stageToScreenCoordinates(new Vector2(x, stage.getHeight() - y)).y < 132 + Main.TOWER_RADIUS) return;
                Vector2 coords = new Vector2(x, y);
                if (placingTower) {
                    for (Tower tower : towers) {
                        if (tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) {
                            return;
                        }
                    }
                    for (int i = 0; i < path.length - 1; i++) {
                        if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, x, y) < Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                            return;
                        }
                    }
                    // TODO check for resources
                    Tower.Type type = null;
                    if (red.isChecked()) type = Tower.Type.RED;
                    else if (blue.isChecked()) type = Tower.Type.BLUE;
                    else if (yellow.isChecked()) type = Tower.Type.YELLOW;
                    if (type != null) {
                        Tower tower = new Tower(coords.x - Main.TOWER_RADIUS, coords.y - Main.TOWER_RADIUS, type, Level.this);
                        towers.add(tower);
                        stage.addActor(tower);
                    }
                    if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                        placingTower = false;
                        red.setChecked(false);
                        blue.setChecked(false);
                        yellow.setChecked(false);
                    }
                }
            }
        });

        Table table = new Table(Main.skin);
        table.setSize(ui.getWidth(), 128);
        table.setBackground(Main.skin.getDrawable("default-round"));
        table.pad(8);

        Table table1 = new Table(Main.skin);
        Button menuToggle = new TextButton("MENU", Main.skin, "large");
        menuToggle.pad(10);
        table1.add(menuToggle).expandY().fill().spaceBottom(4).row();
        final TextButton pause = new TextButton("PAUSE", Main.skin, "large");
        pause.pad(10);
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
                pause.setText(paused ? "RESUME" : "PAUSE");
            }
        });
        table1.add(pause).width(new GlyphLayout(Main.skin.getFont("large"), "RESUME").width + 10).expandY().fill();
        table.left().add(table1).fillY().expandY();

        red.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (red.isChecked()) {
                    blue.setChecked(false);
                    yellow.setChecked(false);
                }
                placingTower = red.isChecked();
            }
        });
        blue.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (blue.isChecked()) {
                    red.setChecked(false);
                    yellow.setChecked(false);
                }
                placingTower = blue.isChecked();
            }
        });
        yellow.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (yellow.isChecked()) {
                    red.setChecked(false);
                    blue.setChecked(false);
                }
                placingTower = yellow.isChecked();
            }
        });

        Table table2 = new Table(Main.skin);
        table2.setBackground(Main.skin.getDrawable("default-round"));
        table2.add(new Label("Towers", Main.skin, "large")).colspan(3).spaceBottom(12).row();
        table2.add(red).size(64);
        table2.add(blue).size(64);
        table2.add(yellow).size(64);
        table.add(table2).spaceLeft(16).expandY().fill();

        ui.addActor(table);

        // TODO resources

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
        if (currWave < waves.length) {
            if (time >= waves[currWave].enemyDistance) {
                time -= waves[currWave].enemyDistance;
                // TODO enemy pool
                final Enemy enemy = waves[currWave].getEnemy();
                if (waves[currWave].isEmpty()) {
                    currWave++;
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
                final Enemy enemy = finalWave.getEnemy();
                if (finalWave.isEmpty()) {
                    currWave++;
                    time -= 10;
                }
                addEnemy(enemy);
            }
        }

        if (!paused) stage.act(delta);
        stage.draw();
        ui.act(delta);
        ui.draw();

        Matrix4 transform = new Matrix4();
        transform.scale(Gdx.graphics.getWidth() / width, Gdx.graphics.getHeight() / height, 1);
        batch.setTransformMatrix(transform);
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
        shapeRenderer.setTransformMatrix(transform);

        if (selected != null) {
            Gdx.gl20.glLineWidth(4);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, .5f);
            shapeRenderer.circle(selected.area.x, selected.area.y, Main.TOWER_RADIUS + 4);
            shapeRenderer.end();
            Gdx.gl20.glLineWidth(1);
        }

        if (placingTower && Gdx.input.getY() < Gdx.graphics.getHeight() - 132 - Main.TOWER_RADIUS) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, .5f);
            for (Tower tower : towers)
                shapeRenderer.circle(tower.area.x, tower.area.y, tower.area.radius);
            shapeRenderer.setColor(0, 1, 0, .5f);
            Vector2 coords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            for (Tower tower : towers)
                if (tower.area.overlaps(new Circle(coords, Main.TOWER_RADIUS))) {
                    shapeRenderer.setColor(1, 0, 0, .5f);
                    break;
                }
            for (int i = 0; i < path.length - 1; i++)
                if (Intersector.distanceSegmentPoint(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, coords.x, coords.y) < Main.TOWER_RADIUS + Main.ENEMY_SIZE) {
                    shapeRenderer.setColor(1, 0, 0, .5f);
                    break;
                }
            shapeRenderer.circle(coords.x, coords.y, Main.TOWER_RADIUS);
            shapeRenderer.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void addEnemy(final Enemy enemy) {
        stage.addActor(enemy);
        enemy.setPosition(path[0].x, path[0].y);
        Action[] actions = new Action[path.length];
        for (int i = 0; i < path.length - 1; i++) {
            actions[i] = Actions.moveTo(path[i + 1].x, path[i + 1].y, new Vector2(path[i + 1].x - path[i].x, path[i + 1].y - path[i].y).len() / Main.ENEMY_SPEED);
        }
        actions[path.length - 1] = Actions.run(new Runnable() {
            @Override
            public void run() {
                if (enemies.contains(enemy)) {
                    enemies.remove(enemy);
                    enemy.remove();
                    // TODO take away life
                }
            }
        });
        enemy.addAction(Actions.sequence(actions));
        enemies.add(enemy);
    }

    @Override
    public void resize(int width, int height) {
        // TODO making non-actors render in wrong spot
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
        batch.dispose();
    }


    public static class LevelPrototype {
        // TODO when you make the generators make these local again
        public float width;
        public float height;

        public float[] path;
        public Wave.WavePrototype[] waves;
    }
}

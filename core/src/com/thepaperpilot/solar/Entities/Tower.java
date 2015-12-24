package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Interface.Menu;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Tower extends Building {
    private static final ParticleEffectPool redPool;
    private static final ParticleEffectPool red2Pool;
    private static final ParticleEffectPool yellowPool;
    private static final ParticleEffectPool bluePool;

    static {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/red.p"), Gdx.files.internal("particles/"));
        redPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/red2.p"), Gdx.files.internal("particles/"));
        red2Pool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/yellow.p"), Gdx.files.internal("particles/"));
        yellowPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        bluePool = new ParticleEffectPool(particleEffect, 0, 100);
    }

    private final Level.Resource type;
    public float range;
    boolean comboUpgrade;
    private boolean ability;
    private float damage;
    private float speed;
    private float time;
    private Level level;
    private ParticleEffect effect;

    public Tower(float x, float y, Level.Resource type, final Level level) {
        super(x, y, Main.TOWER_RADIUS);
        this.type = type;
        this.level = level;
        damage = getBaseDamage(type);
        range = getBaseRange(type);
        speed = getBaseSpeed(type);
        setDrawable(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "red" : type == Level.Resource.BLUE ? "blue" : "yellow")));
        if (type == Level.Resource.YELLOW) effect = yellowPool.obtain();

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!level.placingBuilding) {
                    level.selectedBuilding = (level.selectedBuilding == Tower.this ? null : Tower.this);
                    Menu.select();
                }
                event.reset();
            }
        });
    }

    public static int getRedCost(Level.Resource type) {
        return type == Level.Resource.RED ? 25 : 0;
    }

    public static int getBlueCost(Level.Resource type) {
        return type == Level.Resource.BLUE ? 25 : 0;
    }

    public static int getYellowCost(Level.Resource type) {
        return type == Level.Resource.YELLOW ? 25 : 0;
    }

    public static boolean pay(Level.Resource type, Level level) {
        if (level.redResource >= getRedCost(type) && level.blueResource >= getBlueCost(type) && level.yellowResource >= getYellowCost(type)) {
            level.redResource -= getRedCost(type);
            level.blueResource -= getBlueCost(type);
            level.yellowResource -= getYellowCost(type);
            return true;
        }
        return false;
    }

    public static float getBaseDamage(Level.Resource resource) {
        return resource == Level.Resource.RED ? 3 : resource == Level.Resource.BLUE ? 6 : 1;
    }

    public static float getBaseRange(Level.Resource resource) {
        return resource == Level.Resource.RED ? 100 : resource == Level.Resource.BLUE ? 150 : 50;
    }

    public static float getBaseSpeed(Level.Resource resource) {
        return resource == Level.Resource.RED ? 2 : resource == Level.Resource.BLUE ? 1 : 6;
    }

    public void act(float delta) {
        time += delta * speed;
        Enemy target = null;
        float dist = 0;
        for (Enemy enemy : level.enemies) {
            float length = enemy.getPosition().cpy().sub(getX(), getY()).len();
            if (length <= range) {
                if (target == null || length < dist) { // TODO different ways of preferring enemies. ATM it's closest to tower
                    target = enemy;
                    dist = length;
                }
            }
        }
        if (target == null) {
            time = Math.min(time, Main.TOWER_SPEED);
            if (type == Level.Resource.YELLOW) {
                effect.allowCompletion();
            }
        } else switch (type) {
            default:
            case RED:
                while (time >= Main.TOWER_SPEED) {
                    time -= Main.TOWER_SPEED;
                    ParticleEffect effect = redPool.obtain();
                    effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                    level.particles.add(effect);
                    effect = ability ? red2Pool.obtain() : redPool.obtain();
                    effect.setPosition(target.getX() + Main.ENEMY_SIZE / 2, target.getY() + Main.ENEMY_SIZE / 2);
                    level.particles.add(effect);
                    target.hit(damage);
                }
                break;
            case BLUE:
                while (time >= Main.TOWER_SPEED) {
                    time -= Main.TOWER_SPEED;
                    ParticleEffect effect = bluePool.obtain();
                    effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                    level.particles.add(effect);
                    final Enemy finalTarget = target;
                    level.stage.addActor(new ParticleEffectActor(effect, getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS) {
                        float angle = new Vector2(finalTarget.getX() - getX(), finalTarget.getY() - getY()).angle();

                        public void act(float delta) {
                            Enemy target = finalTarget;
                            float dist = -1;
                            for (Enemy enemy : level.enemies) {
                                float length = enemy.getPosition().cpy().sub(getX(), getY()).len();
                                if (length <= range) {
                                    if (dist == -1 || length < dist) {
                                        target = enemy;
                                        dist = length;
                                    }
                                }
                            }
                            if (dist != -1 && dist < Main.BULLET_SPEED * delta) {
                                target.hit(damage);
                                remove();
                                effect.allowCompletion();
                                return;
                            }
                            if (level.enemies.contains(target)) {
                                float newAngle = new Vector2(target.getX() - getX(), target.getY() - getY()).angle();
                                while (newAngle > angle + 180) newAngle -= 360;
                                while (newAngle < angle - 180) newAngle += 360;
                                if (Math.abs(newAngle - angle) < Main.TURN_RADIUS)
                                    angle = newAngle;
                                else if (newAngle > angle)
                                    angle += Main.TURN_RADIUS;
                                else angle -= Main.TURN_RADIUS;
                            }
                            setPosition(getX() + Main.BULLET_SPEED * MathUtils.cosDeg(angle) * delta, getY() + Main.BULLET_SPEED * MathUtils.sinDeg(angle) * delta);
                            super.act(delta);
                        }
                    });
                }
                break;
            case YELLOW:
                if (!level.particles.contains(effect)) level.particles.add(effect);
                effect.start();
                effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                float angle = new Vector2(target.getX() - getX(), target.getY() - getY()).angle();
                effect.getEmitters().first().getAngle().setHigh(angle - 45, angle + 45);
                effect.getEmitters().first().getAngle().setLow(angle);
                effect.getEmitters().first().getLife().setHigh(range * 10);
                effect.getEmitters().first().getEmission().setHigh(range);
                while (time >= Main.TOWER_SPEED) {
                    time -= Main.TOWER_SPEED;
                    Polygon area = new Polygon(new float[]{
                            getX() + Main.TOWER_RADIUS,
                            getY() + Main.TOWER_RADIUS,

                            getX() + Main.TOWER_RADIUS + range * MathUtils.cosDeg(angle - 45),
                            getY() + Main.TOWER_RADIUS + range * MathUtils.sinDeg(angle - 45),

                            getX() + Main.TOWER_RADIUS + range * MathUtils.cosDeg(angle + 45),
                            getY() + Main.TOWER_RADIUS + range * MathUtils.sinDeg(angle + 45)});

                    for (int i = 0; i < level.enemies.size(); ) {
                        Enemy enemy = level.enemies.get(i);
                        if (area.contains(enemy.getPosition())) {
                            enemy.slowed = damage;
                        }
                        i++;
                    }
                }
                break;
        }
    }
}

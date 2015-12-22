package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Tower extends Image {
    static ParticleEffectPool redPool;
    static ParticleEffectPool red2Pool;
    static ParticleEffectPool yellowPool;
    static ParticleEffectPool bluePool;

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

    public final Circle area;
    private final Type type;
    boolean comboUpgrade;
    boolean ability;
    float damage;
    float speed;
    float range;
    private float time;
    private Level level;
    private ParticleEffect effect;

    public Tower(float x, float y, Type type, final Level level) {
        area = new Circle(x + Main.TOWER_RADIUS, y + Main.TOWER_RADIUS, Main.TOWER_RADIUS);
        setPosition(x, y);
        this.type = type;
        this.level = level;
        switch (type) {
            default:
            case RED:
                setDrawable(Main.getDrawable("towers/red"));
                damage = 3;
                speed = 2;
                range = 200;
                break;
            case BLUE:
                setDrawable(Main.getDrawable("towers/blue"));
                damage = 6;
                speed = 1;
                range = 300;
                break;
            case YELLOW:
                setDrawable(Main.getDrawable("towers/yellow"));
                damage = .2f;
                speed = 6;
                range = 100;
                effect = yellowPool.obtain();
                break;
        }
        setSize(Main.TOWER_RADIUS * 2, Main.TOWER_RADIUS * 2);

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!level.placingTower) level.selected = (level.selected == Tower.this ? null : Tower.this);
            }
        });
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
            if (type == Type.YELLOW) {
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
                                target.slowed = damage;
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
                            if (enemy.hit(damage)) continue;
                        }
                        i++;
                    }
                }
                break;
        }
    }

    public enum Type {
        RED,
        BLUE,
        YELLOW
    }
}

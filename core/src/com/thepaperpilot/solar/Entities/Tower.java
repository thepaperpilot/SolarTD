package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tower extends Building {
    private static final ParticleEffectPool redPool;
    private static final ParticleEffectPool red2Pool;
    private static final ParticleEffectPool yellowPool;
    private static final ParticleEffectPool bluePool;

    private static final float[] damages = new float[]{1, 2, 3, 5, 7, 10, 15, 20, 30, 50, 75, 100};
    private static final int[] damageCosts = new int[]{25, 50, 100, 175, 275, 400, 550, 725, 925, 1150, 1400};
    private static final float[] ranges = new float[]{1, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f, 2.1f};
    private static final int[] rangeCosts = new int[]{25, 50, 100, 175, 275, 400, 550, 725, 925, 1150, 1400};
    private static final float[] speeds = new float[]{1, 1.1f, 1.3f, 1.6f, 2f, 2.5f, 3.1f, 3.8f, 4.6f, 5.4f, 6.4f, 7.5f};
    private static final int[] speedCosts = new int[]{25, 50, 100, 175, 275, 400, 550, 725, 925, 1150, 1400};

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

    public int kills;
    public int shots;
    public Targeting targeting;
    boolean comboUpgrade;
    private float time;
    private boolean ability;
    private int range;
    private int damage;
    private int speed;
    private ParticleEffect effect;

    public Tower(float x, float y, Level.Resource type, final Level level) {
        super(x, y, Main.TOWER_RADIUS, level, type);
        damage = 0;
        range = 0;
        speed = 0;
        setDrawable(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "red" : type == Level.Resource.BLUE ? "blue" : "yellow")));
        if (type == Level.Resource.YELLOW) effect = yellowPool.obtain();
        targeting = type == Level.Resource.RED ? Targeting.FIRST : type == Level.Resource.BLUE ? Targeting.STRONGEST : Targeting.NEAREST;
    }

    public static float getRedCost(Level.Resource type) {
        return type == Level.Resource.RED ? 25 : 0;
    }

    public static float getBlueCost(Level.Resource type) {
        return type == Level.Resource.BLUE ? 25 : 0;
    }

    public static float getYellowCost(Level.Resource type) {
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

    private static float getBaseDamage(Level.Resource resource) {
        return resource == Level.Resource.RED ? 3 : resource == Level.Resource.BLUE ? 6 : 1;
    }

    public static float getBaseRange(Level.Resource resource) {
        return resource == Level.Resource.RED ? 100 : resource == Level.Resource.BLUE ? 150 : 50;
    }

    private static float getBaseSpeed(Level.Resource resource) {
        return resource == Level.Resource.RED ? 2 : resource == Level.Resource.BLUE ? 1 : 6;
    }

    public static String getDescription(Level.Resource type) {
        return type == Level.Resource.RED ? "A medium range fast shooting tower" : type == Level.Resource.BLUE ? "A long range high damage tower" : "A short range AoE tower that slows enemies";
    }

    public void act(float delta) {
        time += delta * getSpeed();
        Enemy target = targeting.target(this);
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
                    if (target.hit(getDamage())) {
                        kills++;
                        level.totalKills++;
                    }
                    shots++;
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
                            if (getX() < 0 || getX() > level.prototype.width || getY() < 0 || getY() > level.prototype.height) {
                                remove();
                                effect.allowCompletion();
                                return;
                            }
                            Enemy target;
                            float length = finalTarget.getPosition().cpy().sub(getX(), getY()).len();
                            if (level.enemies.contains(finalTarget) && length <= getRange()) {
                                target = finalTarget;
                            } else target = targeting.target(Tower.this);
                            if (target != null) {
                                float dist = target.getPosition().cpy().sub(getX(), getY()).len();
                                if (dist < Main.BULLET_SPEED * delta) {
                                    if (target.hit(getDamage())) {
                                        kills++;
                                        level.totalKills++;
                                    }
                                    remove();
                                    effect.allowCompletion();
                                    return;
                                }
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
                    shots++;
                }
                break;
            case YELLOW:
                if (!level.particles.contains(effect)) level.particles.add(effect);
                effect.start();
                effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                float angle = new Vector2(target.getX() - getX(), target.getY() - getY()).angle();
                effect.getEmitters().first().getAngle().setHigh(angle - 45, angle + 45);
                effect.getEmitters().first().getAngle().setLow(angle);
                effect.getEmitters().first().getLife().setHigh(getRange() * 10);
                effect.getEmitters().first().getEmission().setHigh(getRange());
                while (time >= Main.TOWER_SPEED) {
                    time -= Main.TOWER_SPEED;
                    Polygon area = new Polygon(new float[]{
                            getX() + Main.TOWER_RADIUS,
                            getY() + Main.TOWER_RADIUS,

                            getX() + Main.TOWER_RADIUS + getRange() * MathUtils.cosDeg(angle - 45),
                            getY() + Main.TOWER_RADIUS + getRange() * MathUtils.sinDeg(angle - 45),

                            getX() + Main.TOWER_RADIUS + getRange() * MathUtils.cosDeg(angle + 45),
                            getY() + Main.TOWER_RADIUS + getRange() * MathUtils.sinDeg(angle + 45)});

                    for (int i = 0; i < level.enemies.size(); ) {
                        Enemy enemy = level.enemies.get(i);
                        if (area.contains(enemy.getPosition())) {
                            enemy.slowed = getDamage();
                        }
                        i++;
                    }
                    shots++;
                }
                break;
        }
    }

    public void sell() {
        if (type == Level.Resource.YELLOW)
            level.particles.remove(effect);
        super.sell();
    }

    public String getName() {
        return type.name() + " TOWER";
    }

    public void upgradeDamage() {
        if (damage < 11) {
            if (level.redResource >= damageCosts[damage]) {
                redValue += Main.SELL_RATE * damageCosts[damage];
                level.redResource -= damageCosts[damage];
                damage++;
            }
        }
    }

    public void upgradeRange() {
        if (range < 11) {
            if (level.blueResource >= rangeCosts[range]) {
                blueValue += Main.SELL_RATE * rangeCosts[range];
                level.blueResource -= rangeCosts[range];
                range++;
            }
        }
    }

    public void upgradeSpeed() {
        if (speed < 11) {
            if (level.yellowResource >= speedCosts[speed]) {
                yellowValue += Main.SELL_RATE * speedCosts[speed];
                level.yellowResource -= speedCosts[speed];
                speed++;
            }
        }
    }

    public int getDamageIndex() {
        return damage;
    }

    public int getRangeIndex() {
        return range;
    }

    public int getSpeedIndex() {
        return speed;
    }

    public float getDamage() {
        return getBaseDamage(type) * damages[damage];
    }

    public float getRange() {
        return getBaseRange(type) * ranges[range];
    }

    public float getSpeed() {
        return getBaseSpeed(type) * speeds[speed];
    }

    public int getDamageCost() {
        return damage < 11 ? damageCosts[damage] : -1;
    }

    public int getRangeCost() {
        return range < 11 ? rangeCosts[range] : -1;
    }

    public int getSpeedCost() {
        return speed < 11 ? speedCosts[speed] : -1;
    }

    public enum Targeting implements Comparator<Enemy>{
        NEAREST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                float length = enemy.getPosition().cpy().sub(tower.getX(), tower.getY()).len();
                float olength = oEnemy.getPosition().cpy().sub(tower.getX(), tower.getY()).len();
                return (int) (length - olength);
            }
        },
        FIRST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return (int) (enemy.getDistance() - oEnemy.getDistance());
            }
        },
        LAST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return (int) (oEnemy.getDistance() - enemy.getDistance());
            }
        },
        STRONGEST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return (int) (oEnemy.health - enemy.health);
            }
        },
        WEAKEST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return (int) (enemy.health - oEnemy.health);
            }
        };

        public Tower tower;

        public Enemy target(Tower tower) {
            this.tower = tower;
            ArrayList<Enemy> potential = new ArrayList<>();
            for (Enemy enemy : tower.level.enemies) {
                float length = enemy.getPosition().cpy().sub(tower.getX(), tower.getY()).len();
                if (length <= tower.getRange()) {
                    potential.add(enemy);
                }
            }
            if (potential.isEmpty()) return null;
            Collections.sort(potential, this);
            return potential.get(0);
        }
    }
}

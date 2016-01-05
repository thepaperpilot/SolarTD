package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.solar.Combo;
import com.thepaperpilot.solar.Interface.Menu;
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

    public final ArrayList<Tower> neighbors = new ArrayList<>();
    public int kills;
    public int shots;
    public Targeting targeting;
    public boolean comboUpgrade;
    public float comboTimer;
    public float comboParticleTimer;
    public ParticleEffect comboEffect;
    private float time;
    private boolean ability = false;
    private int range;
    private int damage;
    private int speed;
    private int missiles;
    private ParticleEffect effect;
    private Combo combo;

    public Tower(float x, float y, Level.Resource type, final Level level) {
        super(x, y, Main.TOWER_RADIUS, level, type);
        damage = 0;
        range = 0;
        speed = 0;
        setDrawable(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "red" : type == Level.Resource.BLUE ? "blue" : "yellow")));
        if (type == Level.Resource.YELLOW) effect = yellowPool.obtain();
        targeting = type == Level.Resource.RED ? Targeting.FIRST : type == Level.Resource.BLUE ? Targeting.STRONGEST : Targeting.NEAREST;
        comboEffect = new ParticleEffect();
        comboEffect.load(Gdx.files.internal("particles/combo.p"), Gdx.files.internal("particles/"));
        level.particles.add(comboEffect);
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

    public static void refreshNeighbors(Level level) {
        for (Building building : level.buildings) {
            if (building instanceof Generator) continue;
            ((Tower) building).neighbors.clear();
        }
        for (Building building : level.buildings) {
            if (building instanceof Generator) continue;
            Tower tower = ((Tower) building);
            for (Building oBuilding : level.buildings) {
                if (oBuilding instanceof Generator) continue;
                if (tower == oBuilding) continue;
                if (tower.neighbors.contains(oBuilding)) continue;
                if (new Vector2(building.getX(), building.getY()).dst(oBuilding.getX(), oBuilding.getY()) <= 4 * Main.TOWER_RADIUS) {
                    tower.neighbors.add(((Tower) oBuilding));
                    ((Tower) oBuilding).neighbors.add(tower);
                }
            }
            if (tower.getCurrentCombo() == null || !tower.getCombos().contains(tower.getCurrentCombo())) tower.getNewCombo();
            if (level.selectedBuilding == building) Menu.select();
        }
    }

    public void act(float delta) {
        time += delta * getSpeed();
        if (comboUpgrade && getCurrentCombo() != null) {
            comboTimer += delta * getSpeed();
            comboParticleTimer += delta * getSpeed();
            if (comboTimer >= 100) {
                Combo combo = getCurrentCombo();
                combo.fire();
                getNewCombo();
                // TODO fire combos (and make it pretty)
            }
            while (comboTimer < 100 && comboParticleTimer >= 1) {
                Combo combo = getCurrentCombo();
                comboParticleTimer -= 1;
                int red = combo.red;
                int blue = combo.blue;
                int yellow = combo.yellow;
                for (Tower tower : neighbors) {
                    if (!tower.comboUpgrade) continue;
                    if ((tower.type == Level.Resource.RED && red > 0) || (tower.type == Level.Resource.BLUE && blue > 0) || (tower.type == Level.Resource.YELLOW && yellow > 0)){
                        if (tower.type == Level.Resource.RED) red--;
                        if (tower.type == Level.Resource.BLUE) blue--;
                        if (tower.type == Level.Resource.YELLOW) yellow--;
                        ParticleEffect effect = tower.comboEffect;
                        effect.getEmitters().first().getAngle().setHigh(new Vector2(getX() - tower.getX(), getY() - tower.getY()).angle());
                        float dist = new Vector2(tower.getX(), tower.getY()).dst(getX(), getY());
                        effect.getEmitters().first().getLife().setHigh(dist * 10);
                        effect.getEmitters().first().getTint().setColors(new float[]{tower.type == Level.Resource.RED || tower.type == Level.Resource.YELLOW ? 1 : 0, tower.type == Level.Resource.YELLOW ? 1 : 0, type == Level.Resource.BLUE ? 1 : 0});
                        effect.setPosition(tower.getX() + Main.TOWER_RADIUS, tower.getY() + Main.TOWER_RADIUS);
                        effect.getEmitters().first().addParticle();
                    }
                }
            }
        } else comboTimer = comboParticleTimer = 0;
        Enemy target = targeting.target(this, new Vector2(getX(), getY()));
        if ((target == null && !(type == Level.Resource.BLUE && ability)) || (type == Level.Resource.BLUE && missiles >= getSpeed())) {
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
                    if (ability) {
                        Circle area = new Circle(target.getX(), target.getY(), 2 * Main.TOWER_RADIUS);
                        for (int i = 0; i < level.enemies.size(); ) {
                            Enemy enemy = level.enemies.get(i);
                            if (area.contains(enemy.getPosition())) {
                                if (enemy.hit(getDamage())) {
                                    kills++;
                                    level.totalKills++;
                                    continue;
                                }
                            }
                            i++;
                        }
                    }
                    shots++;
                }
                break;
            case BLUE:
                while (time >= Main.TOWER_SPEED && missiles < getSpeed()) {
                    time -= Main.TOWER_SPEED;
                    ParticleEffect effect = bluePool.obtain();
                    effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                    level.particles.add(effect);
                    final Enemy finalTarget = target;
                    level.stage.addActor(new ParticleEffectActor(effect, getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS) {
                        float angle = new Vector2(finalTarget == null ? MathUtils.random() : finalTarget.getX() - getX(), finalTarget == null ? MathUtils.random() : finalTarget.getY() - getY()).angle();

                        public void act(float delta) {
                            if (getX() < 0 || getX() > level.prototype.width || getY() < 0 || getY() > level.prototype.height) {
                                remove();
                                missiles--;
                                effect.allowCompletion();
                                return;
                            }
                            Enemy target;
                            if (finalTarget == null) target = targeting.target(Tower.this, new Vector2(getX(), getY()));
                            else {
                                float length = finalTarget.getPosition().cpy().sub(getX(), getY()).len();
                                if (level.enemies.contains(finalTarget) && length <= getRange()) {
                                    target = finalTarget;
                                } else target = targeting.target(Tower.this, new Vector2(getX(), getY()));
                            }
                            if (target != null) {
                                float dist = target.getPosition().cpy().sub(getX(), getY()).len();
                                if (dist < Main.BULLET_SPEED * delta) {
                                    if (target.hit(getDamage())) {
                                        kills++;
                                        level.totalKills++;
                                    }
                                    remove();
                                    missiles--;
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
                            } else if (ability) angle += Main.TURN_RADIUS / 4f;
                            setPosition(getX() + Main.BULLET_SPEED * MathUtils.cosDeg(angle) * delta, getY() + Main.BULLET_SPEED * MathUtils.sinDeg(angle) * delta);
                            super.act(delta);
                            effect.getEmitters().first().getAngle().setLow(angle);
                        }
                    });
                    shots++;
                    missiles++;
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
                            if (ability && enemy.hit(getDamage())) {
                                kills++;
                                level.totalKills++;
                                continue;
                            }
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
        refreshNeighbors(level);
        for (Tower tower : neighbors)
            if (!tower.getCombos().contains(tower.getCurrentCombo())) tower.getNewCombo();
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
                if (damage == 11 && type == Level.Resource.RED) ability = true;
            }
        }
    }

    public void upgradeRange() {
        if (range < 11) {
            if (level.blueResource >= rangeCosts[range]) {
                blueValue += Main.SELL_RATE * rangeCosts[range];
                level.blueResource -= rangeCosts[range];
                range++;
                if(range == 11 && type == Level.Resource.BLUE) ability = true;
            }
        }
    }

    public void upgradeSpeed() {
        if (speed < 11) {
            if (level.yellowResource >= speedCosts[speed]) {
                yellowValue += Main.SELL_RATE * speedCosts[speed];
                level.yellowResource -= speedCosts[speed];
                speed++;
                if (speed == 11 && type == Level.Resource.YELLOW) ability = true;
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

    public void comboUpgrade() {
        if (!comboUpgrade && level.redResource >= 50 && level.blueResource >= 50 && level.yellowResource >= 50) {
            level.redResource -= 50;
            level.blueResource -= 50;
            level.yellowResource -= 50;
            comboUpgrade = true;
            getNewCombo();
            for (Tower tower : neighbors)
                if (tower.getCurrentCombo() == null) tower.getNewCombo();
            setDrawable(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "redUp" : type == Level.Resource.BLUE ? "blueUp" : "yellowUp")));
        }
    }

    private Combo getCurrentCombo() {
        return combo;
    }

    private void getNewCombo() {
        combo = null;
        ArrayList<Combo> combos = getCombos();
        if (!combos.isEmpty())
            combo = combos.get(MathUtils.random(combos.size() - 1));
    }

    public ArrayList<Combo> getCombos() {
        ArrayList<Combo> combos = new ArrayList<>();
        int red = 0, blue = 0, yellow = 0;
        for (Tower tower : neighbors) {
            if (!tower.comboUpgrade) continue;
            switch (tower.type) {
                case RED:
                    red++;
                    break;
                case BLUE:
                    blue++;
                    break;
                case YELLOW:
                    yellow++;
                    break;
            }
        }
        for (Combo combo : Combo.values()) {
            if (combo.type == type && red >= combo.red && blue >= combo.blue && yellow >= combo.yellow) combos.add(combo);
        }
        return combos;
    }

    public enum Targeting implements Comparator<Enemy>{
        NEAREST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                float length = enemy.getPosition().cpy().sub(point).len();
                float olength = oEnemy.getPosition().cpy().sub(point).len();
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
        public Vector2 point;

        public Enemy target(Tower tower, Vector2 point) {
            this.tower = tower;
            this.point = point;
            ArrayList<Enemy> potential = new ArrayList<>();
            for (Enemy enemy : tower.level.enemies) {
                float length = enemy.getPosition().cpy().sub(point).len();
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

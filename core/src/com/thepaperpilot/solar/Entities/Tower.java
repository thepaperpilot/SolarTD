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
    protected boolean ability = false;
    public int range;
    private int damage;
    private int speed;
    protected int missiles;
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
        return type == Level.Resource.RED ? balancedCost(25) : 0;
    }

    public static float getBlueCost(Level.Resource type) {
        return type == Level.Resource.BLUE ? balancedCost(25) : 0;
    }

    public static float getYellowCost(Level.Resource type) {
        return type == Level.Resource.YELLOW ? balancedCost(25) : 0;
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
            Combo combo = getCurrentCombo();
            if (comboTimer >= Main.COMBO_TIME * (combo.red + combo.blue + combo.yellow) && targeting.target(this, new Vector2(getX(), getY())) != null) {
                if(combo.fire(this)) {
                    getNewCombo();
                    comboTimer = 0;
                }
            }
            if (comboTimer < Main.COMBO_TIME * (combo.red + combo.blue + combo.yellow) && comboParticleTimer >= 1) {
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
        Enemy target = targeting.target(this, new Vector2(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS));
        if (target == null && !(type == Level.Resource.BLUE && ability && missiles <= getSpeed())) {
            time = Math.min(time, Main.TOWER_SPEED);
            if (type == Level.Resource.YELLOW) {
                effect.allowCompletion();
            }
        } else switch (type) {
            default:
            case RED:
                while (time >= Main.TOWER_SPEED) {
                    Main.getSound("red").play(Main.volume);
                    time -= Main.TOWER_SPEED;
                    ParticleEffect effect = redPool.obtain();
                    effect.setPosition(getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS);
                    level.particles.add(effect);
                    effect = ability ? red2Pool.obtain() : redPool.obtain();
                    if (ability) effect.getEmitters().first().getLife().setHigh(range * 2, range * 3);
                    effect.setPosition(target.getX() + Main.ENEMY_SIZE / 2, target.getY() + Main.ENEMY_SIZE / 2);
                    level.particles.add(effect);
                    if (target.hit(getDamage())) {
                        kills++;
                        level.totalKills++;
                    }
                    if (ability) {
                        Circle area = new Circle(target.getX(), target.getY(), range / 2);
                        effect.getEmitters().first().getLife().setHigh(range);
                        int maxHit = (int) getSpeed();
                        for (int i = 0; i < level.enemies.size(); ) {
                            Enemy enemy = level.enemies.get(i);
                            if (area.contains(enemy.getPosition())) {
                                maxHit--;
                                if (maxHit < 0) break;
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
                    Main.getSound("blue").play(Main.volume);
                    time -= Main.TOWER_SPEED;
                    new Missile(Rocket.bluePool, getX() + Main.TOWER_RADIUS, getY() + Main.TOWER_RADIUS, level, this, getDamage(), ability);
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
                    // TODO yellow sound
                    // Main.getSound("yellow").play();
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
                            enemy.slowed = getSpeed();
                            enemy.slowSpeed = getDamage();
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
            if (level.redResource >= balancedCost(damageCosts[damage])) {
                redValue += Main.SELL_RATE * balancedCost(damageCosts[damage]);
                level.redResource -= balancedCost(damageCosts[damage]);
                damage++;
                if (damage == 6 && type == Level.Resource.RED) ability = true;
            }
        }
    }

    public void upgradeRange() {
        if (range < 11) {
            if (level.blueResource >= balancedCost(rangeCosts[range])) {
                blueValue += Main.SELL_RATE * balancedCost(rangeCosts[range]);
                level.blueResource -= balancedCost(rangeCosts[range]);
                range++;
                if(range == 6 && type == Level.Resource.BLUE) ability = true;
            }
        }
    }

    public void upgradeSpeed() {
        if (speed < 11) {
            if (level.yellowResource >= balancedCost(speedCosts[speed])) {
                yellowValue += Main.SELL_RATE * balancedCost(speedCosts[speed]);
                level.yellowResource -= balancedCost(speedCosts[speed]);
                speed++;
                if (speed == 6 && type == Level.Resource.YELLOW) ability = true;
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
        return damage < 11 ? balancedCost(damageCosts[damage]) : -1;
    }

    public int getRangeCost() {
        return range < 11 ? balancedCost(rangeCosts[range]) : -1;
    }

    public int getSpeedCost() {
        return speed < 11 ? balancedCost(speedCosts[speed]) : -1;
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

    public Combo getCurrentCombo() {
        return combo;
    }

    private void getNewCombo() {
        combo = null;
        ArrayList<Combo> combos = getCombos();
        if (!combos.isEmpty())
            combo = combos.get(MathUtils.random(combos.size() - 1));
        if (level.selectedBuilding == this) Menu.select();
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
                return MathUtils.round(length - olength);
            }
        },
        FIRST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return MathUtils.round(enemy.getDistance() - oEnemy.getDistance());
            }
        },
        LAST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return MathUtils.round(oEnemy.getDistance() - enemy.getDistance());
            }
        },
        STRONGEST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return MathUtils.round(oEnemy.getHealth() - enemy.getHealth());
            }
        },
        WEAKEST {
            @Override
            public int compare(Enemy enemy, Enemy oEnemy) {
                return MathUtils.round(enemy.getHealth() - oEnemy.getHealth());
            }
        };

        public Tower tower;
        public Vector2 point;

        public Enemy target(Tower tower, Vector2 point) {
            this.tower = tower;
            this.point = point;
            ArrayList<Enemy> potential = new ArrayList<>();
            for (Enemy enemy : tower.level.enemies) {
                if (enemy.dead) continue;
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

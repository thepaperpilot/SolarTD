package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

import java.util.ArrayList;

public class Enemy extends Image {
    private static final ParticleEffectPool deathPool;
    private static final ParticleEffectPool death2Pool;

    static {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/death.p"), Gdx.files.internal("particles/"));
        deathPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/death2.p"), Gdx.files.internal("particles/"));
        death2Pool = new ParticleEffectPool(particleEffect, 0, 100);
    }

    private final float speed;
    private final Level level;
    public float slowed;
    public float slowSpeed;
    public float poison;
    public float poisonDamage;
    public float health;
    public float totalHealth;
    private int path;
    private ArrayList<FloatAction> damages = new ArrayList<>();
    public boolean dead;

    public Enemy(EnemyPrototype enemyPrototype, Level level) {
        setDrawable(Main.getDrawable(enemyPrototype.name));
        setSize(Main.ENEMY_SIZE, Main.ENEMY_SIZE);
        this.health = enemyPrototype.health;
        this.speed = enemyPrototype.speed;
        this.level = level;
    }

    public static Table getTable(EnemyPrototype prototype, int wave) {
        Table table = new Table(Main.skin);
        table.add(new Image(Main.getDrawable(prototype.name))).size(32).left();
        Table count = new Table(Main.skin);
        count.add(new Label("Count", Main.skin)).row();
        count.add(new Label("" + prototype.count, Main.skin, "large"));
        table.add(count).expand();
        Table health = new Table(Main.skin);
        health.add(new Label("Health", Main.skin)).row();
        health.add(new Label("" + MathUtils.ceil(prototype.health * (float) Math.pow(wave, Level.getHealthRate())), Main.skin, "large"));
        table.add(health).expand();
        Table speed = new Table(Main.skin);
        speed.add(new Label("Speed", Main.skin)).row();
        speed.add(new Label("" + prototype.speed, Main.skin, "large"));
        table.add(speed).expand();
        return table;
    }

    public boolean hit(final float damage) {
        final FloatAction action = new FloatAction(0, damage);
        action.setDuration(.5f);
        addAction(Actions.sequence(action, Actions.run(new Runnable() {
            @Override
            public void run() {
                health -= damage;
                damages.remove(action);
            }
        })));
        damages.add(action);
        return getEndHealth() <= damage;
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void act(float delta) {
        super.act(delta);
        float tempSpeed = speed * Main.ENEMY_SPEED;
        if (slowed > 0) {
            slowed -= delta;
            tempSpeed *= 10 / (slowSpeed + 10);
        } else slowed = 0;
        if (poison > 0) {
            poison -= delta;
            hit(delta * poisonDamage);
        }
        Vector2 dist = new Vector2(level.path[path + 1].x - getX() - Main.ENEMY_SIZE / 2, level.path[path + 1].y - getY() - Main.ENEMY_SIZE / 2);
        float angle = dist.angle();
        angle += 4 * MathUtils.sinDeg(dist.len());
        if (dist.len() < Main.ENEMY_SPEED * delta) {
            path++;
            if (path == level.path.length - 1) {
                if (level.enemies.contains(this)) {
                    level.enemies.remove(this);
                    remove();
                    level.hit(health);
                    Main.getSound("hit").play(Main.volume);
                    ParticleEffect effect = death2Pool.obtain();
                    effect.setPosition(getX() + Main.ENEMY_SIZE / 2, getY() + Main.ENEMY_SIZE / 2);
                    level.particles.add(effect);
                }
            } else setPosition(level.path[path].x - Main.ENEMY_SIZE / 2, level.path[path].y - Main.ENEMY_SIZE / 2);
        } else
            setPosition(getX() + tempSpeed * MathUtils.cosDeg(angle) * delta, getY() + tempSpeed * MathUtils.sinDeg(angle) * delta);
        if (getEndHealth() <= 0) dead = true;
        if (getHealth() <= 0) {
            level.enemies.remove(this);
            remove();
            Main.getSound("death").play(Main.volume);
            ParticleEffect effect = deathPool.obtain();
            effect.setPosition(getX() + Main.ENEMY_SIZE / 2, getY() + Main.ENEMY_SIZE / 2);
            level.particles.add(effect);
        }
    }

    public float getHealth() {
        float hp = health;
        for (FloatAction action : damages) {
            hp -= action.getValue();
        }
        return hp;
    }

    private float getEndHealth() {
        float hp = health;
        for (FloatAction action : damages) {
            hp -= action.getEnd();
        }
        return hp;
    }

    public float getDistance() {
        float dist = new Vector2(level.path[path + 1].x - getX() - Main.ENEMY_SIZE / 2, level.path[path + 1].y - getY() - Main.ENEMY_SIZE / 2).len();
        for (int i = path + 1; i < level.path.length - 1; i++) {
            dist += new Vector2(level.path[i + 1].x - level.path[i].x - Main.ENEMY_SIZE / 2, level.path[i + 1].y - level.path[i].y - Main.ENEMY_SIZE / 2).len();
        }
        return dist;
    }

    public static class EnemyPrototype {
        public String name;
        public int count;
        public float health;
        public float speed;
    }
}

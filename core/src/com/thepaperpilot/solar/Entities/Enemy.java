package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Enemy extends Image {
    private static final ParticleEffectPool deathPool;

    static {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/death.p"), Gdx.files.internal("particles/"));
        deathPool = new ParticleEffectPool(particleEffect, 0, 100);
    }

    private final float speed;
    private final Level level;
    public float slowed;
    public float health;
    private int path;

    public Enemy(EnemyPrototype enemyPrototype, Level level) {
        setDrawable(Main.getDrawable(enemyPrototype.name));
        setSize(Main.ENEMY_SIZE, Main.ENEMY_SIZE);
        this.health = enemyPrototype.health;
        this.speed = enemyPrototype.speed;
        this.level = level;
    }

    public boolean hit(float damage) {
        health -= damage;
        if (health <= 0) {
            level.enemies.remove(this);
            remove();
            ParticleEffect effect = deathPool.obtain();
            effect.setPosition(getX(), getY());
            level.particles.add(effect);
            return true;
        }
        return false;
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void act(float delta) {
        float tempSpeed = speed * Main.ENEMY_SPEED;
        if (slowed > 0) {
            slowed -= delta;
            tempSpeed *= .5f; // TODO make this dynamic somehow
            // maybe a condition class?
        } else slowed = 0;
        Vector2 dist = new Vector2(level.path[path + 1].x - getX() - Main.ENEMY_SIZE / 2, level.path[path + 1].y - getY() - Main.ENEMY_SIZE / 2);
        float angle = dist.angle();
        angle += 4 * MathUtils.sinDeg(dist.len());
        if (dist.len() < Main.ENEMY_SPEED * delta) {
            path++;
            if (path == level.path.length - 1) {
                if (level.enemies.contains(this)) {
                    level.enemies.remove(this);
                    remove();
                    level.population -= health;
                }
            } else setPosition(level.path[path].x - Main.ENEMY_SIZE / 2, level.path[path].y - Main.ENEMY_SIZE / 2);
        } else
            setPosition(getX() + tempSpeed * MathUtils.cosDeg(angle) * delta, getY() + tempSpeed * MathUtils.sinDeg(angle) * delta);
    }

    public float getDistance() {
        float dist = 0;
        for (int i = path; i < level.path.length - 1; i++) {
            dist += new Vector2(level.path[i + 1].x - getX() - Main.ENEMY_SIZE / 2, level.path[i + 1].y - getY() - Main.ENEMY_SIZE / 2).len();
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

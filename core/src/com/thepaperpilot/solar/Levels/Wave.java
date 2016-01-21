package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.thepaperpilot.solar.Entities.Enemy;

import java.util.ArrayList;
import java.util.Collections;

public class Wave extends Actor{
    public final float enemyDistance;
    public final ArrayList<Enemy.EnemyPrototype> enemies;
    private final Level level;
    public float tempDistance;
    private int currEnemyType;
    private int currEnemyCount;
    private float time;

    public Wave(WavePrototype wavePrototype, Level level) {
        this.level = level;
        enemies = new ArrayList<>();
        Collections.addAll(enemies, wavePrototype.enemies);
        enemyDistance = wavePrototype.enemyDistance;
        tempDistance = MathUtils.random(enemyDistance) + enemyDistance / 2;
    }

    public float getTime() {
        float time = 0;
        for (Enemy.EnemyPrototype enemy : enemies) {
            time += enemy.count;
        }
        time *= enemyDistance;
        return time;
    }

    public void act(float delta) {
        time += delta;
        if (time >= tempDistance) {
            time -= tempDistance;
            level.addEnemy(getEnemy(level));
            if (isEmpty()) remove();
        }
    }

    public boolean isEmpty() {
        return currEnemyType >= enemies.size();
    }

    public Enemy getEnemy(Level level) {
        Enemy enemy = new Enemy(enemies.get(currEnemyType), level);
        enemy.health = enemy.totalHealth = MathUtils.ceil(enemy.getHealth() * (float) Math.pow(level.wave, Level.getHealthRate()));
        currEnemyCount++;
        if (currEnemyCount == enemies.get(currEnemyType).count) {
            currEnemyType++;
            currEnemyCount = 0;
        }
        return enemy;
    }

    public static class WavePrototype {
        public Enemy.EnemyPrototype[] enemies = new Enemy.EnemyPrototype[]{};
        public float enemyDistance;
    }
}

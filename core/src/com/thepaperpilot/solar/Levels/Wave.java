package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.thepaperpilot.solar.Entities.Enemy;

public class Wave extends Actor{
    public final float enemyDistance;
    public final Enemy.EnemyPrototype[] enemies;
    private final Level level;
    public float tempDistance;
    private int currEnemyType;
    private int currEnemyCount;
    private float time;

    public Wave(WavePrototype wavePrototype, Level level) {
        this.level = level;
        enemies = wavePrototype.enemies;
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
        return currEnemyType >= enemies.length;
    }

    public Enemy getEnemy(Level level) {
        Enemy enemy = new Enemy(enemies[currEnemyType], level);
        enemy.health = MathUtils.ceil(enemy.health * (float) Math.pow(level.wave, Level.getHealthRate()));
        currEnemyCount++;
        if (currEnemyCount == enemies[currEnemyType].count) {
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

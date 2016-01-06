package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.thepaperpilot.solar.Entities.Enemy;

public class Wave extends Actor{
    public final float enemyDistance;
    private final Level level;
    private final Enemy.EnemyPrototype[] enemies;
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
            if (isEmpty()) {
                level.wave++;
                level.population += level.wave;
                remove();
            }
        }
    }

    public boolean isEmpty() {
        return currEnemyType >= enemies.length;
    }

    public Enemy getEnemy(Level level) {
        Enemy enemy = new Enemy(enemies[currEnemyType], level);
        enemy.health *= Math.pow(level.wave, 1.2f);
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

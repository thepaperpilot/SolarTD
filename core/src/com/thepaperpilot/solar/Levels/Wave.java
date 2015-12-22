package com.thepaperpilot.solar.Levels;

import com.thepaperpilot.solar.Entities.Enemy;

public class Wave {
    public final float enemyDistance;
    private final Enemy.EnemyPrototype[] enemies;
    private int currEnemyType;
    private int currEnemyCount;

    public Wave(WavePrototype wavePrototype) {
        enemies = wavePrototype.enemies;
        enemyDistance = wavePrototype.enemyDistance;
    }

    public boolean isEmpty() {
        return currEnemyType >= enemies.length;
    }

    public Enemy getEnemy(Level level) {
        Enemy enemy = new Enemy(enemies[currEnemyType], level);
        currEnemyCount++;
        if (currEnemyCount == enemies[currEnemyType].count) {
            currEnemyType++;
            currEnemyCount = 0;
        }
        return enemy;
    }

    public static class WavePrototype {
        public Enemy.EnemyPrototype[] enemies;
        public float enemyDistance;
    }
}

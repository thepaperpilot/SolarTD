package com.thepaperpilot.solar.Levels;

import com.thepaperpilot.solar.Entities.Enemy;

public class Wave {
    public Enemy.EnemyPrototype[] enemies;
    public float enemyDistance;
    private int currEnemyType;
    private int currEnemyCount;

    public Wave(WavePrototype wavePrototype) {
        enemies = wavePrototype.enemies;
        enemyDistance = wavePrototype.enemyDistance;
    }

    public boolean isEmpty() {
        return currEnemyType >= enemies.length;
    }

    public Enemy getEnemy() {
        Enemy enemy = new Enemy(enemies[currEnemyType]);
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

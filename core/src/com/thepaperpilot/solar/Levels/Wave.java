package com.thepaperpilot.solar.Levels;

import com.thepaperpilot.solar.Entities.Enemy;

public class Wave {
    public Wave(WavePrototype wavePrototype) {

    }

    public static class WavePrototype {
        Enemy.EnemyPrototype[] enemies;

        public WavePrototype() {

        }
    }
}

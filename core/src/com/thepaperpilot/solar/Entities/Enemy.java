package com.thepaperpilot.solar.Entities;

public class Enemy {
    public Enemy(EnemyPrototype enemyPrototype) {

    }

    public static class EnemyPrototype {
        String name;
        int count;
        int health;
        float speed;

        public EnemyPrototype(){

        }
    }
}

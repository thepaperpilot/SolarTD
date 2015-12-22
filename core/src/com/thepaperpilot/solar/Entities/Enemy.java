package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.thepaperpilot.solar.Main;

public class Enemy extends Image {
    public int health;
    public float speed;

    public Enemy(EnemyPrototype enemyPrototype) {
        setDrawable(Main.getDrawable(enemyPrototype.name));
        setSize(Main.ENEMY_SIZE, Main.ENEMY_SIZE);
        this.health = enemyPrototype.health;
        this.speed = enemyPrototype.speed;
    }

    public static class EnemyPrototype {
        public String name;
        public int count;
        public int health;
        public float speed;
    }
}

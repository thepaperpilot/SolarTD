package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.solar.Main;

public class Bomb extends Rocket{
    private final Tower tower;
    protected Enemy target;

    public Bomb(ParticleEffectPool pool, float x, float y, Tower tower) {
        super(pool, x, y, tower.level);
        this.tower = tower;
        target = tower.targeting.target(tower, new Vector2(tower.getX(), tower.getY()));
        Vector2 target = getTarget();
        if (target != null)
            angle = new Vector2(target.x - getX(), target.y - getY()).angle();
    }

    public void noTarget() {
        hit();
        destroy();
    }

    public Vector2 getTarget() {
        return level.enemies.contains(target) ? target.getPosition() : null;
    }

    public void hit() {
        ParticleEffect effect = Rocket.boomPool.obtain();
        effect.getEmitters().first().getLife().setHigh(tower.getRange() * 2, tower.getRange() * 3);
        effect.setPosition(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2);
        level.particles.add(effect);
        Circle area = new Circle(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, tower.getRange() / 2);
        for (int i = 0; i < level.enemies.size(); ) {
            Enemy enemy = level.enemies.get(i);
            if (area.contains(enemy.getPosition()) && enemy.hit(tower.getDamage())) {
                continue;
            }
            i++;
        }
    }
}

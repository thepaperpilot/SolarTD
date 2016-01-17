package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Missile extends Rocket{
    Enemy target;
    final float damage;
    final Tower tower;
    final boolean hold;

    public Missile(ParticleEffectPool pool, float x, float y, Level level, Tower tower, float damage, boolean hold) {
        super(pool, x, y, level);
        this.tower = tower;
        this.damage = damage;
        this.target = tower.targeting.target(tower, new Vector2(x, y));
        this.hold = hold;
        if (target != null)
            angle = target.getPosition().cpy().sub(new Vector2(x, y)).angle();
    }

    public void noTarget() {
        if (hold) angle += Main.TURN_RADIUS / 4f;
    }

    public Vector2 getTarget() {
        if (level == null || !level.enemies.contains(target)) target = tower.targeting.target(tower, new Vector2(getX(), getY()));
        return target == null ? null : target.getPosition();
    }

    public void destroy() {
        tower.missiles--;
        super.destroy();
    }

    public void hit() {
        if (target.hit(damage)) {
            tower.kills++;
            level.totalKills++;
        }
    }
}

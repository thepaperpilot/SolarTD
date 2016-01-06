package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class Mine extends Image{
    private static final ParticleEffectPool normalPool;

    static {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/mine.p"), Gdx.files.internal("particles/"));
        normalPool = new ParticleEffectPool(particleEffect, 0, 100);
    }

    private final float damage;
    private final float range;
    private final Level level;

    public Mine(float damage, float range, Level level) {
        super(Main.getDrawable("mine"));
        setSize(Main.TOWER_RADIUS, Main.TOWER_RADIUS);
        int path = MathUtils.random(level.path.length - 2);
        Vector2 newPosition = new Vector2(level.path[path].x, level.path[path].y);
        Vector2 difference = new Vector2(level.path[path + 1].x - level.path[path].x, level.path[path + 1].y - level.path[path].y);
        difference.setLength(MathUtils.random(difference.len()));
        newPosition.add(difference);
        newPosition.sub(Main.TOWER_RADIUS / 2, Main.TOWER_RADIUS / 2);
        addAction(Actions.moveTo(newPosition.x, newPosition.y, 1));
        this.damage = damage;
        this.range = range;
        this.level = level;
    }

    public void act(float delta) {
        super.act(delta);
        for (Enemy enemy : level.enemies) {
            Circle area = new Circle(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, Main.TOWER_RADIUS);
            if (area.contains(enemy.getPosition())) {
                area.radius = range / 2;
                ParticleEffect effect = normalPool.obtain();
                effect.getEmitters().first().getLife().setHigh(range * 2, range * 3);
                effect.setPosition(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2);
                level.particles.add(effect);
                for (int i = 0; i < level.enemies.size(); ) {
                    Enemy enemy1 = level.enemies.get(i);
                    if (area.contains(enemy1.getPosition()) && enemy1.hit(damage)) {
                        continue;
                    }
                    i++;
                }
                remove();
                break;
            }
        }
    }
}

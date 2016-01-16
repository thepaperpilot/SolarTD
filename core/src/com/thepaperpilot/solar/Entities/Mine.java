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

    protected final Tower tower;
    protected final float damage;
    protected final float range;
    protected final Level level;

    public Mine(Vector2 pos, Tower tower, float damage, float range, Level level) {
        super(Main.getDrawable("mine"));
        this.tower = tower;
        this.damage = damage;
        this.range = range;
        this.level = level;

        float pathLength = 0;
        for (int i = 0; i < level.path.length - 1; i++) {
            pathLength += new Vector2(level.path[i + 1].x - level.path[i].x, level.path[i + 1].y - level.path[i].y).len();
        }
        float targetLength = MathUtils.random(pathLength);
        int i = 0;
        while (targetLength > new Vector2(level.path[i + 1].x - level.path[i].x, level.path[i + 1].y - level.path[i].y).len()) {
            targetLength -= new Vector2(level.path[i + 1].x - level.path[i].x, level.path[i + 1].y - level.path[i].y).len();
            i++;
        }
        Vector2 target = new Vector2(level.path[i + 1].x - level.path[i].x, level.path[i + 1].y - level.path[i].y).setLength(targetLength).add(new Vector2(level.path[i].x, level.path[i].y));
        addAction(Actions.moveTo(target.x, target.y, target.cpy().sub(pos).len() / 600));

        setPosition(pos.x, pos.y);
        setSize(Main.TOWER_RADIUS, Main.TOWER_RADIUS);
        level.stage.addActor(this);
    }

    public void act(float delta) {
        super.act(delta);
        for (Enemy enemy : level.enemies) {
            Circle area = new Circle(getX() + getWidth() / 2, getY() + getHeight() / 2, Main.TOWER_RADIUS);
            if (area.contains(enemy.getPosition())) {
                area.radius = range / 2;
                ParticleEffect effect = normalPool.obtain();
                effect.getEmitters().first().getLife().setHigh(range * 2, range * 3);
                effect.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
                level.particles.add(effect);
                for (int i = 0; i < level.enemies.size(); ) {
                    Enemy enemy1 = level.enemies.get(i);
                    if (area.contains(enemy1.getPosition())) {
                        if (enemy1.hit(damage)) continue;
                    }
                    i++;
                }
                hit();
                remove();
                break;
            }
        }
    }

    protected void hit() {

    }
}

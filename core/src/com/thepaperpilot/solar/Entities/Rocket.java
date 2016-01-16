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

public class Rocket extends ParticleEffectActor{
    public static final ParticleEffectPool redPool;
    public static final ParticleEffectPool bluePool;
    public static final ParticleEffectPool yellowPool;
    public static final ParticleEffectPool greenPool;
    public static final ParticleEffectPool bigPool;
    public static final ParticleEffectPool boomPool;
    public static final ParticleEffectPool greenBoomPool;
    public static final ParticleEffectPool bfgPool;

    static {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{1, 0, 0});
        redPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        bluePool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{1, 1, 0});
        yellowPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{0, 1, 0});
        greenPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blue.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{1, 0, 0});
        particleEffect.getEmitters().first().getScale().setHigh(4);
        bigPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/mine.p"), Gdx.files.internal("particles/"));
        boomPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/mine.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{0, 1, 0});
        greenBoomPool = new ParticleEffectPool(particleEffect, 0, 100);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/bfg.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getTint().setColors(new float[]{0, 1, 0});
        bfgPool = new ParticleEffectPool(particleEffect, 0, 100);
    }

    protected Level level;
    public float angle;
    public float speed = 1;

    public Rocket(ParticleEffectPool pool, float x, float y, Level level) {
        super(pool.obtain(), x, y);
        effect.setPosition(x, y);
        level.particles.add(effect);
        level.stage.addActor(this);
        this.level = level;
        init();
    }

    public void act(float delta) {
        if (getX() < 0 || getX() > level.prototype.width || getY() < 0 || getY() > level.prototype.height) {
            destroy();
            return;
        }
        Vector2 target = getTarget();
        if (target != null) {
            float dist = target.cpy().sub(getX(), getY()).len();
            if (dist < Main.BULLET_SPEED * delta) {
                hit();
                destroy();
                return;
            }
            float newAngle = new Vector2(target.x - getX(), target.y - getY()).angle();
            while (newAngle > angle + 180) newAngle -= 360;
            while (newAngle < angle - 180) newAngle += 360;
            if (Math.abs(newAngle - angle) < Main.TURN_RADIUS)
                angle = newAngle;
            else if (newAngle > angle)
                angle += Main.TURN_RADIUS;
            else angle -= Main.TURN_RADIUS;
        } else {
            noTarget();
        }
        setPosition(getX() + Main.BULLET_SPEED * speed * MathUtils.cosDeg(angle) * delta, getY() + Main.BULLET_SPEED * speed * MathUtils.sinDeg(angle) * delta);
        effect.getEmitters().first().getAngle().setLow(angle);
        super.act(delta);
    }

    protected void init() {

    }

    protected void noTarget() {

    }

    protected Vector2 getTarget() {
        return new Vector2(getX(), getY());
    }

    protected void destroy() {
        remove();
        effect.allowCompletion();
    }

    protected void hit() {

    }
}

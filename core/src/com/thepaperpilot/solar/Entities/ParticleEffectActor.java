package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

class ParticleEffectActor extends Actor {
    final ParticleEffect effect;

    public ParticleEffectActor(ParticleEffect effect, float x, float y) {
        this.effect = effect;
        effect.start();
        setPosition(x, y);
    }

    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }

    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
        effect.update(delta);
    }
}
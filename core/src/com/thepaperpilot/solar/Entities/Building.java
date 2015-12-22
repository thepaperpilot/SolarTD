package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Building extends Image {

    public final Circle area;

    public Building(float x, float y, float radius) {
        this.area = new Circle(x + radius, y + radius, radius);
        setPosition(x, y);
        setSize(radius * 2, radius * 2);
    }
}

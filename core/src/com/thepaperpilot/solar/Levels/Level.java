package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class Level {
    private final static Json json = new Json();

    public Level(LevelPrototype levelPrototype) {

    }

    public static Level readLevel(String fileName) {
        // read the level from a JSON file
        return new Level(json.fromJson(LevelPrototype.class, Gdx.files.internal(fileName)));
    }


    public static class LevelPrototype {
        int width;
        int height;

        Point.PointPrototype[] path;
        Wave.WavePrototype[] waves;

        public LevelPrototype(){

        }
    }
}

package com.thepaperpilot.solar.Levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Json;

public class LevelGenerator {
    private final static Json json = new Json();

    public static void main(String[] args) {
        new LwjglApplication(new Game() {
            @Override
            public void create() {
                Level.LevelPrototype levelPrototype = new Level.LevelPrototype();
                levelPrototype.width = 980;
                levelPrototype.height = 540;
                levelPrototype.path = new float[]{980, 200, 500, 200, 300, 300, 300, 200, 100, 400, 980, 400};
                levelPrototype.waves = new Wave.WavePrototype[]{};

                String fileName = "level1.json";
                String output = json.prettyPrint(levelPrototype);
                System.out.println(Gdx.files.getExternalStoragePath() + fileName);
                System.out.println(output);
                System.out.println();
                Gdx.files.local(fileName).writeString(output, false);

                Gdx.app.exit();
            }
        }, new LwjglApplicationConfiguration());
    }
}

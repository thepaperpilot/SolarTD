package com.thepaperpilot.solar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends Game implements Screen {
    public static final float TOWER_RADIUS = 16;
    public static final float ENEMY_SIZE = 8;
    public static final float ENEMY_SPEED = 32;
    public static final float TOWER_SPEED = 4;
    public static final float BULLET_SPEED = 96;
    public static final float TURN_RADIUS = 6;
    public static final float UI_WIDTH = 720;
    public static final float SELL_RATE = .75f;
    public static final float GENERATOR_SPEED = 8;
    public static final float WAVE_INTERVAL = 10;
    public static final float HEALTH_RATE = 1.15f;
    public static final float COMBO_TIME = 40;
    private static final AssetManager manager = new AssetManager();
    public static Skin skin;
    public static float volume = 1;
    private static Main instance;
    private static SpriteBatch batch;
    private Stage loadingStage;

    public static void changeScreen(Screen screen) {
        if (screen == null)
            return;
        instance.setScreen(screen);
    }

    // TODO TextureAtlas
    public static Drawable getDrawable(String name) {
        Texture texture = Main.manager.get(name + ".png", Texture.class);
        // the atlas will fix this, but for now we'll set the filters here
        // this is not a pixel art game, so we want linear filtering (as opposed to nearest neighbor)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new Image(texture).getDrawable();
    }

    public static Sound getSound(String name) {
        return manager.get("audio/" + name + ".wav", Sound.class);
    }

    @Override
    public void create() {
        // use this so I can make a static changeScreen function
        // it basically makes Main a singleton
        instance = this;

        batch = new SpriteBatch();

        // start loading all our assets
        manager.load("skin.json", Skin.class);
        manager.load("towers/red.png", Texture.class);
        manager.load("towers/blue.png", Texture.class);
        manager.load("towers/yellow.png", Texture.class);
        manager.load("towers/redStore.png", Texture.class);
        manager.load("towers/blueStore.png", Texture.class);
        manager.load("towers/yellowStore.png", Texture.class);
        manager.load("towers/redStoreDown.png", Texture.class);
        manager.load("towers/blueStoreDown.png", Texture.class);
        manager.load("towers/yellowStoreDown.png", Texture.class);
        manager.load("towers/redUp.png", Texture.class);
        manager.load("towers/blueUp.png", Texture.class);
        manager.load("towers/yellowUp.png", Texture.class);
        manager.load("towers/redGen.png", Texture.class);
        manager.load("towers/blueGen.png", Texture.class);
        manager.load("towers/yellowGen.png", Texture.class);
        manager.load("towers/redGenStoreDown.png", Texture.class);
        manager.load("towers/blueGenStoreDown.png", Texture.class);
        manager.load("towers/yellowGenStoreDown.png", Texture.class);
        manager.load("bg.png", Texture.class);
        manager.load("title.png", Texture.class);
        manager.load("alien.png", Texture.class);
        manager.load("speed.png", Texture.class);
        manager.load("angry.png", Texture.class);
        manager.load("mine.png", Texture.class);
        manager.load("audio/blue.wav", Sound.class);
        manager.load("audio/build.wav", Sound.class);
        manager.load("audio/death.wav", Sound.class);
        manager.load("audio/generate.wav", Sound.class);
        manager.load("audio/hit.wav", Sound.class);
        manager.load("audio/lose.wav", Sound.class);
        manager.load("audio/red.wav", Sound.class);
        manager.load("audio/select.wav", Sound.class);
        manager.load("tutorial/HUD.png", Texture.class);

        // show this screen while it loads
        setScreen(this);
    }

    @Override
    public void show() {
        // show a basic loading screen
        loadingStage = new Stage(new ExtendViewport(200, 200));

        Label loadingLabel = new Label("Loading...", new Skin(Gdx.files.internal("skin.json")));
        loadingLabel.setFillParent(true);
        loadingLabel.setAlignment(Align.center);
        loadingStage.addActor(loadingLabel);

        // basically a sanity check? loadingStage shouldn't have any input listeners
        // but I guess this'll help if the inputprocessor gets set to something it shouldn't
        Gdx.input.setInputProcessor(loadingStage);
    }

    @Override
    public void render(float delta) {
        // render the loading screen
        // act shouldn't do anything, but putting it here is good practice, I guess?
        loadingStage.act();
        loadingStage.draw();

        // continue loading. If complete, do shit
        if (manager.update()) {
            // set some stuff we need universally, now that their assets are loaded
            skin = manager.get("skin.json", Skin.class);
            skin.getFont("large").getData().setScale(.5f);
            skin.getFont("font").getData().setScale(.25f);

            // go to the menu screen
            setScreen(MenuScreen.instance);
        }
    }

    @Override
    public void hide() {
        /// we're a good garbage collector
        loadingStage.dispose();
    }

    @Override
    public void pause() {
        // we're a passthrough!
        if (getScreen() == this || getScreen() == this) return;
        super.pause();
    }

    @Override
    public void resume() {
        // we're a passthrough!
        if (getScreen() == this || getScreen() == this) return;
        super.pause();
    }

    @Override
    public void resize(int width, int height) {
        // we're a passthrough!
        if (getScreen() == this) return;
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        // we're a passthrough!
        if (getScreen() != null && getScreen() != this) {
            getScreen().dispose();
        }
        // also clean up our shit
        manager.dispose();
        if (skin != null) skin.dispose();
        if (batch != null) batch.dispose();
    }

    @Override
    public void render() {
        // we're a passthrough!
        Gdx.gl.glClearColor(0, .015f, .025f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        getScreen().render(Math.min(.1f, Gdx.graphics.getDeltaTime()));
    }
}

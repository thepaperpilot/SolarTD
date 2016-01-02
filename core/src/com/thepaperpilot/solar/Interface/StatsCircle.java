package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thepaperpilot.solar.Entities.Building;
import com.thepaperpilot.solar.Entities.Generator;
import com.thepaperpilot.solar.Entities.Tower;
import com.thepaperpilot.solar.Levels.Level;
import com.thepaperpilot.solar.Main;

public class StatsCircle extends Table {
    public static final int SIZE = 60;

    private final ShapeRenderer shape;
    private final Label redValue;
    private final Label blueValue;
    private final Label yellowValue;

    private boolean tower;
    private float red;
    private float blue;
    private float yellow;
    private float efficiency;
    private float extractor;
    private Color color;

    public StatsCircle() {
        super(Main.skin);
        pad(4);
        shape = new ShapeRenderer();
        redValue = new Label("0", Main.skin);
        redValue.setColor(1, 0, 0, 1);
        blueValue = new Label("0", Main.skin);
        blueValue.setColor(0, 0, 1, 1);
        yellowValue = new Label("0", Main.skin);
        yellowValue.setColor(1, 1, 0, 1);
        add(new Label("Value", Main.skin)).spaceTop(SIZE).colspan(3).row();
        add(redValue).spaceRight(2).spaceBottom(SIZE);
        add(blueValue).spaceRight(2).spaceBottom(SIZE);
        add(yellowValue).spaceRight(2).spaceBottom(SIZE);
    }

    public void draw (Batch batch, float parentAlpha) {
        batch.end();
        Vector2 coords = getStage().stageToScreenCoordinates(localToStageCoordinates(new Vector2(SIZE / 2, SIZE / 2)));
        coords.y = getStage().getViewport().getScreenHeight() - coords.y;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        if (tower) {
            shape.setColor(1, 0, 0, parentAlpha / 2f);
            shape.arc(coords.x, coords.y, SIZE, 60 - (120 * red) / 2f, 120 * red);
            shape.setColor(0, 0, 1, parentAlpha / 2f);
            shape.arc(coords.x, coords.y, SIZE, 180 - (120 * blue) / 2f, 120 * blue);
            shape.setColor(1, 1, 0, parentAlpha / 2f);
            shape.arc(coords.x, coords.y, SIZE, 300 - (120 * yellow) / 2f, 120 * yellow);
        } else {
            shape.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            shape.arc(coords.x, coords.y, SIZE, 120 - (180 * efficiency) / 2f, 180 * efficiency);
            shape.arc(coords.x, coords.y, SIZE, 300 - (180 * extractor) / 2f, 180 * extractor);
        }
        shape.end();
        shape.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl20.glLineWidth(4);
        shape.setColor(.5f, .5f, .5f, parentAlpha);
        shape.circle(coords.x, coords.y, SIZE);
        shape.end();
        Gdx.gl20.glLineWidth(1);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    public void update(Building building) {
        tower = building instanceof Tower;
        if (tower) {
            Tower tower = ((Tower) building);
            red = tower.getDamageIndex() / 11f;
            blue = tower.getRangeIndex() / 11f;
            yellow = tower.getSpeedIndex() / 11f;
        } else {
            Generator generator = ((Generator) building);
            color = generator.type == Level.Resource.RED ? Color.RED : generator.type == Level.Resource.BLUE ? Color.BLUE : Color.YELLOW;
            efficiency = generator.getEfficiencyIndex() / 9f;
            extractor = generator.getExtractors() / 9f;
        }
        redValue.setText("" + (int) building.redValue);
        blueValue.setText("" + (int) building.blueValue);
        yellowValue.setText("" + (int) building.yellowValue);
    }
}

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

    private Color color;
    private Level level;

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

    public static void drawBottom(ShapeRenderer renderer, Vector2 coords, Building building, float parentAlpha, float size) {
        if (building instanceof Tower) {
            Tower tower = ((Tower) building);
            renderer.setColor(1, 0, 0, parentAlpha / 2f);
            renderer.arc(coords.x, coords.y, size, 60 - (120 * tower.getDamageIndex() / 11f) / 2f, 120 * tower.getDamageIndex() / 11f);
            renderer.setColor(0, 0, 1, parentAlpha / 2f);
            renderer.arc(coords.x, coords.y, size, 180 - (120 * tower.getRangeIndex() / 11f) / 2f, 120 * tower.getRangeIndex() / 11f);
            renderer.setColor(1, 1, 0, parentAlpha / 2f);
            renderer.arc(coords.x, coords.y, size, 300 - (120 * tower.getSpeedIndex() / 11f) / 2f, 120 * tower.getSpeedIndex() / 11f);
        } else {
            // CAREFUL BUG ALERT
            // set color BEFORE this method, and give a dummy parentAlpha
            Generator generator = ((Generator) building);
            renderer.arc(coords.x, coords.y, size, 120 - (180 * generator.getEfficiencyIndex() / 9f) / 2f, 180 * generator.getEfficiencyIndex() / 9f);
            renderer.arc(coords.x, coords.y, size, 300 - (180 * generator.getExtractors() / 9f) / 2f, 180 * generator.getExtractors() / 9f);
        }
    }

    private static void drawTop(ShapeRenderer renderer, Vector2 coords) {
        renderer.circle(coords.x, coords.y, SIZE);
    }

    public void init(Level level) {
        this.level = level;
    }

    public void draw (Batch batch, float parentAlpha) {
        if (level == null || level.selectedBuilding == null) return;
        batch.end();
        Vector2 coords = getStage().stageToScreenCoordinates(localToStageCoordinates(new Vector2(SIZE / 2, SIZE / 2)));
        coords.y = getStage().getViewport().getScreenHeight() - coords.y;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        if (color != null) shape.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        drawBottom(shape, coords, level.selectedBuilding, parentAlpha, SIZE);
        shape.end();
        Gdx.gl.glLineWidth(4);
        shape.setColor(.5f, .5f, .5f, parentAlpha);
        shape.begin(ShapeRenderer.ShapeType.Line);
        drawTop(shape, coords);
        shape.end();
        Gdx.gl.glLineWidth(Main.LINE_WIDTH);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    public void update(Building building) {
        if (building instanceof Generator) {
            Generator generator = ((Generator) building);
            color = generator.type == Level.Resource.RED ? Color.RED : generator.type == Level.Resource.BLUE ? Color.BLUE : Color.YELLOW;
        }
        redValue.setText("" + (int) building.redValue);
        blueValue.setText("" + (int) building.blueValue);
        yellowValue.setText("" + (int) building.yellowValue);
    }
}

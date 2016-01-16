package com.thepaperpilot.solar.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

public class Lightning extends Actor{
    public static final Lightning instance = new Lightning();

    public final ArrayList<Vector2> lightning = new ArrayList<>();
    private final ShapeRenderer shape = new ShapeRenderer();

    public void draw(Batch batch, float parentAlpha) {
        if (lightning.isEmpty()) return;
        batch.end();
        Gdx.gl.glLineWidth(2);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setProjectionMatrix(batch.getProjectionMatrix());
        shape.setColor(1, 1, .5f, parentAlpha * .25f);
        for (int i = 0; i < lightning.size(); i += 2) {
            drawLightning(lightning.get(i).x, lightning.get(i).y, lightning.get(i + 1).x, lightning.get(i + 1).y, lightning.get(i).dst(lightning.get(i + 1)) / 10f, shape);
        }
        shape.end();
        lightning.clear();
        Gdx.gl.glLineWidth(1);
        batch.begin();
    }

    private static void drawLightning(float x1, float y1, float x2, float y2, float displace, ShapeRenderer shape) {
        if (displace < 1) {       // must not be zero, and probably no less than 1
            shape.line(x1, y1, x2, y2);
        } else {
            float mid_x = (x2 + x1) / 2;
            float mid_y = (y2 + y1) / 2;
            mid_x += (MathUtils.random(2) - 1) * displace * 2;
            mid_y += (MathUtils.random(2) - 1) * displace * 2;
            drawLightning(x1, y1, mid_x, mid_y, displace / 2, shape);
            drawLightning(mid_x, mid_y, x2, y2, displace / 2, shape);
            if (MathUtils.random(5) == 0) {
                drawLightning(mid_x, mid_y, (mid_x + x2) / 2 - 20 + MathUtils.random(40), (mid_y + y2) / 2 - 20 + MathUtils.random(40), displace / 4, shape);
            }
        }
    }

    public static void add(Vector2 start, Vector2 end) {
        instance.lightning.add(start);
        instance.lightning.add(end);
    }
}

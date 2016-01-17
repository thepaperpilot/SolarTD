package com.thepaperpilot.solar.Interface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.thepaperpilot.solar.Main;

public class ConfirmDialog extends Table{

    public ConfirmDialog(String message, Stage stage) {
        super(Main.skin);
        setBackground(Main.skin.getDrawable("default-round"));
        setFillParent(true);
        setTouchable(Touchable.enabled);
        setColor(1, 1, 1, .5f);

        TextButton ok = new TextButton("Yeah, Let's go!", Main.skin);
        TextButton cancel = new TextButton("No, Let's stay here", Main.skin);

        Table dialog = new Table(Main.skin);
        dialog.setBackground(Main.skin.getDrawable("default-round"));
        dialog.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        dialog.add(new Label("Are you sure you want to " + message + "?", Main.skin, "large")).colspan(2).row();
        dialog.add(ok).expandX();
        dialog.add(cancel).expandX();
        add(dialog);

        ok.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.getSound("select").play(Main.volume);
                remove();
                event.reset();
                ok();
            }
        });
        cancel.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Main.getSound("select").play(Main.volume);
                remove();
                event.reset();
                ConfirmDialog.this.cancel();
            }
        });

        stage.addActor(this);
    }

    public void ok() {

    }

    public void cancel() {

    }
}

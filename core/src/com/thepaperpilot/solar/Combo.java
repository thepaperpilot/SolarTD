package com.thepaperpilot.solar;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thepaperpilot.solar.Entities.*;
import com.thepaperpilot.solar.Levels.Level;

public enum Combo {
    MINE(1, 0, 0, Level.Resource.RED) {
        public boolean fire(Tower tower) {
            new Mine(new Vector2(tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2), tower, tower.getDamage(), tower.getRange() / 2, tower.level).setSize(Main.TOWER_RADIUS / 2, Main.TOWER_RADIUS / 2);
            return true;
        }
    },
    CLUSTER_BOMB(0, 1, 0, Level.Resource.RED) {
        public boolean fire(final Tower tower) {
            new Bomb(Rocket.redPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower) {
                public void hit() {
                    super.hit();
                    for (int i = 0; i < 360; i += (360 / tower.getSpeed())) {
                        new Missile(Rocket.bluePool, getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, level, tower, tower.getDamage() / 4f, false).angle = i;
                    }
                }
            };
            return true;
        }
    },
    SPARKY_CLUSTER_BOMB(0, 1, 1, Level.Resource.RED) {
        public boolean fire(final Tower tower) {
            new Bomb(Rocket.redPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower) {
                public void hit() {
                    super.hit();
                    for (int i = 0; i < level.enemies.size(); i++) {
                        Enemy enemy = level.enemies.get(i);
                        if (MathUtils.random() < tower.getSpeed() / 25f) continue;
                        Lightning.add(new Vector2(getX(), getY()), new Vector2(enemy.getX(), enemy.getY()));
                        enemy.hit(tower.getDamage());
                    }
                }
            };
            return true;
        }
    },
    BIG_MINE(1, 1, 0, Level.Resource.RED) {
        public boolean fire(Tower tower) {
            new Mine(new Vector2(tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2), tower, tower.getDamage() / 2, tower.getRange(), tower.level);
            return true;
        }
    },
    SPARKY_MINE(1, 0, 1, Level.Resource.RED) {
        public boolean fire(Tower tower) {
            Mine mine = new Mine(new Vector2(tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2), tower, tower.getDamage() / 2, tower.getRange(), tower.level) {
                public void hit() {
                    for (int i = 0; i < level.enemies.size(); i++) {
                        Enemy enemy = level.enemies.get(i);
                        if (MathUtils.random() < tower.getSpeed() / 25f) continue;
                        Lightning.add(new Vector2(getX(), getY()), new Vector2(enemy.getX(), enemy.getY()));
                        enemy.hit(tower.getDamage());
                    }
                }
            };
            mine.setColor(1, 1, 0, 1);
            mine.setSize(Main.TOWER_RADIUS / 2f, Main.TOWER_RADIUS / 2f);
            return true;
        }
    },
    BIG_CLUSTER_BOMB(0, 2, 0, Level.Resource.RED) {
        public boolean fire(final Tower tower) {
            new Bomb(Rocket.bigPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower) {
                public void hit() {
                    for (int i = 0; i < 360; i += (360 / tower.getSpeed())) {
                        new Missile(Rocket.bluePool, getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, level, tower, tower.getDamage() / 2f, false).angle = i;
                    }
                }
            };
            return true;
        }
    },
    BIG_CLUSTER_BOMB_MINE(1, 2, 0, Level.Resource.RED) {
        public boolean fire(Tower tower) {
            Mine mine = new Mine(new Vector2(tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2), tower, tower.getDamage(), tower.getRange(), tower.level) {
                public void hit() {
                    for (int i = 0; i < 360; i += (360 / tower.getSpeed())) {
                        new Missile(Rocket.bluePool, getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, level, tower, tower.getDamage() / 2f, false).angle = i;
                    }
                }
            };
            mine.setColor(0, 0, 1, 1);
            mine.setSize(Main.TOWER_RADIUS, Main.TOWER_RADIUS);
            return true;
        }
    },
    TAZER_ROCKETS(0, 0, 1, Level.Resource.BLUE) {
        public boolean fire(final Tower tower) {
            new Missile(Rocket.yellowPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower.level, tower, tower.getDamage(), true) {
                float time;

                public void act(float delta) {
                    super.act(delta);
                    time += delta;
                    if (time > 2 / tower.getSpeed() && !level.enemies.isEmpty()) {
                        time = 0;
                        Enemy enemy = level.enemies.get(MathUtils.random(level.enemies.size() - 1));
                        Lightning.add(new Vector2(getX(), getY()), new Vector2(enemy.getX(), enemy.getY()));
                        enemy.hit(tower.getDamage());
                    }
                }
            };
            return true;
        }
    },
    POISON_GAS_ROCKETS(1, 0, 1, Level.Resource.BLUE) {
        public boolean fire(final Tower tower) {
            new Bomb(Rocket.greenPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower) {
                public void hit() {
                    ParticleEffect effect = Rocket.greenBoomPool.obtain();
                    effect.getEmitters().first().getLife().setHigh(tower.getRange() * 2, tower.getRange() * 3);
                    effect.setPosition(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2);
                    level.particles.add(effect);
                    Circle area = new Circle(getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, tower.getRange() / 2);
                    for (int i = 0; i < level.enemies.size(); i++) {
                        Enemy enemy = level.enemies.get(i);
                        if (area.contains(enemy.getPosition())) {
                            enemy.poison = tower.getSpeed();
                            enemy.poisonDamage = tower.getDamage() / 2f;
                        }
                    }
                }
            };
            return true;
        }
    },
    NUKE(2, 0, 0, Level.Resource.BLUE) {
        public boolean fire(final Tower tower) {
            new Missile(Rocket.bigPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower.level, tower, 0, true) {
                public void hit() {
                    ParticleEffect effect = Rocket.boomPool.obtain();
                    effect.getEmitters().first().getLife().setHigh(tower.getRange() * 4, tower.getRange() * 6);
                    effect.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
                    level.particles.add(effect);
                    Circle area = new Circle(getX() + getWidth() / 2, getY() + getHeight() / 2, tower.getRange());
                    for (int i = 0; i < level.enemies.size(); ) {
                        Enemy enemy = level.enemies.get(i);
                        if (area.contains(enemy.getPosition())) {
                            if (enemy.hit(tower.getDamage() * 4)) continue;
                        }
                        i++;
                    }
                }
            };
            return true;
        }
    },
    NAPALM_ROCKET(1, 1, 1, Level.Resource.BLUE) {
        public boolean fire(final Tower tower) {
            new Missile(Rocket.redPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower.level, tower, 0, true) {
                public void hit() {
                    bomb(level, tower, getX(), getY(), 8);
                }
            };
            return true;
        }

        public void bomb(final Level level, final Tower tower, final float x, final float y, final int bombs) {
            if (bombs < 0) return;
            ParticleEffect effect = Rocket.boomPool.obtain();
            effect.getEmitters().first().getLife().setHigh(tower.getRange() * 4, tower.getRange() * 6);
            effect.setPosition(x, y);
            level.particles.add(effect);
            Circle area = new Circle(x + tower.getRange() / 2, y + tower.getRange() / 2, tower.getRange());
            for (int i = 0; i < level.enemies.size(); ) {
                Enemy enemy = level.enemies.get(i);
                if (area.contains(enemy.getPosition())) {
                    if (enemy.hit(tower.getDamage() / 2)) continue;
                }
                i++;
            }

            level.stage.addAction(Actions.sequence(Actions.delay(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    bomb(level, tower, x - 100 + MathUtils.random(200), y - 100 + MathUtils.random(200), bombs - 1);
                }
            })));
        }
    },
    ROCKET_BFG(1, 2, 0, Level.Resource.BLUE) {
        public boolean fire(final Tower tower) {
            new Missile(Rocket.bfgPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower.level, tower, tower.getDamage(), false) {
                float time;

                public Vector2 getTarget() {
                    return null;
                }

                public void act(float delta) {
                    super.act(delta);
                    time += delta;
                    if (time > 4 / tower.getSpeed()) {
                        time = 0;
                        new Missile(Rocket.bluePool, getX() + Main.TOWER_RADIUS / 2, getY() + Main.TOWER_RADIUS / 2, level, tower, tower.getDamage() / 2f, false).angle = MathUtils.random(360);
                    }
                }
            }.speed = .4f;
            return true;
        }
    },
    BFG(1, 1, 1, Level.Resource.YELLOW) {
        public boolean fire(final Tower tower) {
            new Missile(Rocket.bfgPool, tower.getX() + Main.TOWER_RADIUS / 2, tower.getY() + Main.TOWER_RADIUS / 2, tower.level, tower, tower.getDamage(), false) {
                float time;

                public Vector2 getTarget() {
                    return null;
                }

                public void act(float delta) {
                    super.act(delta);
                    time += delta;
                    if (time > 12 / tower.getSpeed() && !level.enemies.isEmpty()) {
                        time = 0;
                        Enemy enemy = level.enemies.get(MathUtils.random(level.enemies.size() - 1));
                        Lightning.add(new Vector2(getX(), getY()), new Vector2(enemy.getX(), enemy.getY()));
                        enemy.hit(tower.getDamage() / 4f);
                    }
                }
            }.speed = .4f;
            return true;
        }
    };

    public final int red;
    public final int blue;
    public final int yellow;
    public final Level.Resource type;

    Combo(int red, int blue, int yellow, Level.Resource type) { // values do not include the tower firing
        this.red = red;
        this.blue = blue;
        this.yellow = yellow;
        this.type = type;
    }

    public Table getTable() {
        Table table = new Table(Main.skin);
        table.left().pad(4).add(new Label(name().replaceAll("_", " "), Main.skin)).left().spaceLeft(2).colspan(1 + red + blue + yellow).row();
        table.add(new Image(Main.getDrawable("towers/" + (type == Level.Resource.RED ? "redStoreDown" : type == Level.Resource.BLUE ? "blueStoreDown" : "yellowStoreDown")))).size(32);
        for (int i = 0; i < red; i++) {
            table.add(new Image(Main.getDrawable("towers/redStore"))).size(32);
            if ((table.getChildren().size - 1) % 4 == 0) table.row();
        }
        for (int i = 0; i < blue; i++) {
            table.add(new Image(Main.getDrawable("towers/blueStore"))).size(32);
            if ((table.getChildren().size - 1) % 4 == 0) table.row();
        }
        for (int i = 0; i < yellow; i++) {
            table.add(new Image(Main.getDrawable("towers/yellowStore"))).size(32);
            if ((table.getChildren().size - 1) % 4 == 0) table.row();
        }
        table.setBackground(Main.skin.getDrawable("default-rect"));
        return table;
    }

    public abstract boolean fire(Tower tower);
}

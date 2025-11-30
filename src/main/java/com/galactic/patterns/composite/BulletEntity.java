package com.galactic.patterns.composite;

import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BulletEntity extends GameEntity {
    private boolean isPlayerBullet;

    public BulletEntity(double x, double y, boolean isPlayerBullet) {
        super(x, y, 6, 20, "BULLET");
        this.isPlayerBullet = isPlayerBullet;
        this.type = isPlayerBullet ? "PLAYER_BULLET" : "ENEMY_BULLET";
    }

    @Override
    public void update(double deltaTime) {
        if (isPlayerBullet) {
            y -= 600 * deltaTime;
        } else {
            y += 300 * deltaTime;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Color c = isPlayerBullet ? Color.YELLOW : Color.RED;
        Renderer.drawLaser(gc, x, y, w, h, c);
    }
}
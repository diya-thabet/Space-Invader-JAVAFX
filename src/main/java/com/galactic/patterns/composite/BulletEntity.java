package com.galactic.patterns.composite;

import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BulletEntity extends GameEntity {
    private boolean isPlayerBullet;
    private double vx, vy;

    // Standard Constructor
    public BulletEntity(double x, double y, boolean isPlayerBullet) {
        this(x, y, 0, isPlayerBullet ? -600 : 300, isPlayerBullet);
    }

    // Vector Constructor for Spread Shots
    public BulletEntity(double x, double y, double vx, double vy, boolean isPlayerBullet) {
        super(x, y, 6, 20, "BULLET");
        this.isPlayerBullet = isPlayerBullet;
        this.vx = vx;
        this.vy = vy;
        this.type = isPlayerBullet ? "PLAYER_BULLET" : "ENEMY_BULLET";
    }

    @Override
    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;

        // Cleanup if off-screen
        if (y < -50 || y > 900 || x < -50 || x > 850) {
            setAlive(false);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Color c = isPlayerBullet ? Color.YELLOW : Color.RED;
        Renderer.drawLaser(gc, x, y, w, h, c);
    }
}
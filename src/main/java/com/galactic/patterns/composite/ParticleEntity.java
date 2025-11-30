package com.galactic.patterns.composite;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class ParticleEntity extends GameEntity {
    private double life = 1.0;
    private double vx, vy;

    public ParticleEntity(double x, double y) {
        super(x, y, 5, 5, "PARTICLE");
        // Explosion direction aléatoire
        double angle = Math.random() * Math.PI * 2;
        double speed = Math.random() * 100 + 50;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }

    @Override
    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        life -= 2.0 * deltaTime; // Disparait vite

        if (life <= 0) setAlive(false);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(Color.ORANGE.deriveColor(0, 1, 1, life)); // Transparence
        gc.fillOval(x, y, w * life, h * life);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
    }
}
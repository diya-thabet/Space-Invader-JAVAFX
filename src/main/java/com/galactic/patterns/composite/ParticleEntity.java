package com.galactic.patterns.composite;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class ParticleEntity extends GameEntity {
    private double life = 1.0;
    private double vx, vy;
    private Color color;

    public ParticleEntity(double x, double y, Color color) {
        super(x, y, 6, 6, "PARTICLE");
        this.color = color;

        double angle = Math.random() * Math.PI * 2;
        double speed = Math.random() * 150 + 50;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }

    @Override
    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;

        // Drag effect
        vx *= 0.95;
        vy *= 0.95;

        life -= 1.5 * deltaTime; // Fade out

        if (life <= 0) setAlive(false);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(color.deriveColor(0, 1, 1, life));
        double size = w * life;
        gc.fillOval(x, y, size, size);
        gc.restore();
    }
}
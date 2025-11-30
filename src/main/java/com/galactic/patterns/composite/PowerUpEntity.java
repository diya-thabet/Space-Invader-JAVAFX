package com.galactic.patterns.composite;

import com.galactic.patterns.composite.GameEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// --- POWERUP ---
public class PowerUpEntity extends GameEntity {
    private double time = 0;
    public PowerUpEntity(double x, double y) {
        super(x, y, 20, 20, "POWERUP");
    }

    @Override
    public void update(double deltaTime) {
        time += deltaTime;
        y += 100 * deltaTime;
        // Effet de flottement
        x += Math.sin(time * 5) * 0.5;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillOval(x, y, w, h);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, w, h);
    }
}
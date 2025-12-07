package com.galactic.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Renderer {

    // Shared effect instance
    private static final DropShadow NEON_GLOW = new DropShadow();

    static {
        NEON_GLOW.setRadius(20);
        NEON_GLOW.setSpread(0.4);
        NEON_GLOW.setColor(Color.CYAN);
    }

    // --- COMPATIBILITY: Draw Background ---
    public static void drawBackground(GraphicsContext gc, double w, double h) {
        drawRetroGrid(gc, w, h, System.currentTimeMillis() / 1000.0, 50);
    }

    // --- RETRO GRID ENGINE ---
    public static void drawRetroGrid(GraphicsContext gc, double w, double h, double time, double speed) {
        gc.save();

        // 1. Deep Space Background (Gradient)
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#050011")),
                new Stop(1, Color.web("#2a0044")));
        gc.setFill(bgGradient);
        gc.fillRect(0, 0, w, h);

        // 2. Horizon Glow (Transparent Pink)
        LinearGradient horizonGlow = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.web("#ff00ff", 0.3)));

        gc.setFill(horizonGlow);
        gc.fillRect(0, h * 0.6, w, h * 0.4);

        // 3. Grid Lines
        gc.setStroke(Color.web("#d100d1", 0.25));
        gc.setLineWidth(2);

        double gridSize = 50;
        double offset = (time * speed) % gridSize;

        // Vertical lines
        for (double x = 0; x < w; x += gridSize) {
            gc.strokeLine(x, 0, x, h);
        }

        // Horizontal moving lines
        for (double y = offset; y < h; y += gridSize) {
            // Fade out lines near top
            double opacity = Math.min(1.0, (y / h) + 0.1);
            gc.setStroke(Color.web("#d100d1", opacity * 0.4));
            gc.strokeLine(0, y, w, y);
        }

        gc.restore();
    }

    // --- NEON DRAWING HELPERS ---

    public static void drawNeonPlayer(GraphicsContext gc, double x, double y, double w, double h) {
        gc.save();
        NEON_GLOW.setColor(Color.CYAN);
        gc.setEffect(NEON_GLOW);

        // Main Body
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);
        gc.strokePolygon(
                new double[]{x + w/2, x, x + w},
                new double[]{y, y + h, y + h},
                3
        );

        // Engine Thruster
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(Color.CYAN.deriveColor(0, 1, 1, 0.5));
        gc.fillPolygon(
                new double[]{x + w/2, x + 10, x + w - 10},
                new double[]{y, y + h, y + h},
                3
        );

        gc.restore();
    }

    public static void drawNeonEnemy(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        gc.save();
        NEON_GLOW.setColor(color);
        gc.setEffect(NEON_GLOW);

        gc.setStroke(color);
        gc.setLineWidth(2);

        // Invader Shape
        gc.strokePolygon(
                new double[]{x, x + w * 0.2, x + w * 0.5, x + w * 0.8, x + w, x + w * 0.5},
                new double[]{y + h * 0.2, y, y + h * 0.4, y, y + h * 0.2, y + h},
                6
        );

        // Eyes
        gc.setFill(Color.WHITE);
        gc.fillOval(x + w * 0.3, y + h * 0.4, 4, 4);
        gc.fillOval(x + w * 0.7, y + h * 0.4, 4, 4);

        gc.restore();
    }

    public static void drawLaser(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);

        // Core
        gc.setFill(Color.WHITE);
        gc.fillOval(x + w*0.25, y, w*0.5, h);

        // Glow
        NEON_GLOW.setColor(color);
        gc.setEffect(NEON_GLOW);
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, w, h);

        gc.restore();
    }
}
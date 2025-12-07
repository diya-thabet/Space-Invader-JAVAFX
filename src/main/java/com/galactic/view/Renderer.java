package com.galactic.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Renderer {

    // --- COMPATIBILITY ---
    public static void drawBackground(GraphicsContext gc, double w, double h) {
        drawRetroGrid(gc, w, h, System.currentTimeMillis() / 1000.0, 50);
    }

    public static void drawRetroGrid(GraphicsContext gc, double w, double h, double time, double speed) {
        gc.save();
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#050011")), new Stop(1, Color.web("#2a0044")));
        gc.setFill(bgGradient);
        gc.fillRect(0, 0, w, h);

        LinearGradient horizonGlow = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), new Stop(1, Color.web("#ff00ff", 0.3)));
        gc.setFill(horizonGlow);
        gc.fillRect(0, h * 0.6, w, h * 0.4);

        gc.setStroke(Color.web("#d100d1", 0.25));
        gc.setLineWidth(2);

        double gridSize = 50;
        double offset = (time * speed) % gridSize;

        for (double x = 0; x < w; x += gridSize) gc.strokeLine(x, 0, x, h);
        for (double y = offset; y < h; y += gridSize) {
            double opacity = Math.min(1.0, (y / h) + 0.1);
            gc.setStroke(Color.web("#d100d1", opacity * 0.4));
            gc.strokeLine(0, y, w, y);
        }
        gc.restore();
    }

    // --- THE PERFECT NEON SHIP (Friend's Design + Neon Glow) ---
    public static void drawNeonPlayer(GraphicsContext gc, double x, double y, double w, double h, boolean shielded) {
        gc.save();

        // 1. Shield (Manual Glow to avoid Squares)
        if (shielded) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.setGlobalAlpha(0.6 + Math.sin(System.currentTimeMillis() / 100.0) * 0.2);
            gc.strokeOval(x - 10, y - 10, w + 20, h + 20);
            gc.setGlobalAlpha(1.0);
        }

        // 2. Wings (Dark Grey/Blue with Neon Edge)
        gc.setFill(Color.rgb(30, 30, 60));
        gc.fillPolygon(
                new double[]{x, x + w, x + w / 2},
                new double[]{y + h - 10, y + h - 10, y},
                3
        );
        // Neon Wing Edge
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokePolygon(
                new double[]{x, x + w, x + w / 2},
                new double[]{y + h - 10, y + h - 10, y},
                3
        );

        // 3. Cannons (Red Tips)
        gc.setFill(Color.RED);
        gc.fillRect(x, y + h - 20, 5, 20);
        gc.fillRect(x + w - 5, y + h - 20, 5, 20);

        // 4. Cockpit (Blue Glow)
        gc.setFill(Color.WHITE);
        gc.fillOval(x + w/2 - 5, y + h/2, 10, 15);
        // Manual Glow for cockpit
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeOval(x + w/2 - 5, y + h/2, 10, 15);

        // 5. Engine Flame (Flickering)
        double flicker = Math.random() * 10 + 5;
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(Color.ORANGE);
        gc.fillPolygon(
                new double[]{x + w/2 - 6, x + w/2 + 6, x + w/2},
                new double[]{y + h, y + h, y + h + flicker},
                3
        );

        gc.restore();
    }

    public static void drawRedAlien(GraphicsContext gc, double x, double y, double w, double h) {
        gc.save();
        // Manual Glow Strategy (Layering) -> NO SQUARES
        gc.setStroke(Color.RED);

        // Outer faint glow
        gc.setGlobalAlpha(0.3);
        gc.setLineWidth(6);
        gc.strokeOval(x, y, w, h - 10);

        // Inner bright line
        gc.setGlobalAlpha(1.0);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, w, h - 10);

        // Legs
        gc.strokeLine(x + 5, y + h - 10, x, y + h);
        gc.strokeLine(x + w - 5, y + h - 10, x + w, y + h);

        // Eyes
        gc.setFill(Color.YELLOW);
        gc.fillOval(x + 10, y + 10, 5, 5);
        gc.fillOval(x + w - 15, y + 10, 5, 5);

        gc.restore();
    }

    public static void drawGreenAlien(GraphicsContext gc, double x, double y, double w, double h) {
        gc.save();

        // Manual Glow
        gc.setStroke(Color.LIME);
        gc.setGlobalAlpha(0.3);
        gc.setLineWidth(6);
        gc.strokePolygon(new double[]{x, x + w, x + w / 2}, new double[]{y, y, y + h}, 3);

        gc.setGlobalAlpha(1.0);
        gc.setLineWidth(2);
        gc.strokePolygon(new double[]{x, x + w, x + w / 2}, new double[]{y, y, y + h}, 3);

        gc.setFill(Color.RED);
        gc.fillOval(x + w/2 - 4, y + 10, 8, 8);

        gc.restore();
    }

    public static void drawLaser(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);

        // Core
        gc.setFill(Color.WHITE);
        gc.fillOval(x + w*0.25, y, w*0.5, h);

        // Manual Glow (No DropShadow = No Squares)
        gc.setStroke(color);
        gc.setGlobalAlpha(0.4); // Faint wide glow
        gc.setLineWidth(6);
        gc.strokeOval(x, y, w, h);

        gc.setGlobalAlpha(1.0); // Sharp bright edge
        gc.setLineWidth(2);
        gc.strokeOval(x, y, w, h);

        gc.restore();
    }
}
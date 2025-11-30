package com.galactic.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Classe utilitaire pour dessiner des formes "Néon" stylées.
 */
public class Renderer {

    private static final DropShadow NEON_GLOW = new DropShadow();

    static {
        NEON_GLOW.setRadius(15);
        NEON_GLOW.setSpread(0.5);
    }

    public static void drawNeonPlayer(GraphicsContext gc, double x, double y, double w, double h) {
        gc.save();
        NEON_GLOW.setColor(Color.CYAN);
        gc.setEffect(NEON_GLOW);

        // Corps du vaisseau
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokePolygon(
                new double[]{x + w/2, x, x + w},
                new double[]{y, y + h, y + h},
                3
        );

        // Cockpit
        gc.setFill(Color.WHITE);
        gc.fillOval(x + w/2 - 3, y + h/2, 6, 6);

        gc.restore();
    }

    public static void drawNeonEnemy(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        gc.save();
        NEON_GLOW.setColor(color);
        gc.setEffect(NEON_GLOW);

        gc.setStroke(color);
        gc.setLineWidth(2);

        // Forme "Alien"
        gc.strokePolygon(
                new double[]{x, x + w/4, x + w/2, x + 3*w/4, x + w, x + w/2},
                new double[]{y, y + h/2, y + h, y + h/2, y, y + h/3},
                6
        );

        // Yeux
        gc.setFill(Color.BLACK);
        gc.fillOval(x + w/3, y + h/3, 4, 4);
        gc.fillOval(x + 2*w/3, y + h/3, 4, 4);

        gc.restore();
    }

    public static void drawLaser(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        gc.save();
        gc.setGlobalBlendMode(BlendMode.ADD);
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE), new Stop(1, color));
        gc.setFill(gradient);
        gc.fillOval(x, y, w, h);
        gc.restore();
    }

    public static void drawBackground(GraphicsContext gc, double width, double height) {
        // Fond spatial sombre dégradé
        LinearGradient bg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#050510")), new Stop(1, Color.web("#101025")));
        gc.setFill(bg);
        gc.fillRect(0, 0, width, height);
    }
}
package com.galactic.view;

import com.galactic.core.GameEngine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class StarField {
    private double[] starsX;
    private double[] starsY;
    private double[] speeds;
    private double[] sizes;
    private int count = 200; // Increased for HD
    private Random rand = new Random();

    public StarField() {
        starsX = new double[count];
        starsY = new double[count];
        speeds = new double[count];
        sizes = new double[count];

        double w = GameEngine.getInstance().getWidth();
        double h = GameEngine.getInstance().getHeight();

        for (int i = 0; i < count; i++) {
            starsX[i] = rand.nextDouble() * w;
            starsY[i] = rand.nextDouble() * h;
            // Parallax: Faster stars are bigger and brighter
            double depth = rand.nextDouble();
            speeds[i] = 0.5 + depth * 3.0;
            sizes[i] = 0.5 + depth * 2.0;
        }
    }

    public void update() {
        double w = GameEngine.getInstance().getWidth();
        double h = GameEngine.getInstance().getHeight();

        for (int i = 0; i < count; i++) {
            starsY[i] += speeds[i];
            if (starsY[i] > h) {
                starsY[i] = 0;
                starsX[i] = rand.nextDouble() * w;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        // Deep space background
        gc.setFill(Color.rgb(5, 5, 15));
        gc.fillRect(0, 0, GameEngine.getInstance().getWidth(), GameEngine.getInstance().getHeight());

        gc.setFill(Color.WHITE);
        for (int i = 0; i < count; i++) {
            gc.setGlobalAlpha(Math.min(1.0, speeds[i] / 3.0)); // Fainter if slower
            gc.fillOval(starsX[i], starsY[i], sizes[i], sizes[i]);
        }
        gc.setGlobalAlpha(1.0);
    }
}
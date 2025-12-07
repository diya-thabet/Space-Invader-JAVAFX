package com.galactic;

import com.galactic.core.GameEngine;
import com.galactic.utils.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameApp extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            Logger.getInstance().log("INFO", "Application Starting...");

            StackPane root = new StackPane();
            Canvas canvas = new Canvas(WIDTH, HEIGHT);
            root.getChildren().add(canvas);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Initialisation du Singleton Moteur
            GameEngine engine = GameEngine.getInstance();
            engine.init(gc, WIDTH, HEIGHT);

            // --- CORRECTION: Handling both PRESS and RELEASE events ---
            scene.setOnKeyPressed(event -> engine.handleInput(event));
            scene.setOnKeyReleased(event -> engine.handleInput(event));
            // ---------------------------------------------------------

            primaryStage.setTitle("Galactic Defenders - Design Patterns Project");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Boucle de jeu (Game Loop)
            new AnimationTimer() {
                long lastTime = System.nanoTime();

                @Override
                public void handle(long now) {
                    double deltaTime = (now - lastTime) / 1e9; // Convertir en secondes
                    lastTime = now;

                    engine.update(deltaTime);
                    engine.render();
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().log("ERROR", "Fatal Error: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        Logger.getInstance().log("INFO", "Application Stopping...");
        Logger.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
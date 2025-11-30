package com.galactic.core;

import com.galactic.patterns.state.GameState;
import com.galactic.patterns.state.MenuState;
import com.galactic.utils.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

/**
 * Moteur principal du jeu.
 * Utilise le STATE PATTERN pour déléguer la logique selon l'état actuel.
 */
public class GameEngine {
    private static GameEngine instance;
    private GameState currentState;

    // Contexte graphique partagé
    private GraphicsContext gc;
    private double width;
    private double height;

    private GameEngine() {}

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    public void init(GraphicsContext gc, double width, double height) {
        this.gc = gc;
        this.width = width;
        this.height = height;
        Logger.getInstance().log("INFO", "Game Engine Initialized");

        // État initial
        setState(new MenuState());
    }

    public void setState(GameState state) {
        if (currentState != null) {
            currentState.onExit();
        }
        String oldStateName = (currentState == null) ? "NULL" : currentState.getClass().getSimpleName();
        currentState = state;
        currentState.onEnter(this);

        Logger.getInstance().log("STATE", "Game: " + oldStateName + " -> " + currentState.getClass().getSimpleName());
    }

    public void handleInput(KeyEvent event) {
        if (currentState != null) {
            currentState.handleInput(this, event);
        }
    }

    public void update(double deltaTime) {
        if (currentState != null) {
            currentState.update(this, deltaTime);
        }
    }

    public void render() {
        if (currentState != null && gc != null) {
            // Nettoyage de l'écran
            gc.clearRect(0, 0, width, height);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(0, 0, width, height);

            currentState.render(this, gc);
        }
    }

    // Getters
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
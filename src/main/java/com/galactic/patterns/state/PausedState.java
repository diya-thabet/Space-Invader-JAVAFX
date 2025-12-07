package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class PausedState implements GameState {

    private GameState returnState;
    private double time = 0;

    public PausedState(GameState stateToKeep) {
        this.returnState = stateToKeep;
    }

    @Override
    public void onEnter(GameEngine context) {
        // Reset timer when entering pause
        time = 0;
    }

    @Override
    public void update(GameEngine context, double deltaTime) {
        time += deltaTime;
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        // 1. Draw the frozen game behind the menu
        returnState.render(context, gc);

        // 2. Dark Overlay (Darker for readability)
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(0, 0, context.getWidth(), context.getHeight());

        // 3. Neon Text
        gc.setTextAlign(TextAlignment.CENTER);

        // "PAUSED" Title
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Impact", 80));
        gc.fillText("PAUSED", context.getWidth()/2, context.getHeight()/2 - 50);

        // Instructions
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 20));
        gc.fillText("PRESS [P] TO RESUME", context.getWidth()/2, context.getHeight()/2 + 20);
        gc.fillText("PRESS [M] FOR MENU", context.getWidth()/2, context.getHeight()/2 + 60);
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        // --- FIX: INPUT DELAY (DEBOUNCE) ---
        // Ignore inputs for the first 0.3 seconds to prevent accidental double-taps
        if (time < 0.3) return;

        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.P) {
                context.setState(returnState);
            } else if (event.getCode() == KeyCode.M) {
                context.setState(new MenuState());
            }
        }
    }

    @Override
    public void onExit() {}
}
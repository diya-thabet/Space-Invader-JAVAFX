package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class MenuState implements GameState {
    private double time = 0;

    @Override
    public void onEnter(GameEngine context) {}

    @Override
    public void update(GameEngine context, double deltaTime) {
        time += deltaTime;
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        Renderer.drawBackground(gc, context.getWidth(), context.getHeight());

        gc.setTextAlign(TextAlignment.CENTER);

        // Titre Néon Pulsant
        double glow = Math.abs(Math.sin(time * 2));
        gc.setFill(Color.CYAN);
        gc.setEffect(new javafx.scene.effect.DropShadow(20 * glow, Color.CYAN));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gc.fillText("NEON DEFENDERS", context.getWidth() / 2, 200);
        gc.setEffect(null);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 20));

        if (time % 1.0 < 0.5) {
            gc.fillText("- PRESS ENTER TO START -", context.getWidth() / 2, 400);
        }
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            context.setState(new PlayingState());
        }
    }

    @Override
    public void onExit() {}
}
package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.view.Renderer; // <--- Critical Import
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameOverState implements GameState {
    private int score;
    private double time = 0;

    public GameOverState(int score) {
        this.score = score;
    }

    @Override
    public void onEnter(GameEngine context) {}

    @Override
    public void update(GameEngine context, double deltaTime) {
        time += deltaTime;
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        // Use the new Retro Grid to match the rest of the game
        Renderer.drawRetroGrid(gc, context.getWidth(), context.getHeight(), time, 20);

        gc.setTextAlign(TextAlignment.CENTER);

        // Neon Game Over Text with Glow
        gc.save();
        DropShadow redGlow = new DropShadow();
        redGlow.setColor(Color.RED);
        redGlow.setRadius(20);
        gc.setEffect(redGlow);

        gc.setFill(Color.RED);
        gc.setFont(new Font("Impact", 80));
        gc.fillText("GAME OVER", context.getWidth() / 2, 250);
        gc.restore();

        // Score Display
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 40));
        gc.fillText("FINAL SCORE: " + score, context.getWidth() / 2, 350);

        // Retry Prompt (Blinking)
        if (time % 1.0 < 0.5) {
            gc.setFont(new Font("Consolas", 20));
            gc.fillText("- PRESS ENTER TO RETRY -", context.getWidth() / 2, 450);
        }
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            // Restart the game
            context.setState(new PlayingState());
        }
    }

    @Override
    public void onExit() {}
}
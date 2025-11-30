package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameOverState implements GameState {
    private int score;

    public GameOverState(int score) {
        this.score = score;
    }

    @Override
    public void onEnter(GameEngine context) {}

    @Override
    public void update(GameEngine context, double deltaTime) {}

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        Renderer.drawBackground(gc, context.getWidth(), context.getHeight());

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 60));
        gc.fillText("GAME OVER", context.getWidth() / 2, 250);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 30));
        gc.fillText("Final Score: " + score, context.getWidth() / 2, 320);

        gc.setFont(new Font("Arial", 20));
        gc.fillText("Press ENTER to Restart", context.getWidth() / 2, 450);
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            context.setState(new MenuState());
        }
    }

    @Override
    public void onExit() {}
}
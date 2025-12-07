package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
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
        // Safe call to Renderer
        Renderer.drawRetroGrid(gc, context.getWidth(), context.getHeight(), time, 50);

        gc.setTextAlign(TextAlignment.CENTER);

        // Neon Title Pulse
        double glow = Math.abs(Math.sin(time * 3));

        gc.save();
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.CYAN);
        titleGlow.setRadius(30 * glow);
        gc.setEffect(titleGlow);

        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Impact", 70));
        gc.fillText("NEON DEFENDERS", context.getWidth() / 2, 200);
        gc.restore();

        // Subtitle
        gc.setFill(Color.MAGENTA);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        gc.fillText("ULTIMATE EDITION", context.getWidth() / 2, 260);

        // Blink Text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 20));

        if (time % 1.0 < 0.5) {
            gc.fillText("- PRESS ENTER TO START -", context.getWidth() / 2, 450);
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
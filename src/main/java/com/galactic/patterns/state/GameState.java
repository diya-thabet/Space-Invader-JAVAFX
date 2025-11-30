package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.patterns.composite.EntityGroup;
import com.galactic.patterns.composite.GameEntity;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

// --- INTERFACE ---
public interface GameState {
    void onEnter(GameEngine context);
    void update(GameEngine context, double deltaTime);
    void render(GameEngine context, GraphicsContext gc);
    void handleInput(GameEngine context, KeyEvent event);
    void onExit();
}

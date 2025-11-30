package com.galactic.patterns.composite;

import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

// --- COMPOSITE ---
public class EntityGroup extends GameEntity {
    private List<GameEntity> children = new ArrayList<>();

    public EntityGroup() {
        super(0, 0, 0, 0, "GROUP");
    }

    @Override
    public void add(GameEntity e) { children.add(e); }

    @Override
    public void remove(GameEntity e) { children.remove(e); }

    @Override
    public List<GameEntity> getChildren() { return children; }

    @Override
    public void update(double deltaTime) {
        List<GameEntity> snapshot = new ArrayList<>(children);
        for (GameEntity child : snapshot) {
            child.update(deltaTime);
        }
        children.removeIf(e -> !e.isAlive());
    }

    @Override
    public void render(GraphicsContext gc) {
        for (GameEntity child : children) {
            child.render(gc);
        }
    }
}

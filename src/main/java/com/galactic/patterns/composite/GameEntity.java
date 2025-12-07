package com.galactic.patterns.composite;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public abstract class GameEntity {
    protected double x, y, w, h;
    protected boolean isAlive = true;
    protected String type;
    protected int health = 1;

    public GameEntity(double x, double y, double w, double h, String type) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.type = type;
    }

    public abstract void update(double deltaTime);
    public abstract void render(GraphicsContext gc);

    // Composite Methods
    public void add(GameEntity e) { throw new UnsupportedOperationException(); }
    public void remove(GameEntity e) { throw new UnsupportedOperationException(); }
    public List<GameEntity> getChildren() { return null; }

    // Damage Logic
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.isAlive = false;
        }
    }

    public BoundingBox getBounds() { return new BoundingBox(x, y, w, h); }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getType() { return type; }

    // FIX: Added Getter for Health so PlayingState can display it
    public int getHealth() { return health; }
}
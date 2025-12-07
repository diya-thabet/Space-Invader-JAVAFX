package com.galactic.patterns.composite;

import com.galactic.patterns.factory.EntityFactory;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyEntity extends GameEntity {
    public enum Behavior { SIMPLE, SINE_WAVE, KAMIKAZE }

    private double timeAlive = 0;
    private double startX;
    private EntityGroup worldRef;
    private Behavior behavior;
    private Color color;
    private PlayerEntity playerTarget; // For Kamikaze

    public EnemyEntity(double x, double y, EntityGroup worldRef, Behavior behavior) {
        super(x, y, 40, 40, "ENEMY");
        this.startX = x;
        this.worldRef = worldRef;
        this.behavior = behavior;
        this.health = 2;

        switch (behavior) {
            case SIMPLE -> this.color = Color.MAGENTA;
            case SINE_WAVE -> this.color = Color.CYAN;
            case KAMIKAZE -> { this.color = Color.ORANGE; this.health = 1; }
        }

        // Find player for tracking (Hacky but works for simple game)
        for(GameEntity e : worldRef.getChildren()) {
            if(e instanceof PlayerEntity) {
                this.playerTarget = (PlayerEntity)e;
                break;
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        timeAlive += deltaTime;

        switch (behavior) {
            case SIMPLE:
                y += 80 * deltaTime;
                break;

            case SINE_WAVE:
                y += 60 * deltaTime;
                x = startX + Math.sin(timeAlive * 3) * 80;
                break;

            case KAMIKAZE:
                y += 150 * deltaTime; // Fast
                if (playerTarget != null && playerTarget.isAlive()) {
                    if (x < playerTarget.getX()) x += 100 * deltaTime;
                    if (x > playerTarget.getX()) x -= 100 * deltaTime;
                }
                break;
        }

        // Shooting logic (Kamikaze doesn't shoot, others do)
        if (behavior != Behavior.KAMIKAZE && Math.random() < 0.005) {
            GameEntity bullet = EntityFactory.createEnemyBullet(x + w/2, y + h);
            worldRef.add(bullet);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Renderer.drawNeonEnemy(gc, x, y, w, h, color);
    }
}
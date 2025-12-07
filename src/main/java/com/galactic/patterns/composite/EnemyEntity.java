package com.galactic.patterns.composite;

import com.galactic.patterns.factory.EntityFactory;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;

public class EnemyEntity extends GameEntity {
    // Defines the Types for the Factory
    public enum Type { RED, GREEN }

    private Type enemyType;
    private double timeAlive = 0;
    private EntityGroup worldRef;

    public EnemyEntity(double x, double y, EntityGroup worldRef, Type type) {
        super(x, y, 40, 40, "ENEMY");
        this.worldRef = worldRef;
        this.enemyType = type;
        this.health = (type == Type.RED) ? 2 : 1; // Red is stronger
    }

    @Override
    public void update(double deltaTime) {
        timeAlive += deltaTime;

        // Shooting Logic
        double shootChance = (enemyType == Type.RED) ? 0.002 : 0.0005;
        if (Math.random() < shootChance) {
            GameEntity bullet = EntityFactory.createEnemyBullet(x + w/2, y + h);
            worldRef.add(bullet);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (enemyType == Type.RED) {
            Renderer.drawRedAlien(gc, x, y, w, h);
        } else {
            Renderer.drawGreenAlien(gc, x, y, w, h);
        }
    }
}
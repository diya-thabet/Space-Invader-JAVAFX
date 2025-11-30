package com.galactic.patterns.composite;

import com.galactic.patterns.factory.EntityFactory;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyEntity extends GameEntity {
    private double timeAlive = 0;
    private double startX;
    private EntityGroup worldRef; // Référence pour tirer
    private double health;

    public EnemyEntity(double x, double y, EntityGroup worldRef) {
        super(x, y, 40, 40, "ENEMY");
        this.startX = x;
        this.worldRef = worldRef;
        this.health = 2; // Il faut 2 coups pour le tuer
    }

    @Override
    public void update(double deltaTime) {
        timeAlive += deltaTime;

        // Mouvement en Sine Wave (ZigZag)
        y += 60 * deltaTime;
        x = startX + Math.sin(timeAlive * 3) * 50;

        // Tir aléatoire
        if (Math.random() < 0.005) { // 0.5% chance par frame
            GameEntity bullet = EntityFactory.createEnemyBullet(x + w/2, y + h);
            worldRef.add(bullet);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Renderer.drawNeonEnemy(gc, x, y, w, h, Color.MAGENTA);
    }
}
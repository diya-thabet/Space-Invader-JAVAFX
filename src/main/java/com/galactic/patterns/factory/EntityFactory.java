package com.galactic.patterns.factory;

import com.galactic.patterns.composite.*;
import javafx.scene.paint.Color;

public class EntityFactory {

    public static GameEntity createEnemy(double x, double y, EntityGroup world) {
        // Randomly choose behavior
        double r = Math.random();
        EnemyEntity.Behavior behavior;
        if (r < 0.5) behavior = EnemyEntity.Behavior.SIMPLE;
        else if (r < 0.8) behavior = EnemyEntity.Behavior.SINE_WAVE;
        else behavior = EnemyEntity.Behavior.KAMIKAZE;

        return new EnemyEntity(x, y, world, behavior);
    }

    public static GameEntity createPlayer(double x, double y) {
        return new PlayerEntity(x, y);
    }

    public static GameEntity createBullet(double x, double y) {
        return new BulletEntity(x, y, true);
    }

    // New: For Spread Shot
    public static GameEntity createBulletVector(double x, double y, double vx, double vy) {
        return new BulletEntity(x, y, vx, vy, true);
    }

    public static GameEntity createEnemyBullet(double x, double y) {
        return new BulletEntity(x, y, false);
    }

    public static GameEntity createPowerUp(double x, double y) {
        return new PowerUpEntity(x, y);
    }

    public static GameEntity createParticle(double x, double y) {
        return new ParticleEntity(x, y, Color.ORANGE);
    }

    public static GameEntity createBlueParticle(double x, double y) {
        return new ParticleEntity(x, y, Color.CYAN);
    }
}
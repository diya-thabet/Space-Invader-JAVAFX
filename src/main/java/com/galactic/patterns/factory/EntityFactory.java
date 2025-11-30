package com.galactic.patterns.factory;

import com.galactic.patterns.composite.*;
import com.galactic.utils.Logger;

public class EntityFactory {

    // Besoin de la référence au monde pour les ennemis qui tirent
    public static GameEntity createEnemy(double x, double y, EntityGroup world) {
        return new EnemyEntity(x, y, world);
    }

    public static GameEntity createPlayer(double x, double y) {
        return new PlayerEntity(x, y);
    }

    public static GameEntity createBullet(double x, double y) {
        return new BulletEntity(x, y, true); // true = Joueur
    }

    public static GameEntity createEnemyBullet(double x, double y) {
        return new BulletEntity(x, y, false); // false = Ennemi
    }

    public static GameEntity createPowerUp(double x, double y) {
        return new PowerUpEntity(x, y);
    }

    public static GameEntity createParticle(double x, double y) {
        return new ParticleEntity(x, y);
    }
}
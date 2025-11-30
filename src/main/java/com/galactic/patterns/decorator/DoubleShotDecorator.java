package com.galactic.patterns.decorator;

import com.galactic.patterns.composite.EntityGroup;
import com.galactic.patterns.factory.EntityFactory;

// --- CONCRETE DECORATOR ---
public class DoubleShotDecorator extends WeaponDecorator {
    public DoubleShotDecorator(Weapon decoratedWeapon) {
        super(decoratedWeapon);
    }

    @Override
    public void shoot(double x, double y, EntityGroup world) {
        // Le tir original
        super.shoot(x, y, world);
        // + Deux tirs sur les cotés
        world.add(EntityFactory.createBullet(x - 15, y));
        world.add(EntityFactory.createBullet(x + 10, y));

        // On ne log pas à chaque tir pour éviter le spam, mais l'application du décorateur est loggée dans PlayerEntity
    }
}

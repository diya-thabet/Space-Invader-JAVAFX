package com.galactic.patterns.decorator;

import com.galactic.patterns.composite.EntityGroup;
import com.galactic.patterns.factory.EntityFactory;

// --- CONCRETE COMPONENT ---
public class SimpleWeapon implements Weapon {
    @Override
    public void shoot(double x, double y, EntityGroup world) {
        // Tir simple tout droit
        world.add(EntityFactory.createBullet(x - 2.5, y - 10));
    }
}

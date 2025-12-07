package com.galactic.patterns.decorator;

import com.galactic.patterns.composite.EntityGroup;
import com.galactic.patterns.factory.EntityFactory;

public class SpreadShotDecorator extends WeaponDecorator {
    public SpreadShotDecorator(Weapon decoratedWeapon) {
        super(decoratedWeapon);
    }

    @Override
    public void shoot(double x, double y, EntityGroup world) {
        // Shoot the previous weapon's pattern (Stacks upgrades!)
        super.shoot(x, y, world);

        // Add Spread Shot (Angled bullets)
        // Left Diagonal
        world.add(EntityFactory.createBulletVector(x, y, -150, -550));
        // Right Diagonal
        world.add(EntityFactory.createBulletVector(x, y, 150, -550));
    }
}
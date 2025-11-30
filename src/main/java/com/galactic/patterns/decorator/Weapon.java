package com.galactic.patterns.decorator;

import com.galactic.patterns.composite.EntityGroup;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;

// --- COMPONENT INTERFACE ---
public interface Weapon {
    void shoot(double x, double y, EntityGroup world);
}

// --- DECORATOR ABSTRACT ---
abstract class WeaponDecorator implements Weapon {
    protected Weapon decoratedWeapon;

    public WeaponDecorator(Weapon decoratedWeapon) {
        this.decoratedWeapon = decoratedWeapon;
    }

    @Override
    public void shoot(double x, double y, EntityGroup world) {
        decoratedWeapon.shoot(x, y, world);
    }
}


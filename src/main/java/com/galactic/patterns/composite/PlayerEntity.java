package com.galactic.patterns.composite;

import com.galactic.patterns.decorator.*;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.galactic.view.Renderer;

public class PlayerEntity extends GameEntity {
    private Weapon weapon;
    private double velocityX = 0;
    private static final double SPEED = 600;
    private int upgradeLevel = 0;

    public PlayerEntity(double x, double y) {
        super(x, y, 50, 50, "PLAYER");
        this.weapon = new SimpleWeapon();
        this.health = 5; // Increased HP
    }

    public void upgradeWeapon() {
        upgradeLevel++;
        if (upgradeLevel == 1) {
            this.weapon = new DoubleShotDecorator(this.weapon);
            Logger.getInstance().log("UPGRADE", "Acquired Double Shot!");
        } else if (upgradeLevel == 2) {
            this.weapon = new SpreadShotDecorator(this.weapon); // Stacks!
            Logger.getInstance().log("UPGRADE", "Acquired Spread Shot!");
        } else {
            // Heal if max upgrade
            this.health = Math.min(this.health + 1, 5);
            Logger.getInstance().log("HEAL", "Weapon Maxed. HP Restored.");
        }
    }

    public void handleInput(KeyEvent event, EntityGroup world) {
        if (event.getCode() == KeyCode.SPACE) {
            weapon.shoot(x + w/2 - 2.5, y, world); // Center shot
        }
    }

    public void handleMovement(boolean left, boolean right) {
        velocityX = 0;
        if (left) velocityX -= SPEED;
        if (right) velocityX += SPEED;
    }

    @Override
    public void update(double deltaTime) {
        x += velocityX * deltaTime;
        if (x < 0) x = 0;
        if (x > 800 - w) x = 800 - w;
    }

    @Override
    public void render(GraphicsContext gc) {
        Renderer.drawNeonPlayer(gc, x, y, w, h);
    }
}
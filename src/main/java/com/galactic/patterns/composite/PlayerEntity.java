package com.galactic.patterns.composite;

import com.galactic.core.GameEngine;
import com.galactic.patterns.decorator.*;
import com.galactic.utils.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.galactic.view.Renderer;

public class PlayerEntity extends GameEntity {
    private Weapon weapon;
    private double velocityX = 0;
    private double velocityY = 0; // NEW
    private static final double SPEED = 600;
    private int upgradeLevel = 0;

    // Shield Logic
    private boolean isShielded = false;
    private double shieldTimer = 0;

    public PlayerEntity(double x, double y) {
        super(x, y, 50, 50, "PLAYER");
        this.weapon = new SimpleWeapon();
        this.health = 5;
    }

    public void upgradeWeapon() {
        upgradeLevel++;
        if (upgradeLevel == 1) {
            this.weapon = new DoubleShotDecorator(this.weapon);
        } else if (upgradeLevel == 2) {
            this.weapon = new SpreadShotDecorator(this.weapon);
        } else {
            activateShield(5.0);
        }
    }

    public void activateShield(double duration) {
        isShielded = true;
        shieldTimer = duration;
        Logger.getInstance().log("PLAYER", "Shield Activated!");
    }

    @Override
    public void takeDamage(int amount) {
        if (isShielded) {
            Logger.getInstance().log("PLAYER", "Shield absorbed damage!");
            return;
        }
        super.takeDamage(amount);
    }

    public void handleInput(KeyEvent event, EntityGroup world) {
        if (event.getCode() == KeyCode.SPACE) {
            weapon.shoot(x + w/2 - 2.5, y, world);
        }
        if (event.getCode() == KeyCode.B) {
            activateShield(3.0);
        }
    }

    // UPDATED: Now handles 4 directions
    public void handleMovement(boolean left, boolean right, boolean up, boolean down) {
        velocityX = 0;
        velocityY = 0;

        if (left) velocityX -= SPEED;
        if (right) velocityX += SPEED;

        if (up) velocityY -= SPEED;
        if (down) velocityY += SPEED;
    }

    @Override
    public void update(double deltaTime) {
        x += velocityX * deltaTime;
        y += velocityY * deltaTime; // Apply Vertical Movement

        if (isShielded) {
            shieldTimer -= deltaTime;
            if (shieldTimer <= 0) isShielded = false;
        }

        // Clamp to screen
        double screenWidth = GameEngine.getInstance().getWidth();
        double screenHeight = GameEngine.getInstance().getHeight(); // NEW

        if (x < 0) x = 0;
        if (x > screenWidth - w) x = screenWidth - w;

        if (y < 0) y = 0;
        if (y > screenHeight - h) y = screenHeight - h;
    }

    @Override
    public void render(GraphicsContext gc) {
        Renderer.drawNeonPlayer(gc, x, y, w, h, isShielded);
    }
}
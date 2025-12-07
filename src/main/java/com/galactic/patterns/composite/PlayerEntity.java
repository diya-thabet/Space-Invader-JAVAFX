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
    private static final double SPEED = 600;
    private int upgradeLevel = 0;

    // Shield Logic (From Friend's Code)
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
            activateShield(5.0); // Max weapon grants shield
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
        // Shield cheat for testing from friend's code
        if (event.getCode() == KeyCode.B) {
            activateShield(3.0);
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

        // Shield Timer
        if (isShielded) {
            shieldTimer -= deltaTime;
            if (shieldTimer <= 0) isShielded = false;
        }

        double screenWidth = GameEngine.getInstance().getWidth();
        if (x < 0) x = 0;
        if (x > screenWidth - w) x = screenWidth - w;
    }

    @Override
    public void render(GraphicsContext gc) {
        // Pass shield state to renderer
        Renderer.drawNeonPlayer(gc, x, y, w, h, isShielded);
    }
}
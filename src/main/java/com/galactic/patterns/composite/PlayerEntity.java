package com.galactic.patterns.composite;

import com.galactic.patterns.decorator.SimpleWeapon;
import com.galactic.patterns.decorator.Weapon;
import com.galactic.patterns.decorator.DoubleShotDecorator;
import com.galactic.view.Renderer;
import com.galactic.utils.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PlayerEntity extends GameEntity {
    private Weapon weapon;
    private double velocityX = 0;
    private static final double SPEED = 600; // Vitesse constante (pixels/seconde)

    public PlayerEntity(double x, double y) {
        super(x, y, 50, 50, "PLAYER");
        this.weapon = new SimpleWeapon();
        this.health = 3;
    }

    public void upgradeWeapon() {
        this.weapon = new DoubleShotDecorator(this.weapon);
        Logger.getInstance().log("DECORATOR", "WEAPON UPGRADED!");
    }

    // Gestion des tirs (Appui touche)
    public void handleInput(KeyEvent event, EntityGroup world) {
        if (event.getCode() == KeyCode.SPACE) {
            weapon.shoot(x + w/2, y, world);
        }
    }

    // Gestion du mouvement (État continu)
    public void handleMovement(boolean left, boolean right) {
        velocityX = 0; // Reset vitesse chaque frame
        if (left) velocityX -= SPEED;
        if (right) velocityX += SPEED;
    }

    @Override
    public void update(double deltaTime) {
        // Application directe de la vitesse
        x += velocityX * deltaTime;

        // Limites écran (Clamp)
        if (x < 0) { x = 0; }
        if (x > 800 - w) { x = 800 - w; }
    }

    @Override
    public void render(GraphicsContext gc) {
        Renderer.drawNeonPlayer(gc, x, y, w, h);
    }

    public int getHealth() { return health; }
}
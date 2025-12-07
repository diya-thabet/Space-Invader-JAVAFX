package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.patterns.composite.*;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;
import com.galactic.view.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class PlayingState implements GameState {
    private EntityGroup rootEntity;
    private PlayerEntity player;
    private double enemySpawnTimer = 0;
    private int score = 0;

    // Input Flags for smooth movement
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private List<double[]> stars = new ArrayList<>();

    @Override
    public void onEnter(GameEngine context) {
        rootEntity = new EntityGroup();
        player = (PlayerEntity) EntityFactory.createPlayer(context.getWidth() / 2, context.getHeight() - 80);
        rootEntity.add(player);

        // Generate background stars
        for(int i=0; i<100; i++) {
            stars.add(new double[]{Math.random() * context.getWidth(), Math.random() * context.getHeight(), Math.random() * 2 + 0.5});
        }
        Logger.getInstance().log("INFO", "Game Started");
    }

    @Override
    public void update(GameEngine context, double deltaTime) {
        // 1. Apply Movement based on flags
        player.handleMovement(leftPressed, rightPressed);

        // 2. Spawn Enemies
        enemySpawnTimer += deltaTime;
        if (enemySpawnTimer > 1.5) { // Spawn every 1.5 seconds
            double x = Math.random() * (context.getWidth() - 40);
            GameEntity enemy = EntityFactory.createEnemy(x, -40, rootEntity);
            rootEntity.add(enemy);
            enemySpawnTimer = 0;
        }

        // 3. Update all entities
        rootEntity.update(deltaTime);

        // 4. Collision Detection
        checkCollisions(context);
    }

    private void checkCollisions(GameEngine context) {
        List<GameEntity> children = rootEntity.getChildren();

        for (int i = 0; i < children.size(); i++) {
            GameEntity a = children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                GameEntity b = children.get(j);

                if (a.getBounds().intersects(b.getBounds())) {

                    // Case 1: Player Bullet vs Enemy
                    if (checkPair(a, b, "PLAYER_BULLET", "ENEMY")) {
                        GameEntity bullet = getType(a, b, "PLAYER_BULLET");
                        GameEntity enemy = getType(a, b, "ENEMY");

                        bullet.setAlive(false);
                        enemy.takeDamage(1);

                        if (!enemy.isAlive()) {
                            score += 100;
                            // Chance to drop PowerUp
                            if (Math.random() < 0.3) {
                                rootEntity.add(EntityFactory.createPowerUp(enemy.getX(), enemy.getY()));
                            }
                            // Explosion particles
                            for(int k=0; k<5; k++) rootEntity.add(EntityFactory.createParticle(enemy.getX() + 20, enemy.getY() + 20));
                        }
                    }
                    // Case 2: Enemy vs Player
                    else if (checkPair(a, b, "ENEMY", "PLAYER")) {
                        GameEntity enemy = getType(a, b, "ENEMY");
                        GameEntity p = getType(a, b, "PLAYER");

                        enemy.setAlive(false); // Enemy dies on crash
                        p.takeDamage(1);

                        if (!p.isAlive()) {
                            context.setState(new GameOverState(score));
                            return;
                        }
                    }
                    // Case 3: Enemy Bullet vs Player
                    else if (checkPair(a, b, "ENEMY_BULLET", "PLAYER")) {
                        GameEntity bullet = getType(a, b, "ENEMY_BULLET");
                        GameEntity p = getType(a, b, "PLAYER");

                        bullet.setAlive(false);
                        p.takeDamage(1);

                        if (!p.isAlive()) {
                            context.setState(new GameOverState(score));
                            return;
                        }
                    }
                    // Case 4: Player vs PowerUp
                    else if (checkPair(a, b, "PLAYER", "POWERUP")) {
                        GameEntity powerup = getType(a, b, "POWERUP");
                        powerup.setAlive(false);
                        player.upgradeWeapon();
                        score += 50;
                    }
                }
            }
        }
    }

    // Helper: Returns true if one entity is type1 and the other is type2 (order independent)
    private boolean checkPair(GameEntity a, GameEntity b, String type1, String type2) {
        return (a.getType().equals(type1) && b.getType().equals(type2)) ||
                (a.getType().equals(type2) && b.getType().equals(type1));
    }

    // Helper: Returns the specific entity from the pair
    private GameEntity getType(GameEntity a, GameEntity b, String type) {
        return a.getType().equals(type) ? a : b;
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        Renderer.drawBackground(gc, context.getWidth(), context.getHeight());

        // Draw Stars
        gc.setFill(Color.WHITE);
        for(double[] star : stars) gc.fillOval(star[0], star[1], star[2], star[2]);

        rootEntity.render(gc);

        // UI
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("SCORE: " + score, 20, 30);
        gc.fillText("HP: " + player.getHealth(), 20, 60);
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = true;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = true;

            // Pass shoot event (SPACE) to player immediately
            player.handleInput(event, rootEntity);

        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = false;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = false;
        }
    }

    @Override
    public void onExit() {
        Logger.getInstance().log("INFO", "Exiting Playing State. Final Score: " + score);
    }
}
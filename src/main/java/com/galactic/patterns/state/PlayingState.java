package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.patterns.composite.*;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;
import com.galactic.view.Renderer;
import com.galactic.view.StarField;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayingState implements GameState {
    private EntityGroup rootEntity;
    private SquadEntity enemySquad;
    private PlayerEntity player;
    private StarField starField;

    private int score = 0;
    private int wave = 1;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private double screenShakeTimer = 0;
    // --- FIX PAUSE BOUNCE ---
    private double pauseCooldown = 0; // Prevents spamming pause
    private Random random = new Random();

    @Override
    public void onEnter(GameEngine context) {
        rootEntity = new EntityGroup();
        starField = new StarField();

        player = (PlayerEntity) EntityFactory.createPlayer(context.getWidth() / 2, context.getHeight() - 80);
        rootEntity.add(player);

        startWave(context);
        Logger.getInstance().log("INFO", "Game Started - Stable Edition");
    }

    private void startWave(GameEngine context) {
        enemySquad = new SquadEntity();
        rootEntity.add(enemySquad);

        int rows = 3 + (wave / 2);
        int cols = 6 + (wave / 2);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = 100 + c * 60;
                // --- FIX: SPAWN LOWER ---
                // Enemies spawn at y=100+ to avoid overlapping the Health Bar/Score
                double y = 120 + r * 50;
                EnemyEntity.Type type = (r == 0) ? EnemyEntity.Type.RED : EnemyEntity.Type.GREEN;
                GameEntity enemy = new EnemyEntity(x, y, rootEntity, type);
                enemySquad.add(enemy);
            }
        }
    }

    @Override
    public void update(GameEngine context, double deltaTime) {
        starField.update();

        if (screenShakeTimer > 0) screenShakeTimer -= deltaTime;
        if (pauseCooldown > 0) pauseCooldown -= deltaTime;

        player.handleMovement(leftPressed, rightPressed);

        // Thruster particles
        if (Math.random() < 0.5) {
            rootEntity.add(EntityFactory.createBlueParticle(player.getX() + 25, player.getY() + 50));
        }

        rootEntity.update(deltaTime);
        checkCollisions(context);

        if (enemySquad.getChildren().isEmpty()) {
            wave++;
            startWave(context);
            player.activateShield(3.0);
        }
    }

    private void checkCollisions(GameEngine context) {
        List<GameEntity> allEntities = rootEntity.getChildren();
        List<GameEntity> enemies = enemySquad.getChildren();
        List<GameEntity> toAdd = new ArrayList<>();

        // 1. Check Player Bullets vs Enemies
        for (GameEntity entity : allEntities) {
            if (entity instanceof BulletEntity && entity.getType().equals("PLAYER_BULLET")) {
                for (GameEntity enemy : enemies) {
                    if (entity.getBounds().intersects(enemy.getBounds())) {
                        entity.setAlive(false);
                        enemy.takeDamage(1);
                        if (!enemy.isAlive()) {
                            score += 100;
                            screenShakeTimer = 0.05;
                            spawnExplosion(toAdd, enemy.getX(), enemy.getY(), 10);

                            if (Math.random() < 0.1) {
                                toAdd.add(EntityFactory.createPowerUp(enemy.getX(), enemy.getY()));
                            }
                        }
                    }
                }
            }
        }

        // 2. Other Collisions
        for (GameEntity a : allEntities) {
            if (!a.isAlive()) continue;

            if (a.getBounds().intersects(player.getBounds())) {
                if (a.getType().equals("ENEMY_BULLET")) {
                    a.setAlive(false);
                    player.takeDamage(1);
                    screenShakeTimer = 0.2;
                    if (!player.isAlive()) context.setState(new GameOverState(score));
                }
                else if (a.getType().equals("POWERUP")) {
                    a.setAlive(false);
                    player.upgradeWeapon();
                    score += 50;
                }
            }
        }

        for (GameEntity e : toAdd) {
            rootEntity.add(e);
        }
    }

    private void spawnExplosion(List<GameEntity> list, double x, double y, int count) {
        for(int i=0; i<count; i++) list.add(EntityFactory.createParticle(x, y));
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        gc.save();
        if (screenShakeTimer > 0) {
            gc.translate((random.nextDouble()-0.5)*10, (random.nextDouble()-0.5)*10);
        }

        starField.draw(gc);
        rootEntity.render(gc);

        // --- HUD FIXES ---
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 22));

        // Moved Score/Wave further down/right to be safe
        gc.fillText("SCORE: " + score, 70, 40);
        gc.fillText("WAVE: " + wave, 70, 70);

        // --- HEALTH BAR FIX ---
        // Semi-transparent background so you can see enemies behind it
        double hpPercent = Math.max(0, player.getHealth()) / 5.0;

        gc.setGlobalAlpha(0.6); // Transparency
        gc.setFill(Color.BLACK);
        gc.fillRect(40, 90, 204, 24); // Backing box

        gc.setGlobalAlpha(0.8);
        gc.setFill(Color.RED);
        gc.fillRect(42, 92, 200, 20); // Empty Red Bar

        gc.setGlobalAlpha(1.0); // Full opacity for health
        gc.setFill(Color.LIME);
        gc.fillRect(42, 92, hpPercent * 200, 20);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(40, 90, 204, 24);

        gc.restore();
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = true;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = true;
            player.handleInput(event, rootEntity);

            // Pause on PRESS (easier to control than release for toggle)
            if(event.getCode() == KeyCode.P && pauseCooldown <= 0) {
                context.setState(new PausedState(this));
            }

        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = false;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = false;
        }
    }

    @Override
    public void onExit() {
        // Reset flags
        leftPressed = false;
        rightPressed = false;
    }
}
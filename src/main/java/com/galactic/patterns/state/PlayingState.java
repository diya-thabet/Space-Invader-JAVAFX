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

    // Movement Flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private double screenShakeTimer = 0;
    private double pauseCooldown = 0;
    private Random random = new Random();

    // Flag to prevent resetting game when resuming from Pause
    private boolean isInitialized = false;

    @Override
    public void onEnter(GameEngine context) {
        // Only initialize entities once (First start)
        if (!isInitialized) {
            rootEntity = new EntityGroup();
            starField = new StarField();

            player = (PlayerEntity) EntityFactory.createPlayer(context.getWidth() / 2, context.getHeight() - 80);
            rootEntity.add(player);

            startWave(context);
            Logger.getInstance().log("INFO", "Game Started - Stable Edition");
            isInitialized = true;
        }

        // Add cooldown when entering state (e.g., returning from Pause) to prevent bouncing
        pauseCooldown = 0.5;
    }

    private void startWave(GameEngine context) {
        enemySquad = new SquadEntity();
        rootEntity.add(enemySquad);

        int rows = 3 + (wave / 2);
        int cols = 6 + (wave / 2);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = 100 + c * 60;
                // Enemies spawn lower to clear HUD
                double y = 150 + r * 50;
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

        // Pass all 4 directions to player
        player.handleMovement(leftPressed, rightPressed, upPressed, downPressed);

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

        // --- HUD IMPROVEMENTS ---

        // 1. Semi-transparent background box for readability
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRoundRect(20, 20, 280, 140, 20, 20);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRoundRect(20, 20, 280, 140, 20, 20);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 24)); // Larger font

        // 2. Text moved down significantly (x=50, y=60+)
        gc.fillText("SCORE: " + score, 110, 60);
        gc.fillText("WAVE: " + wave, 90, 90);

        // 3. Health Bar moved inside the box
        double hpPercent = Math.max(0, player.getHealth()) / 5.0;

        // Backing
        gc.setFill(Color.BLACK);
        gc.fillRect(50, 110, 220, 25);

        // Red background
        gc.setFill(Color.web("#550000"));
        gc.fillRect(50, 110, 220, 25);

        // Green Health
        gc.setFill(Color.LIME);
        gc.fillRect(50, 110, hpPercent * 220, 25);

        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(50, 110, 220, 25);

        gc.restore();
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = true;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = true;
            if(event.getCode() == KeyCode.UP) upPressed = true;     // NEW
            if(event.getCode() == KeyCode.DOWN) downPressed = true; // NEW

            player.handleInput(event, rootEntity);

            if(event.getCode() == KeyCode.P && pauseCooldown <= 0) {
                context.setState(new PausedState(this));
            }

        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = false;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = false;
            if(event.getCode() == KeyCode.UP) upPressed = false;     // NEW
            if(event.getCode() == KeyCode.DOWN) downPressed = false; // NEW
        }
    }

    @Override
    public void onExit() {
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;
    }
}
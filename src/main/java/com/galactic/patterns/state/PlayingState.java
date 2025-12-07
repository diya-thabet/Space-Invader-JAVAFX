package com.galactic.patterns.state;

import com.galactic.core.GameEngine;
import com.galactic.patterns.composite.*;
import com.galactic.patterns.factory.EntityFactory;
import com.galactic.utils.Logger;
import com.galactic.view.Renderer;
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
    private PlayerEntity player;
    private double enemySpawnTimer = 0;
    private int score = 0;
    private double time = 0;

    // Control Flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // JUICE Mechanics
    private double screenShakeTimer = 0;
    private Random random = new Random();

    @Override
    public void onEnter(GameEngine context) {
        rootEntity = new EntityGroup();
        // Spawns player at bottom center
        player = (PlayerEntity) EntityFactory.createPlayer(context.getWidth() / 2, context.getHeight() - 80);
        rootEntity.add(player);
        Logger.getInstance().log("INFO", "Game Started - GLHF");
    }

    @Override
    public void update(GameEngine context, double deltaTime) {
        time += deltaTime;

        // 1. Shake Decay
        if (screenShakeTimer > 0) screenShakeTimer -= deltaTime;

        // 2. Player Input & Particles
        player.handleMovement(leftPressed, rightPressed);
        // Thruster particles
        if (Math.random() < 0.5) {
            rootEntity.add(EntityFactory.createBlueParticle(player.getX() + 25, player.getY() + 50));
        }

        // 3. Dynamic Enemy Spawning
        enemySpawnTimer += deltaTime;
        double spawnRate = Math.max(0.5, 1.5 - (score / 1000.0));

        if (enemySpawnTimer > spawnRate) {
            double x = Math.random() * (context.getWidth() - 40);
            GameEntity enemy = EntityFactory.createEnemy(x, -50, rootEntity);
            rootEntity.add(enemy);
            enemySpawnTimer = 0;
        }

        // 4. Update Physics
        rootEntity.update(deltaTime);

        // 5. Collisions
        checkCollisions(context);
    }

    private void checkCollisions(GameEngine context) {
        List<GameEntity> children = rootEntity.getChildren();
        List<GameEntity> toRemove = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            GameEntity a = children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                GameEntity b = children.get(j);

                if (a.getBounds().intersects(b.getBounds())) {
                    if (checkPair(a, b, "PLAYER_BULLET", "ENEMY")) {
                        handleEnemyHit(getType(a, b, "PLAYER_BULLET"), getType(a, b, "ENEMY"));
                    }
                    else if (checkPair(a, b, "ENEMY", "PLAYER")) {
                        getType(a, b, "ENEMY").setAlive(false);
                        player.takeDamage(1);
                        addScreenShake(0.3);
                        spawnExplosion(player.getX(), player.getY(), 10);
                        if(!player.isAlive()) context.setState(new GameOverState(score));
                    }
                    else if (checkPair(a, b, "ENEMY_BULLET", "PLAYER")) {
                        getType(a, b, "ENEMY_BULLET").setAlive(false);
                        player.takeDamage(1);
                        addScreenShake(0.1);
                        if(!player.isAlive()) context.setState(new GameOverState(score));
                    }
                    else if (checkPair(a, b, "PLAYER", "POWERUP")) {
                        getType(a, b, "POWERUP").setAlive(false);
                        player.upgradeWeapon();
                        score += 50;
                        Logger.getInstance().log("GAME", "PowerUp Collected!");
                    }
                }
            }
        }
    }

    private void handleEnemyHit(GameEntity bullet, GameEntity enemy) {
        bullet.setAlive(false);
        enemy.takeDamage(1);
        spawnExplosion(bullet.getX(), bullet.getY(), 2);

        if (!enemy.isAlive()) {
            score += 100;
            addScreenShake(0.05);
            spawnExplosion(enemy.getX(), enemy.getY(), 8);
            if (Math.random() < 0.2) {
                rootEntity.add(EntityFactory.createPowerUp(enemy.getX(), enemy.getY()));
            }
        }
    }

    private void spawnExplosion(double x, double y, int count) {
        for(int i=0; i<count; i++) rootEntity.add(EntityFactory.createParticle(x, y));
    }

    private void addScreenShake(double amount) {
        this.screenShakeTimer = amount;
    }

    private boolean checkPair(GameEntity a, GameEntity b, String t1, String t2) {
        return (a.getType().equals(t1) && b.getType().equals(t2)) || (a.getType().equals(t2) && b.getType().equals(t1));
    }

    private GameEntity getType(GameEntity a, GameEntity b, String type) {
        return a.getType().equals(type) ? a : b;
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        // --- APPLY SCREEN SHAKE ---
        gc.save();
        if (screenShakeTimer > 0) {
            double dx = (random.nextDouble() - 0.5) * 10 * screenShakeTimer;
            double dy = (random.nextDouble() - 0.5) * 10 * screenShakeTimer;
            gc.translate(dx, dy);
        }

        // Draw Retro Grid
        Renderer.drawRetroGrid(gc, context.getWidth(), context.getHeight(), time, 100);

        rootEntity.render(gc);

        // --- UI ---
        gc.save();
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 22));

        // Shadow for text
        DropShadow textShadow = new DropShadow();
        textShadow.setColor(Color.BLACK);
        textShadow.setRadius(2.0);
        textShadow.setSpread(0.8);
        gc.setEffect(textShadow);

        gc.fillText("SCORE: " + score, 20, 50);
        gc.fillText("SHIELD: ", 20, 80);

        // Shield Bar
        gc.setEffect(null);
        gc.setFill(Color.RED);
        gc.fillRect(110, 65, 100, 15);
        gc.setFill(Color.LIME);
        gc.fillRect(110, 65, (Math.max(0, player.getHealth()) / 5.0) * 100, 15);

        gc.restore();
        gc.restore();
    }

    @Override
    public void handleInput(GameEngine context, KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = true;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = true;
            player.handleInput(event, rootEntity);
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = false;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = false;
        }
    }

    @Override
    public void onExit() {}
}
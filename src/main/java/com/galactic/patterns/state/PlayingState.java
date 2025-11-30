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

    // Input Flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private List<double[]> stars = new ArrayList<>();

    @Override
    public void onEnter(GameEngine context) {
        rootEntity = new EntityGroup();
        player = (PlayerEntity) EntityFactory.createPlayer(context.getWidth() / 2, context.getHeight() - 80);
        rootEntity.add(player);

        for(int i=0; i<100; i++) {
            stars.add(new double[]{Math.random() * context.getWidth(), Math.random() * context.getHeight(), Math.random() * 2 + 0.5});
        }
        Logger.getInstance().log("INFO", "Game Started");
    }

    @Override
    public void update(GameEngine context, double deltaTime) {
        // 1. Handle Movement (Applique la vitesse basée sur les inputs)
        player.handleMovement(leftPressed, rightPressed);

        // 2. Update World
        rootEntity.update(deltaTime);

        // 3. Stars Logic
        for(double[] star : stars) {
            star[1] += star[2] * 60 * deltaTime;
            if(star[1] > context.getHeight()) {
                star[1] = 0;
                star[0] = Math.random() * context.getWidth();
            }
        }

        // 4. Enemy Spawning
        enemySpawnTimer += deltaTime;
        if (enemySpawnTimer > 1.5) {
            GameEntity enemy = EntityFactory.createEnemy(Math.random() * (context.getWidth() - 40), -40, rootEntity);
            rootEntity.add(enemy);
            enemySpawnTimer = 0;
        }

        checkCollisions(context);

        if (!player.isAlive()) {
            context.setState(new GameOverState(score));
        }
    }

    private void createExplosion(double x, double y) {
        for(int i=0; i<10; i++) rootEntity.add(EntityFactory.createParticle(x, y));
    }

    private void checkCollisions(GameEngine context) {
        List<GameEntity> children = rootEntity.getChildren();
        List<GameEntity> toRemove = new ArrayList<>();

        for (GameEntity a : children) {
            if (a.getY() > context.getHeight() + 50 || a.getY() < -50) {
                if(a instanceof BulletEntity) toRemove.add(a);
            }
            if (!a.isAlive()) continue;

            for (GameEntity b : children) {
                if (a == b || !b.isAlive()) continue;
                if (a.getBounds().intersects(b.getBounds())) {
                    if (a.getType().equals("PLAYER") && (b.getType().equals("ENEMY") || b.getType().equals("ENEMY_BULLET"))) {
                        a.takeDamage(1);
                        b.setAlive(false);
                        createExplosion(a.getX(), a.getY());
                        Logger.getInstance().log("HIT", "Player hit! HP: " + ((PlayerEntity)a).getHealth());
                    }
                    else if (a.getType().equals("PLAYER_BULLET") && b.getType().equals("ENEMY")) {
                        a.setAlive(false);
                        b.takeDamage(1);
                        if(!b.isAlive()) {
                            score += 100;
                            createExplosion(b.getX(), b.getY());
                            if(Math.random() < 0.1) rootEntity.add(EntityFactory.createPowerUp(b.getX(), b.getY()));
                        } else {
                            rootEntity.add(EntityFactory.createParticle(b.getX(), b.getY()));
                        }
                    }
                    else if (a.getType().equals("PLAYER") && b.getType().equals("POWERUP")) {
                        b.setAlive(false);
                        player.upgradeWeapon();
                        score += 50;
                    }
                }
            }
        }
        children.removeIf(e -> !e.isAlive() || toRemove.contains(e));
    }

    @Override
    public void render(GameEngine context, GraphicsContext gc) {
        Renderer.drawBackground(gc, context.getWidth(), context.getHeight());
        gc.setFill(Color.WHITE);
        for(double[] star : stars) gc.fillOval(star[0], star[1], star[2], star[2]);
        rootEntity.render(gc);
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
            player.handleInput(event, rootEntity);
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if(event.getCode() == KeyCode.LEFT) leftPressed = false;
            if(event.getCode() == KeyCode.RIGHT) rightPressed = false;
        }
    }

    @Override
    public void onExit() {}
}
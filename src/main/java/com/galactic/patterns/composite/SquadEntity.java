package com.galactic.patterns.composite;

import com.galactic.core.GameEngine;
import java.util.List;

public class SquadEntity extends EntityGroup {
    private double dx = 150; // Horizontal Speed
    private double dropDistance = 20;
    private boolean movingRight = true;

    public SquadEntity() {
        super();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime); // Updates animations of children

        double screenWidth = GameEngine.getInstance().getWidth();
        boolean hitWall = false;

        List<GameEntity> enemies = getChildren();
        if (enemies.isEmpty()) return;

        // 1. Move Horizontally
        double moveAmount = dx * deltaTime;
        for (GameEntity e : enemies) {
            e.x += moveAmount;

            // Check bounds
            if ((movingRight && e.x + e.w > screenWidth - 20) || (!movingRight && e.x < 20)) {
                hitWall = true;
            }
        }

        // 2. If Wall Hit, Reverse and Drop
        if (hitWall) {
            movingRight = !movingRight;
            dx = -dx;
            for (GameEntity e : enemies) {
                e.y += dropDistance;
                // Push back slightly to prevent sticking
                e.x += (movingRight ? 5 : -5);
            }
        }
    }
}
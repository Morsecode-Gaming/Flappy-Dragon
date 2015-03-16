package com.morsecodegaming.flappydragongl.projectiles;

import android.graphics.Color;

import com.morsecodegaming.flappydragongl.projectiles.Projectile;

import framework.graphics.shape.Square;
import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-02-17.
 */
public class Fireball extends Projectile {
    public final int WIDTH = 20;
    public final int HEIGHT = 20;
    public final int FORCE = 500;

    private int targetX, targetY;

    public Fireball(float x, float y, Force force) {
        this.x = x;
        this.y = y;
        hit = false;
        generateShape();
        applyForce(force);
    }

    @Override
    protected void generateShape() {
        shape = new Square(x, y, WIDTH, HEIGHT, Color.RED);
    }

    private void checkHits() {
    }

    @Override
    public void hitTarget() {
    }
}
package com.morsecodegaming.flappydragongl.projectiles;

import android.graphics.Color;

import com.morsecodegaming.flappydragongl.projectiles.Projectile;

import framework.graphics.shape.Square;

/**
 * Created by Morsecode Gaming on 2015-02-19.
 */
public class Rock extends Projectile {
    public static final int HEIGHT = 25;
    public static final int WIDTH = 25;

    private final int SPEED = 6;

    public Rock(float x, float y) {
        this.x = x;
        this.y = y;
        mass = 1.5f;
        scrollsWithBackground = true;
        generateShape();
    }

    @Override
    protected void generateShape() {
        shape = new Square(x,y,WIDTH,HEIGHT, Color.LTGRAY, "Rock");
    }

    private void checkHits() {
    }

    @Override
    public void hitTarget() {
        super.hitTarget();
    }
}

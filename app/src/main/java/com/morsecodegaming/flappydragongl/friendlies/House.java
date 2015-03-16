package com.morsecodegaming.flappydragongl.friendlies;

import android.graphics.Color;

import framework.graphics.shape.Square;
import framework.physics.PhysicsConstants;
import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-03-03.
 */
public class House extends Friendly {
    private final int WIDTH = 75;
    private final int HEIGHT = 100;

    public House() {
        x = 1915;
        y = 880;
        width = WIDTH;
        height = HEIGHT;
        scrollsWithBackground = true;
        generateShape();
    }

    @Override
    protected void generateShape() {
        shape = new Square(x,y,width,height, Color.GRAY);
    }
}

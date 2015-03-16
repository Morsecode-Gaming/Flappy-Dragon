package com.morsecodegaming.flappydragongl.character;

import com.morsecodegaming.flappydragongl.R;
import com.morsecodegaming.flappydragongl.projectiles.Fireball;

import java.util.ArrayList;

import framework.GameObject;

import framework.graphics.textures.sprites.Sprite;
import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-02-27.
 */
public class Dragon extends GameObject {
    public ArrayList<Fireball> fireballs = new ArrayList<>();
    private int health;

    public Dragon () {
        super();
        mass = 2f;
        health = 3;
        generateShape();
    }

    @Override
    protected void generateShape() {
//        shape = new Square(500, 880, 250, 100, Color.MAGENTA);
        shape = new Sprite(500, 880, 250, 100, R.drawable.dragon1_small_uncoloured);
    }

    // Attack Methods
    public void fire(Force fireForce) {
        float dragonVector[] = shape.getTransformedVector();
        Fireball fireball = new Fireball(dragonVector[0]+shape.getWidth(), dragonVector[1]+(shape.getHeight()/2), fireForce);
        fireballs.add(fireball);
    }

    public void getHit(int damage) {
        health -= damage;
    }

    // Getters
    public float getCenterX() {
        return shape.getTransformedVector()[0]+(shape.getWidth()/2);
    }

    public float getCenterY() {
        return shape.getTransformedVector()[1]+(shape.getHeight()/2);
    }

    public int getHealth() {
        return health;
    }
}

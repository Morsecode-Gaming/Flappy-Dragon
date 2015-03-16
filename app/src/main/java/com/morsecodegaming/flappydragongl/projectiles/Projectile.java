package com.morsecodegaming.flappydragongl.projectiles;

import framework.GameObject;
import framework.physics.PhysicsObject;

/**
 * Created by Morsecode Gaming on 2015-02-17.
 */
public abstract class Projectile extends GameObject {
    protected float x, y;
    protected boolean hit;

    public Projectile() {
        super();
    }

    public void hitTarget() {
        hit = true;
    }

    public boolean isHit() {
        return hit;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

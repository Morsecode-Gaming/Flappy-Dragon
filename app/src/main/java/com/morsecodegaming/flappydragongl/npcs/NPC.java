package com.morsecodegaming.flappydragongl.npcs;

import framework.GameObject;

/**
 * Created by bajmorse on 15-03-24.
 */
public abstract class NPC extends GameObject {
    protected int points;

    // Setters
    public void setPoints(int points) {
        this.points = points;
    }

    // Getters
    public int getPoints() {
        return points;
    }
}

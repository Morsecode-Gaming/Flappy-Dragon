package com.morsecodegaming.flappydragongl.enemies;

import com.morsecodegaming.flappydragongl.views.MainView;

import framework.GameObject;

/**
 * Created by Morsecode Gaming on 2015-02-17.
 */
public abstract class Enemy extends GameObject {
    protected int x, y;
    protected int width, height;
    protected int health, damage;
    protected boolean alive;
    protected boolean readyToFire;
    MainView gameView;

    public Enemy() {
        super();
    }

    public void hit(int damage) {
        health -= damage;

        if (health <= 0) {
            die();
        }
    }

    public abstract void update();

    public void die() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isReadyToFire() {
        return readyToFire;
    }

    public void setGameView(MainView gameView) {
        this.gameView = gameView;
    }
}

package com.morsecodegaming.flappydragongl.npcs.friendlies;

import com.morsecodegaming.flappydragongl.npcs.NPC;

import framework.GameObject;

/**
 * Created by Morsecode Gaming on 2015-03-03.
 */
public abstract class Friendly extends NPC {
    protected int x, y;
    protected int width, height;

    public Friendly() {
        super();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

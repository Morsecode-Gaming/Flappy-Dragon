package com.morsecodegaming.flappydragongl.miscellaneous;

import android.content.Context;

import framework.GameObject;
import framework.graphics.textures.sprites.BackgroundSprite;
import framework.graphics.textures.sprites.Sprite;

public class Background extends GameObject {
    private Context context;
	private float x, y, width, height;
    private float scrollSpeed;
    private int resourceId;

    public Background(Context context, int scrollSpeed, float x, float y, float width, float height, int resourceId) {
        this.context = context;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scrollSpeed = scrollSpeed;
        this.resourceId = resourceId;
        generateShape();
    }

    @Override
    protected void generateShape() {
        shape = new BackgroundSprite(context, x, y, width, height, resourceId);
    }

	/*
	 * Game Loop Methods
	 */
    public void update() {
        ((BackgroundSprite) shape).scrollSprite(scrollSpeed);
    }

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}

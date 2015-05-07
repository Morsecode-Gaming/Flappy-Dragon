package com.morsecodegaming.flappydragongl.npcs.enemies;

import android.graphics.Color;

import com.morsecodegaming.flappydragongl.projectiles.Rock;
import com.morsecodegaming.flappydragongl.views.MainView;

import framework.graphics.shape.Square;
import framework.physics.PhysicsConstants;
import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-02-17.
 */
public class Catapult extends Enemy {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    private long lastShot;

    public Catapult(MainView mainView) {
        x = 1915;
        y = 920;
        width = WIDTH;
        height = HEIGHT;
        scrollsWithBackground = true;
        readyToFire = false;
        lastShot = System.currentTimeMillis() - 2500;
        setGameView(mainView);
        setPoints(10);
        generateShape();

        Force normalForce = new Force(PhysicsConstants.gravityStrength(mass), new float[]{0f,-1f,0f});
        applyForce(normalForce);
    }

    @Override
    protected void generateShape() {
        shape = new Square(x,y,width,height, Color.DKGRAY);
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - lastShot > 3000) {
            lastShot = System.currentTimeMillis();
            fire();
        }
    }

    private void fire() {
        float dragonVector[] = gameView.getPlayerDragon().getShape().getTransformedVector();

        float xPoint = dragonVector[0] + gameView.getPlayerDragon().getShape().getWidth() + (dragonVector[0] < x ? 100 : -50);
        float dx = xPoint - shape.getTransformedVector()[0];

        float dy = dragonVector[1] - shape.getTransformedVector()[1];
        final Force fireForce = new Force(300, new float[]{dx, dy, 0f}, 50);

        Rock rock = new Rock((int) shape.getTransformedVector()[0], (int) shape.getTransformedVector()[1]);
        rock.applyForce(fireForce);
        gameView.projectiles.add(rock);
        gameView.addGameObject(rock);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
    }
}

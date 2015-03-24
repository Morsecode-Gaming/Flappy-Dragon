package com.morsecodegaming.flappydragongl.views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.morsecodegaming.flappydragongl.GameState;
import com.morsecodegaming.flappydragongl.R;
import com.morsecodegaming.flappydragongl.player.Dragon;
import com.morsecodegaming.flappydragongl.miscellaneous.Background;
import com.morsecodegaming.flappydragongl.projectiles.Fireball;
import com.morsecodegaming.flappydragongl.npcs.enemies.Catapult;
import com.morsecodegaming.flappydragongl.npcs.enemies.Enemy;
import com.morsecodegaming.flappydragongl.npcs.friendlies.Friendly;
import com.morsecodegaming.flappydragongl.npcs.friendlies.House;
import com.morsecodegaming.flappydragongl.projectiles.Projectile;

import java.util.ArrayList;
import java.util.Random;

import framework.GameSurfaceView;
import framework.graphics.GameGLRenderer;
import framework.graphics.shape.Square;
import framework.graphics.textures.text.TextAtlas;
import framework.physics.forces.Force;
import framework.ui.Button;
import framework.ui.Label;
import framework.utils.Line;

/**
 * Created by Morsecode Gaming on 2015-02-25.
 */
public class MainView extends GameSurfaceView {
    // State Machine
    private int gameState = GameState.READY;

    // Constants
    private final int GROUND = 980;

    // Game Objects
    public Dragon playerDragon;
    public Background background;
    public ArrayList<Friendly> friendlies = new ArrayList<>();
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Projectile> projectiles = new ArrayList<>();

    // UI Buttons
    private Button pauseButton;
    private ArrayList<Button> pauseMenu;

    // Game Variables and Labels
    private int score = 0;
    private Label lifeLabel;
    private Label fireLabel;
    private Label scoreLabel;
    private Label lifeValue;
    private Label fireValue;
    private Label scoreValue;

    // Constructors
    public MainView(Context context) {
        super(context);
    }

    @Override
    public void surfaceWasCreated() {
        super.surfaceWasCreated();

        // Scroll Background
        backgroundScrollSpeed = 6;
        foregroundScrollSpeed = 4;

        // Setup initial objects
        setupGame();
    }

    private void setupGame() {
        createBackground();
        createDragon();
        createGround();
        createUI();
        loadAtlases();
    }

    private void loadAtlases() {
        renderer.addGameShape(TextAtlas.getInstance());
    }

    private void createBackground() {
        background = new Background(getContext(), backgroundScrollSpeed, 0, 0, screenWidth, GROUND, R.drawable.robot);
        renderer.addGameShape(background.getShape());
    }

    private void createGround() {
        Square ground = new Square(0, GROUND, 1920, (int) (screenHeight-GROUND), Color.rgb(139, 69, 19));
        renderer.addGameShape(ground);
    }

    private void createDragon() {
        playerDragon = new Dragon();
        addGameObject(playerDragon);
    }

    private void createUI() {
        // Add Pause Button
        pauseButton = new Button("Pause", (int) (screenWidth-250), 0, 250, 125, Color.YELLOW, Color.RED) {
            @Override
            public void click() {
                if (gameState == GameState.PLAYING) {
                    pauseGame();
                } else if (gameState == GameState.PAUSED) {
                    resumeGame();
                }
            }
        };
        addButton(pauseButton);

        lifeLabel = new Label("Life:", 10, 10, Color.RED);
        lifeValue = new Label("3", 155, 10, Color.RED);
        fireLabel = new Label("Flame:", 10, 50, Color.RED);
        fireValue = new Label("5", 250, 50, Color.RED);
        scoreLabel = new Label("Score:", 600, 10, Color.RED);
        scoreValue = new Label("0", 840, 10, Color.RED);
    }

    // Game Flow (state-change) Methods
    private void startGame() {
        Log.d("GAME STATE", "Start Game");
        playGame();
        Force takeOffForce;
        takeOffForce = new Force(100, new float[]{0f, -1f, 0f}, 75);
        playerDragon.applyForce(takeOffForce);
    }

    public void pauseGame() {
        Log.d("GAME STATE", "Pause Game");
        gameState = GameState.PAUSED;
        updateThread.pauseThread();
        showPauseMenu();
    }

    private void showPauseMenu() {
        pauseButton.hide();

        if (pauseMenu == null) {
            createPauseMenu();
        }
        for (Button button : pauseMenu) {
            addButton(button);
        }
    }

    private void createPauseMenu() {
        // Pause Menu
        pauseMenu = new ArrayList<>();

        // Resume Button
        pauseMenu.add(new Button("Resume", 300, 200, (int) (screenWidth-600), 200, Color.BLUE, Color.BLACK) {
            @Override
            public void click() {
                resumeGame();
            }
        });

        // Reset Button
        pauseMenu.add(new Button("Reset", 300, 600, (int) (screenWidth-600), 200, Color.RED, Color.BLACK) {
            @Override
            public void click() {
                resetGame();
            }
        });
    }

    private void playGame() {
        Log.d("GAME STATE", "Play Game");
        gameState = GameState.PLAYING;
        if (!updateThread.isAlive()) {
            updateThread.start();
        }
        updateThread.startThread();
    }

    public void resumeGame() {
        Log.d("GAME STATE", "Resume Game");
        pauseButton.show();
        hidePauseMenu();
        playGame();
    }

    private void hidePauseMenu() {
        for (Button button : pauseMenu) {
            destroyButton(button);
        }
    }

    protected void resetGame() {
        Log.d("GAME STATE", "Reset Game");
        clearObjects();
        setupGame();
        updateThread.pauseThread();
        gameState = GameState.READY;
    }

    @Override
    protected void clearObjects() {
        super.clearObjects();
        enemies.clear();
        friendlies.clear();
        projectiles.clear();
    }

    // Touch Handling
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!super.onTouchEvent(event)) {
            if (gameState == GameState.READY) {
                startGame();
            } else if (gameState == GameState.PLAYING) {
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);
                final int maskedAction = event.getActionMasked();

                final float eventX = event.getX(pointerIndex);
                final float eventY = event.getY(pointerIndex);
                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        if (eventX < 500) {
                            Force flapForce;
                            if (playerDragon.getVelocity()[1] >= 0) {
                                flapForce = new Force(150, new float[]{0f, -1f, 0f}, 75);
                            } else {
                                flapForce = new Force(75, new float[]{0f, -1f, 0f}, 50);
                            }
                            playerDragon.applyForce(flapForce);
                        } else {
                            float dragonVector[] = playerDragon.getShape().getTransformedVector();
                            float dx = eventX - dragonVector[0];
                            float dy = eventY - dragonVector[1];
                            final Force fireForce = new Force(350, new float[]{dx, dy, 0f}, 100);
                            playerDragon.fire(fireForce);
                            addGameObject(playerDragon.fireballs.get(playerDragon.fireballs.size() - 1));
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    protected void update() {
        super.update();

        if (updateCount == 100) {
            Random random = new Random();
            int eventIndex = random.nextInt(10);
            if (eventIndex == 1) {
                Log.d("SPAWN", "Spawn a house!");
                House house = new House();
                friendlies.add(house);
                addGameObject(house);
            } else if (eventIndex == 2) {
                Log.d("SPAWN", "Spawn a catapult!");
                Catapult catapult = new Catapult(this);
                enemies.add(catapult);
                addGameObject(catapult);
            }
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                background.update();
            }
        });

        playerDragon.getShape().translate(playerDragon.getVelocity());
        if (playerDragon.getShape().getTransformedVector()[1] > (GROUND - playerDragon.getShape().getHeight())) {
            resetGame();
        }

        for (int i = 0; i < playerDragon.fireballs.size(); i++) {
            Fireball fireball = playerDragon.fireballs.get(i);
            fireball.getShape().translate(fireball.getVelocity());
            checkFireballHit(fireball);
        }

        for (int i = 0; i < friendlies.size(); i++) {
            Friendly friendly = friendlies.get(i);
            if (friendly.getShape().getTransformedVector()[0] < -50) {
                friendlies.remove(friendly);
                removeGameObject(friendly);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.update();
            if (enemy.getShape().getTransformedVector()[0] < -50) {
                enemies.remove(enemy);
                removeGameObject(enemy);
            }
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            projectile.getShape().translate(projectile.getVelocity());
            float transformedVector[] = projectile.getShape().getTransformedVector();
            if (transformedVector[0] < -50 || transformedVector[0] > screenWidth || transformedVector[1] > screenHeight) {
                projectiles.remove(projectile);
                removeGameObject(projectile);
            } else {
                checkProjectileHit(projectile);
            }
        }

        updateUI();
    }

    private void updateUI() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                scoreValue.setText(Integer.toString(score));
            }
        });
    }

    private void checkFireballHit(Fireball fireball) {
        float transformedVector[] = fireball.getShape().getTransformedVector();

        if (transformedVector[0] > GameGLRenderer.screenWidth || transformedVector[1] > GameGLRenderer.screenHeight) {
            playerDragon.fireballs.remove(fireball);
            removeGameObject(fireball);
        } else {
            Line line = fireball.getMovementLine();
            for (int i = 0; i < friendlies.size(); i++) {
                Friendly friendly = friendlies.get(i);
                if (line.intersects(friendly.getShape())) {
                    score -= friendly.getPoints();
                    friendlies.remove(friendly);
                    removeGameObject(friendly);
                }
            }
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (line.intersects(enemy.getShape())) {
                    score += enemy.getPoints();
                    enemies.remove(enemy);
                    removeGameObject(enemy);
                }
            }
        }
    }

    private void checkProjectileHit(Projectile projectile) {
        float transformedVector[] = projectile.getShape().getTransformedVector();

        Line line;
        if (transformedVector[0] > GameGLRenderer.screenWidth || transformedVector[1] > GameGLRenderer.screenHeight) {
            projectiles.remove(projectile);
            removeGameObject(projectile);
        } else {
            line = projectile.getMovementLine();
            if (line.intersects(playerDragon.getShape())) {
                Log.d("PLAYER_DRAGON", "Was hit!");
                playerDragon.getHit(1);
                projectiles.remove(projectile);
                removeGameObject(projectile);
                if (playerDragon.getHealth() <= 0) {
                    resetGame();
                }
            }
        }
    }

    // Getters
    public Dragon getPlayerDragon() {
        return playerDragon;
    }
}

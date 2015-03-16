package framework;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;

import framework.graphics.GameGLRenderer;
import framework.graphics.SurfaceViewListener;
import framework.graphics.textures.text.TextAtlas;
import framework.ui.Button;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public abstract class GameSurfaceView extends GLSurfaceView implements SurfaceViewListener {
    protected float screenWidth, screenHeight;

    protected final float TOUCH_SCALE_FACTOR = 180.f / 320;
    public final GameGLRenderer renderer;

    protected UpdateThread updateThread = new UpdateThread();
    protected int backgroundScrollSpeed = 0;
    protected int updateCount = 0;

    protected ArrayList<GameObject> backgroundObjects = new ArrayList<>();
    protected ArrayList<GameObject> gameObjects = new ArrayList<>();
    protected ArrayList<GameObject> uiObjects = new ArrayList<>();

    protected ArrayList<Button> buttons = new ArrayList<>();
    private SparseArray<Button> clickedButtons = new SparseArray<>();

    private SparseArray<PointF> activePointers = new SparseArray<>();

    public GameSurfaceView(Context context) {
        super(context);

        // Set OpenGL Context version to ES 2.0
        setEGLContextClientVersion(2);

        // Create the OpenGL Renderer for drawing on the GLSurfaceView
        renderer = new GameGLRenderer(context, this);
        setRenderer(renderer);

        // Set Screen Size
        calculateScreenSize(context);

        // Set the Render Mode
        /*
            * Continuously - Continuously re-render the scene
            * When Dirty - Only re-render when requestRender() is called
         */
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private void calculateScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(point);
        } else {
            display.getSize(point);
        }
        screenWidth = point.x;
        screenHeight = point.y;
    }

    // Lifecycle Methods
    @Override
    public void onResume() {
        super.onResume();
        if (updateThread.isAlive()) {
            updateThread.startThread();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (updateThread.isAlive()) {
            updateThread.stopThread();
        }
    }

    // SurfaceViewListener Methods
    @Override
    public void surfaceWasCreated() {
    }

    // Game Object Methods
    public void addBackgroundObject(final GameObject object) {
        backgroundObjects.add(object);
        renderer.addGameShape(object.getShape());
    }

    public void removeBackgroundObject(GameObject object) {
        backgroundObjects.remove(object);
        renderer.removeShape(object.getShape());
    }

    public void addGameObject(final GameObject object) {
        gameObjects.add(object);
        renderer.addGameShape(object.getShape());
    }

    public void removeGameObject(GameObject object) {
        gameObjects.remove(object);
        renderer.removeShape(object.getShape());
    }

    public void addUIElement (final GameObject object) {
        uiObjects.add(object);
        renderer.addGameShape(object.getShape());
    }

    public void removeUIElement (final GameObject object) {
        uiObjects.remove(object);
        renderer.removeShape(object.getShape());
    }

    protected void clearObjects() {
        gameObjects.clear();
        renderer.clearShapes();
    }

    // Buttons Methods
    public void addButton(final Button button) {
        if (Thread.currentThread().getName().contains("GLThread")) {
            button.generateShape();
            buttons.add(button);
            renderer.addGameShape(button.getShape());
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    button.generateShape();
                    buttons.add(button);
                    renderer.addGameShape(button.getShape());
                }
            });
        }
    }

    public void destroyButton(Button button) {
        buttons.remove(button);
        for (int i : button.getTextIndices()) {
            TextAtlas.getInstance().removeTexture(i);
        }
        renderer.removeShape(button.getShape());
    }

    // Touch Handling
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean touchHandled = false;

        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
        final int maskedAction = event.getActionMasked();
        final PointF point = new PointF(event.getX(pointerIndex), event.getY(pointerIndex));
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                activePointers.put(pointerId, point);
                for (final Button button : buttons) {
                    if (button.getShape().isVisible() && button.checkClick(point)) {
                        clickedButtons.put(pointerId, button);
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                button.press();
                            }
                        });
                        touchHandled = true;
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    int movePointerId = event.getPointerId(i);
                    final Button clickedButton = clickedButtons.get(movePointerId);
                    if (clickedButton != null) {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                boolean isPressing = clickedButton.checkClick(point);
                                if (isPressing && !clickedButton.isPressed()) {
                                    clickedButton.press();
                                } else if (!isPressing && clickedButton.isPressed()) {
                                    clickedButton.release();
                                }
                            }
                        });
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                final Button clickedButton = clickedButtons.get(pointerId);
                if (clickedButton != null) {
                    boolean isPressing = clickedButton.checkClick(point);
                    if (isPressing) {
                        clickedButton.click();
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                clickedButtons.get(pointerId).release();
                                clickedButtons.remove(pointerId);
                            }
                        });
                    }
                    activePointers.remove(pointerId);
                    touchHandled = true;
                }
                break;
            }
        }
        return touchHandled;
    }

    // Updates
    protected class UpdateThread extends Thread {
        private boolean running = true;
        private boolean paused = false;

        @Override
        public void run() {
            while (running) {
                try {
//                    sleep(16,666666);
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!paused) {
                    if (updateCount == 60) {
                        updateCount = 0;
                    } else {
                        updateCount++;
                    }

                    for (int i = 0; i < gameObjects.size(); i++) {
                        GameObject object = gameObjects.get(i);
                        object.calculateVelocity();
                        if (object.doesScrollWithBackground()) {
                            object.getShape().translateX(-backgroundScrollSpeed);
                        }
                    }

                    update();
                }
            }
        }

        public void startThread() { running = true; paused = false; }
        public void pauseThread() { paused = true; }
        public void stopThread() {
            running = false;
        }
    }

    protected void update() {}
}

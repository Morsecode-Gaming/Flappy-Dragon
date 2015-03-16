package framework.graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.morsecodegaming.flappydragongl.R;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import framework.GameObject;
import framework.graphics.shape.OpenGLShape;
import framework.graphics.textures.TextureManager;
import framework.utils.FPSCounter;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public class GameGLRenderer implements GLSurfaceView.Renderer {
    // Screen Resolution (STATIC)
    public static float screenWidth, screenHeight;

    // FPS Counter
    private FPSCounter fpsCounter = new FPSCounter();

    // Context and lifecycle variables
    private final Context context;
    private final SurfaceViewListener listener;

    // Images and gameShapes
    private ArrayList<OpenGLShape> backgroundShapes = new ArrayList<>();
    private ArrayList<OpenGLShape> gameShapes = new ArrayList<>();
    private ArrayList<OpenGLShape> uiShapes = new ArrayList<>();

    // OpenGL rendering matrices
    private float[] mvpMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    // Transformation matrices
    private float[] rotationMatrix;
    private float[] translationMatrix;
    private float[] transformationMatrix;

    public GameGLRenderer(final Context context, SurfaceViewListener listener) {
        this.context = context;
        this.listener = listener;;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.f, 1.f, 1.f, 1.f);

        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);

        listener.surfaceWasCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        GLES20.glViewport(0,0,width,height);

        // Set projection matrix
        Matrix.orthoM(projectionMatrix, 0, 0f, screenWidth, screenHeight, 0f, -3f, 7f);

        // Set the view matrix (camera position)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);

        // Calculate the mvp (model-view-projection) matrix
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        loadFontsAndImages();
    }

    private void loadFontsAndImages() {
        TextureManager textureManager = TextureManager.getInstance();
        if (!textureManager.hasContext()) {
            textureManager.setContext(context);
        }

        // Load Images
        textureManager.loadImage(R.drawable.dragon1_small_uncoloured);
        textureManager.loadImage(R.drawable.sky_small);
        textureManager.loadImage(R.drawable.enemy_textures);

        // Load Fonts
        textureManager.loadImage(R.drawable.font);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        fpsCounter.logFrame();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        renderShapes();
    }

    private void renderShapes() {
        renderShapeArray(backgroundShapes);
        renderShapeArray(gameShapes);
        renderShapeArray(uiShapes);
    }

    private void renderShapeArray(ArrayList<OpenGLShape> shapeArray) {
        for (int i = 0; i < shapeArray.size(); i++) {
            OpenGLShape shape = shapeArray.get(i);
            if (shape == null) {
                shapeArray.remove(i);
            } else if (shape.isVisible()) {
                shape.draw(mvpMatrix);
            }
        }
    }

    // Shapes
    public void addBackgroundShape(OpenGLShape shape) {
        backgroundShapes.add(shape);
    }

    public void addGameShape(OpenGLShape shape) {
        gameShapes.add(shape);
    }

    public void addUIShape(OpenGLShape shape) {
        uiShapes.add(shape);
    }

    public void removeShape(OpenGLShape shape) {
        if (backgroundShapes.indexOf(shape) >= 0) {
            backgroundShapes.remove(shape);
        } else if (gameShapes.indexOf(shape) >= 0) {
            gameShapes.remove(shape);
        } else if (uiShapes.indexOf(shape) >= 0) {
            uiShapes.remove(shape);
        } else {
            Log.w("RENDERER", "Attempting to remove a shape that doesn't exist");
        }
    }

    public void clearShapes() {
        gameShapes.clear();
    }
}

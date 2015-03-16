package framework.graphics.shape;

import android.graphics.Color;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import framework.graphics.tools.ColorTools;
import framework.utils.Line;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public abstract class OpenGLShape {
    // Tag
    protected String tag = "";

    // Visibility
    protected boolean visible = true;

    // Number of dimensions
    public static final int COORDS_PER_VERTEX = 3;

    // Number of coordinates * 4 bytes per float
    protected final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    // Shader programs and buffers
    protected boolean setupBuffers = false;
    protected int shaderProgram;
    protected FloatBuffer vertexBuffer;
    protected ShortBuffer drawListBuffer;

    // Handles
    protected int positionHandle;
    protected int colorHandle;
    protected int mvpMatrixHandle;
    protected int transformationMatrixHandle;

    // Shape Coordinates
    protected float x, y;
    protected float transformedX, transformedY;
    protected float width, height;
    protected float rotationX, rotationY;

    // Shape vertices
    protected float vertices[];
    protected short drawOrder[];
    protected int vertexCount;

    // Shape modifiers
    protected float color[];
    protected float translationVector[] = new float[]{0f,0f,0f};
    protected float rotationVector[] =new float[]{0f,0f,0f};
    protected float[] modelMatrix = new float[16];

    // Unimplemented Methods
    public abstract void draw(float[] mvpMatrix);
    protected abstract void calculateVertices();
    protected abstract void setupBuffers();

    // Implemented Methods
    // Initialization
    protected void initialize() {
        modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
    }

    // Transformations
    public void rotateX(float rotation) {
        Matrix.translateM(modelMatrix, 0, x+(width/2f), y+(height/2f), 0);
        Matrix.rotateM(modelMatrix, 0, rotation, 1f, 0f, 0f);
        Matrix.translateM(modelMatrix, 0, x, y, 0);
    }

    public void rotateY(float rotation) {
        Matrix.translateM(modelMatrix, 0, -(x+(width/2f)), -(y+(height/2f)), 0);
        Matrix.rotateM(modelMatrix, 0, rotation, 0f, 1f, 0f);
        Matrix.translateM(modelMatrix, 0, -(x+(width/2f)), -(y+(height/2f)), 0);
    }

    public void rotateZ(float rotation) {
        Matrix.translateM(modelMatrix, 0, x+(width/2f), y+(height/2f), 0);
        Matrix.rotateM(modelMatrix, 0, rotation, 0f, 0f, 1f);
        Matrix.translateM(modelMatrix, 0, -(x+(width/2f)), -(y+(height/2f)), 0);
    }

    public void translate(float translation[]) {
        translationVector[0] += translation[0];
        translationVector[1] += translation[1];
        Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);
    }

    public void translateTo(float x, float y) {
        translationVector[0] = x - this.x;
        translationVector[1] = y - this.y;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, translationVector[0], translationVector[1], 0f);
    }

    public void translateX(float translation) {
        translationVector[0] += translation;
        Matrix.translateM(modelMatrix, 0, translation, 0f, 0f);
    }

    public void translateY(float translation) {
        translationVector[1] += translation;
        Matrix.translateM(modelMatrix, 0, 0f, translation, 0f);
    }

    public void translateZ(float translation) {
        Matrix.translateM(modelMatrix, 0, 0f, 0f, translation);
    }

    public void resetTransformations() {
        Matrix.setIdentityM(modelMatrix, 0);
        translationVector = new float[]{0f,0f,0f};
    }

    public void resetModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public float[] getTransformedVector() {
        return new float[]{x+translationVector[0], y+translationVector[1]};
    }

    public ArrayList<Line> getEdges() {
        ArrayList<Line> edges = new ArrayList<>();

        for (int i = 0; i < (vertices.length-7); i+=3) {
            edges.add(new Line(vertices[i]+translationVector[0],vertices[i+1]+translationVector[1],vertices[i+3]+translationVector[0],vertices[i+4]+translationVector[1]));
        }
        edges.add(new Line(vertices[vertices.length-3]+translationVector[0],vertices[vertices.length-2]+translationVector[1],vertices[0]+translationVector[0],vertices[1]+translationVector[1]));

        return edges;
    }

    // Setters
    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setColor(int color) {
        this.color = ColorTools.colorIntToRGBA(color);
    }

    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float[] getColor() {
        return color;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getTag() {
        return tag;
    }
}

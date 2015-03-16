package framework.graphics.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import framework.graphics.GameGLRenderer;
import framework.graphics.tools.Shaders;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public class Square extends OpenGLShape {
    public Square(float x, float y, int width, int height, int color) {
        tag = "Square";
        setup(x, y, width, height, color);
    }

    public Square(float x, float y, int width, int height, int color, String tag) {
        this.tag = tag;
        setup(x, y, width, height, color);
    }

    private void setup(float x, float y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        transformedX = x;
        transformedY = y;

        this.width = width;
        this.height = height;
        rotationX = 0;
        rotationY = 0;

        initialize();
        calculateVertices();
        setColor(color);
    }

    @Override
    protected void calculateVertices() {
        vertices = new float[] {
                x, y, 0.f, // top left
                x+rotationX, y+height, 0.f, // bottom left
                x+width+rotationX, y+height+rotationY, 0.f, // bottom right
                x+width, y+rotationY, 0.f // top right
        };
    }

    @Override
    protected void setupBuffers() {
        drawOrder = new short[] {
                0,1,2,0,2,3
        };
        vertexCount = vertices.length / COORDS_PER_VERTEX;

        if (vertexBuffer != null) {
            vertexBuffer.clear();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        if (drawListBuffer != null) {
            drawListBuffer.clear();
        }
        ByteBuffer drawBuffer = ByteBuffer.allocateDirect(drawOrder.length*2);
        drawBuffer.order(ByteOrder.nativeOrder());
        drawListBuffer = drawBuffer.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.solidColorVertexShader);
        int fragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.solidColorFragmentShader);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        setupBuffers = true;
    }

    public void draw(float[] mvpMatrix) {
        if (!setupBuffers) {
            setupBuffers();
        }

        GLES20.glUseProgram(shaderProgram);

        // Calculate the scratch matrix
        float scratch[] = new float[16];
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, modelMatrix, 0);

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glBlendColor(color[0], color[1], color[2], color[3]);

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratch, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}

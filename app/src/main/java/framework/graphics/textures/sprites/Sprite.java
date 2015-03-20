package framework.graphics.textures.sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import framework.graphics.GameGLRenderer;
import framework.graphics.textures.TexturedShape;
import framework.graphics.tools.Shaders;
import framework.utils.GraphicsUtils;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public class Sprite extends TexturedShape {
    // New for textures
    protected int resourceId;

    // New buffers and handle
    protected FloatBuffer textureBuffer;
    protected float u, v, textureWidth, textureHeight;

    protected Sprite(){}

    public Sprite(float x, float y, float width, float height, int resourceId) {
        this.resourceId = resourceId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rotationX = 0;
        rotationY = 0;

        u = 0.f;
        v = 0.f;
        textureWidth = 1.f;
        textureHeight = 1.f;

        initialize();
        calculateVertices();
    }

    @Override
    protected void calculateVertices() {
        vertices = new float[] {
                x, y, 0.f, // top left
                x, y+height, 0.f, // bottom left
                x+width, y+height, 0.f, // bottom right
                x+width, y, 0.f // top right
        };
    }

    @Override
    protected void setupBuffers() {
        drawOrder = new short[]{
                0, 1, 2, 0, 2, 3
        };
        vertexCount = vertices.length / COORDS_PER_VERTEX;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        setupTextureBuffer();

        ByteBuffer drawBuffer = ByteBuffer.allocateDirect(drawOrder.length*2);
        drawBuffer.order(ByteOrder.nativeOrder());
        drawListBuffer = drawBuffer.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.imageVertexShader);
        int fragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.imageFragmentShader);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        setupBuffers = true;
    }

    protected void setupTextureBuffer() {
        final float[] textureCoordinateData = {
                u, v,
                u, v+textureHeight,
                u+textureWidth, v+textureHeight,
                u+textureWidth, v
        };

        ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length*4);
        textureByteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = textureByteBuffer.asFloatBuffer();
        textureBuffer.put(textureCoordinateData);
        textureBuffer.position(0);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(shaderProgram);

        if (!setupBuffers) {
            setupBuffers();
        }

        // Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureManager.images.get(resourceId));

        // Calculate the scratch matrix
        float scratch[] = new float[16];
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, modelMatrix, 0);

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        int textureCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "a_texCoord");
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratch, 0);

        int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "s_texture");
        GLES20.glUniform1i(textureHandle, 0);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
    }
}

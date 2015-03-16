package framework.graphics.textures;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import framework.graphics.GameGLRenderer;
import framework.graphics.tools.Shaders;
import framework.graphics.tools.VertexSet;

/**
 * Created by Morsecode Gaming on 2015-03-11.
 */
public class TextureAtlas extends TexturedShape {
    // New for textureAtlas
    protected int resourceId;
    private int numSprites = 0;

    // New buffers and handles
    protected FloatBuffer textureBuffer;
    protected VertexSet vertexSet = new VertexSet(12);
    protected VertexSet textureVertexSet = new VertexSet(8);

    public TextureAtlas(int resourceId) {
        this.resourceId = resourceId;
        this.x = 0;
        this.y = 0;
        this.width = GameGLRenderer.screenWidth;
        this.height = GameGLRenderer.screenHeight;

        rotationX = 0;
        rotationY = 0;

        initialize();
    }

    public int addTexture(float x, float y, float width, float height, float u, float v, float textureWidth, float textureHeight) {
        int index = addVertices(x, y, width, height);
        addTextureVertices(index, u, v, textureWidth, textureHeight);
        numSprites = vertexSet.getNumberOfSets();

        setupBuffers();

        return index;
    }

    private int addVertices(float x, float y, float width, float height) {
        int vertexIndex = vertexSet.addVertices(new float[]
                {
                        x, y, 0f,
                        x, y+height, 0f,
                        x+width, y+height, 0f,
                        x+width, y, 0f
                });
        vertexBuffer = vertexSet.getVertexBuffer();
        return vertexIndex;
    }

    private void addTextureVertices(int index, float u, float v, float textureWidth, float textureHeight) {
        textureVertexSet.addVertices(index, new float[]
                {
                        u, v,
                        u, v+textureHeight,
                        u+textureWidth, v+textureHeight,
                        u+textureWidth, v
                });
        textureBuffer = textureVertexSet.getVertexBuffer();
    }

    public void removeTexture(int index) {
        vertexSet.removeVertexSet(index);
        textureVertexSet.removeVertexSet(index);

        numSprites = vertexSet.getNumberOfSets();
        vertexBuffer = vertexSet.getVertexBuffer();
        textureBuffer = textureVertexSet.getVertexBuffer();
    }

    @Override
    protected void calculateVertices() {}

    @Override
    protected void setupBuffers() {
        drawOrder = new short[numSprites*6];
        for (int i = 0, spriteIndex = 0; i < numSprites; i++, spriteIndex++) {
            drawOrder[i*6] = (short) (spriteIndex*4);
            drawOrder[i*6+1] = (short) (spriteIndex*4+1);
            drawOrder[i*6+2] = (short) (spriteIndex*4+2);
            drawOrder[i*6+3] = (short) (spriteIndex*4);
            drawOrder[i*6+4] = (short) (spriteIndex*4+2);
            drawOrder[i*6+5] = (short) (spriteIndex*4+3);
        }

        ByteBuffer drawListByteBuffer = ByteBuffer.allocateDirect(drawOrder.length*2);
        drawListByteBuffer.order(ByteOrder.nativeOrder());
        drawListBuffer = drawListByteBuffer.asShortBuffer();
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

        if (color != null) {
            int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Color");
            GLES20.glUniform4fv(colorHandle, 1, color, 0);
            GLES20.glBlendColor(255, 255, 255, 1);
        }

        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratch, 0);

        int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "s_texture");
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
    }
}

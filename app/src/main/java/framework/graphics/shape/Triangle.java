package framework.graphics.shape;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import framework.graphics.GameGLRenderer;
import framework.graphics.tools.Shaders;

/**
 * Created by Morsecode Gaming on 2015-02-22.
 */
public class Triangle extends OpenGLShape {
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Triangle() {
    }

    public Triangle(int x, int y, int z) {
        vertices = new float[] {
                0.0f, 0.622008459f, 0.0f, // top
                -0.25f, -0.311004243f, 0.0f, // bottom left
                0.25f, -0.31104243f, 0.0f // bottom right
        };
        color = new float[] {
                0.6371875f, 0.76953125f, 0.22265625f, 1.f
        };
        vertexCount = vertices.length / COORDS_PER_VERTEX;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int vertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(shaderProgram);

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        transformationMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(transformationMatrixHandle, 1, false, mvpMatrix,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    @Override
    protected void calculateVertices() {

    }

    @Override
    protected void setupBuffers() {

    }
}

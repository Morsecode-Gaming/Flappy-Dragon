package framework.graphics.tools;

import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by Morsecode Gaming on 2015-03-13.
 */
public class VertexSet {
    private SparseArray<VertexSet> vertexSets = new SparseArray<>();
    private FloatBuffer vertexBuffer;
    private float[] vertices;
    private short numberOfSets = 0;
    private int vertexSize = 12;

    public VertexSet(){
        vertices = new float[0];
    }

    public VertexSet(int vertexSize) {
        this.vertexSize = vertexSize;
    }

    private VertexSet(float[] vertices) {
        this.vertices = vertices;
    }

    private void prepareBuffer() {
        vertices = new float[getVertexCount()];
        Log.d("TEXTURE", "Vertices: " + getVertexCount());
        for (int i = 0; i < vertexSets.size(); i++) {
            int index = vertexSets.keyAt(i);
            VertexSet vertexSet = vertexSets.get(index);
            System.arraycopy(vertexSet.vertices, 0, vertices, i * vertexSize, vertexSet.vertices.length);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    // Add vertices float array
    public int addVertices(float[] newVertices) {
        if (newVertices.length != 8 && newVertices.length != 12) {
            throw new RuntimeException("Attempting to add an incorrect set of vertices");
        }

        VertexSet vertexSet = new VertexSet(newVertices);
        return addVertices(vertexSet);
    }

    public void addVertices(int index, float[] newVertices) {
        if (newVertices.length != 8 && newVertices.length != 12) {
            throw new RuntimeException("Attempting to add an incorrect set of vertices");
        }

        VertexSet vertexSet = new VertexSet(newVertices);
        addVertices(index, vertexSet);
    }

    // Add vertices vertex set
    public int addVertices(VertexSet vertexSet) {
        numberOfSets++;
        int index = nextAvailableIndex();
        vertexSets.put(index, vertexSet);
        prepareBuffer();
        return index;
    }

    public void addVertices(int index, VertexSet vertexSet) {
        numberOfSets++;
        vertexSets.put(index, vertexSet);
        prepareBuffer();
    }

    // Remove vertex set
    public void removeVertexSet(int index) {
        if (vertexSets.indexOfKey(index) < 0) {
            Log.w("TEXTURES", "Attempting to remove a non-existent vertex set");
            return;
        }

        numberOfSets--;
        vertexSets.remove(index);
        prepareBuffer();
    }

    // Helpers
    public int nextAvailableIndex() {
        int index = -1;
        while (vertexSets.indexOfKey(++index) >= 0) {}
        Log.d("TEXTURES", "Next key is: " + index);
        return index;
    }

    // Getters
    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public int getVertexCount() {
        if (vertexSets.size() == 0) {
            return vertices.length;
        } else {
            int vertexCount = 0;
            for (int i = 0; i < vertexSets.size(); i++) {
                int key = vertexSets.keyAt(i);
                VertexSet vertexSet = vertexSets.get(key);
                vertexCount += vertexSet.getVertexCount();
            }
            return vertexCount;
        }
    }

    public float[] getVertices() {
        return vertices;
    }

    public short getNumberOfSets() {
        return numberOfSets;
    }
}

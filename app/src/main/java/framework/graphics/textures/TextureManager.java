package framework.graphics.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseIntArray;

import java.nio.IntBuffer;

/**
 * Created by Morsecode Gaming on 2015-03-12.
 */
public class TextureManager {
    private final int MAX_TEXTURES;

    private Context context;
    public int numTextures;
    public int[] textureNames;
    public SparseIntArray images = new SparseIntArray();

    private static TextureManager ourInstance = new TextureManager();
    public static TextureManager getInstance() {
        return ourInstance;
    }

    private TextureManager() {
        IntBuffer maxTexturesBuffer = IntBuffer.allocate(1);
        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, maxTexturesBuffer);
        MAX_TEXTURES = maxTexturesBuffer.get(0);
        Log.d("TEXTURE", "Max textures supported: " + MAX_TEXTURES);

        IntBuffer maxSizeBuffer = IntBuffer.allocate(1);
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSizeBuffer);
        Log.d("TEXTURE", "Max texture size supported: " + maxSizeBuffer.get(0));

        numTextures = 0;
        textureNames = new int[MAX_TEXTURES];

        GLES20.glGenTextures(MAX_TEXTURES, textureNames, 0);
    }

    public int loadImage(int resourceId) {
        int id = nextId();
        images.put(resourceId, id);

        // Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Set wrapping
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        return id;
    }

    private int nextId() {
        return numTextures++;
    }

    // Context
    public boolean hasContext() {
        return context != null;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

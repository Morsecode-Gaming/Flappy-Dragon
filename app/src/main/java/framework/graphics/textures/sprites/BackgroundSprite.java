package framework.graphics.textures.sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES20;

import com.morsecodegaming.flappydragongl.miscellaneous.Background;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import framework.utils.GraphicsUtils;

/**
 * Created by Morsecode Gaming on 2015-03-17.
 */
public class BackgroundSprite extends Sprite {
    private Point screenSize;
    private float u2, v2;

    public BackgroundSprite(Context context, float x, float y, float width, float height, int resourceId) {
        super();

        this.resourceId = resourceId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rotationX = 0;
        rotationY = 0;

        screenSize = GraphicsUtils.calculateScreenSize(context);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        u = 0.f;
        v = 0.f;
        textureWidth = Math.min(1.f, screenSize.x/((float) bitmap.getWidth()));
        textureHeight = Math.min(1.f, screenSize.y/((float) bitmap.getHeight()));
        u2 = u + textureWidth;
        v2 = v + textureHeight;

        initialize();
        calculateVertices();
    }

    @Override
    protected void setupTextureBuffer() {
        final float[] textureCoordinateData = {
                u, v,
                u, v2,
                u2, v2,
                u2, v
        };

        ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length*4);
        textureByteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = textureByteBuffer.asFloatBuffer();
        textureBuffer.put(textureCoordinateData);
        textureBuffer.position(0);
    }

    public void scrollSprite(float move) {
        u += (move/screenSize.x);
        if (u > 1.f) u-=2.f;

        u2 = u + textureWidth;

        setupBuffers = false;
    }
}

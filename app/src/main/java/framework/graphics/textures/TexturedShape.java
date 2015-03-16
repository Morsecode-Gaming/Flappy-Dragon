package framework.graphics.textures;

import android.content.Context;

import framework.graphics.shape.OpenGLShape;

/**
 * Created by Morsecode Gaming on 2015-03-12.
 */
public abstract class TexturedShape extends OpenGLShape {
    protected Context context;
    protected TextureManager textureManager = TextureManager.getInstance();
}

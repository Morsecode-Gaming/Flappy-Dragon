package com.morsecodegaming.flappydragongl.enemies;

import android.util.Log;

import com.morsecodegaming.flappydragongl.R;

import framework.GameSurfaceView;
import framework.graphics.GameGLRenderer;
import framework.graphics.textures.TextureAtlas;

/**
 * Created by Morsecode Gaming on 2015-03-14.
 */
public class EnemyAtlas extends TextureAtlas {
    private static EnemyAtlas ourInstance = new EnemyAtlas();

    public static EnemyAtlas getInstance() {
        return ourInstance;
    }

    private EnemyAtlas() {
        super(R.drawable.enemy_textures);
    }

    public int newCatapult(float x, float y, float width, float height) {
        int catapultIndex = addTexture(x, y, width, height, 0f, 0f, 0.5f, 0.5f);
        Log.d("ENEMIES", "New Catapult: " + catapultIndex);
        return catapultIndex;
    }

    public void removeCatapult(int index) {
        removeTexture(index);
    }
}

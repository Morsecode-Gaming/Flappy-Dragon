package framework.graphics.textures.text;

import android.util.Log;

import com.morsecodegaming.flappydragongl.R;

import org.w3c.dom.Text;

import framework.graphics.textures.TextureAtlas;
import framework.graphics.tools.VertexSet;

/**
 * Created by Morsecode Gaming on 2015-03-13.
 */
public class TextAtlas extends TextureAtlas {
    private static TextAtlas ourInstance = new TextAtlas();
    private final float TEXT_UV_BOX_SIZE = 0.125f;
    private final float TEXT_SIZE = 32f;
    private final float TEXT_SPACE_SIZE = 20f;
    private final float TEXT_SPACING = 4f;

    private static int[] charSize =
            {
                    36,29,30,34,25,25,34,33,
                    11,20,31,24,48,35,39,29,
                    42,31,27,31,34,35,46,35,
                    31,27,30,26,28,26,31,28,
                    28,28,29,29,14,24,30,18,
                    26,14,14,14,25,28,31,0,
                    0,38,39,12,36,34,0,0,
                    0,38,0,0,0,0,0,0
            };

    public static TextAtlas getInstance() { return ourInstance; }
    private TextAtlas() {
        super(R.drawable.font);
    }

    public void addChar(char c, float x, float y, float scale, int color) {
        setColor(color);

        int charIndex = convertCharToIndex(c);
        float charWidth = charSize[charIndex];
        float scaledCharWidth = charWidth*scale;
        float scaledSize = TEXT_SIZE*scale;

        int row = charIndex / 8;
        int col = charIndex % 8;
        float u = col * TEXT_UV_BOX_SIZE;
        float v = row * TEXT_UV_BOX_SIZE;
        addTexture(x, y, scaledCharWidth, scaledSize, u, v, 0.125f * (charWidth / 64f), 0.125f);
    }

    public int[] addText(String text, float x, float y, float scale, int color) {
//        setColor(color);
        int[] indexes = new int[text.length()];
        for (char c : text.toCharArray()) {
            int charIndex = convertCharToIndex(c);
            float charWidth = charSize[charIndex];
            float scaledCharWidth = charWidth*scale;
            float scaledSize = TEXT_SIZE*scale;

            int row = charIndex / 8;
            int col = charIndex % 8;
            float u = col * TEXT_UV_BOX_SIZE;
            float v = row * TEXT_UV_BOX_SIZE;
            indexes[text.indexOf(c)] = addTexture(x,y,scaledCharWidth, scaledSize,u,v,0.125f*(charWidth/64f),0.125f);

            x += scaledCharWidth + TEXT_SPACING;
        }
        return indexes;
    }

    private int convertCharToIndex(int charVal) {
        int index = -1;
        if (charVal > 64 && charVal < 91) { // A-Z
            index = charVal - 65;
        } else if (charVal > 96 && charVal < 123) { // a-z
            index = charVal - 97;
        } else if (charVal > 47 && charVal < 58) { // 0-9
            index = charVal - 48 + 26;
        } else if (charVal == 43) { // +
            index = 38;
        } else if (charVal == 45) { // -
            index = 39;
        } else if (charVal == 33) { // !
            index = 36;
        } else if (charVal == 63) { // ?
            index = 37;
        } else if (charVal == 61) { // =
            index = 40;
        } else if (charVal == 58) { // :
            index = 41;
        } else if (charVal == 46) { // .
            index = 42;
        } else if (charVal == 44) { // ,
            index = 43;
        } else if (charVal == 42) { // *
            index = 44;
        } else if (charVal == 36) { // $
            index = 45;
        }
        return index;
    }
}

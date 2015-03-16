package framework.utils;

/**
 * Created by Morsecode Gaming on 2015-02-27.
 */
public class MathUtils {
    public static float vectorLength(float v[]) {
        float length = 0;
        for (float f : v) {
            length += Math.pow(f,2);
        }
        return (float) Math.sqrt(length);
    }

    public static float[] convertToUnitVector(float v[]) {
        float unitV[] = {0f,0f,0f};
        float length = vectorLength(v);
        for (int i = 0; i < v.length; i++) {
            unitV[i] = v[i]/length;
        }
        return unitV;
    }

    public static float distanceBetweenPoints(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
    }
}

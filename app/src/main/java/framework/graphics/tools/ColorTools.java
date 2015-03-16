package framework.graphics.tools;

/**
 * Created by Morsecode Gaming on 2015-03-11.
 */
public class ColorTools {
    public static float[] colorIntToRGBA(int color) {
        String colorString = Integer.toHexString(color);

        String redString = colorString.substring(2,4);
        long redLong = Long.parseLong(redString, 16);
        float red = ((float) redLong)/255f;

        String greenString = colorString.substring(4, 6);
        Long greenLong = Long.parseLong(greenString, 16);
        float green = ((float) greenLong)/255f;

        String blueString = colorString.substring(6, 8);
        Long blueLong = Long.parseLong(blueString, 16);
        float blue = ((float) blueLong)/255f;

        String alphaString = colorString.substring(0, 2);
        Long alphaLong = Long.parseLong(alphaString, 16);
        float alpha = ((float) alphaLong)/255f;

        return new float[]{
                red, green, blue, alpha
        };
    }
}

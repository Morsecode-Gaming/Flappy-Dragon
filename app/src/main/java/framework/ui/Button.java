package framework.ui;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import framework.graphics.GameGLRenderer;
import framework.graphics.shape.OpenGLShape;
import framework.graphics.shape.Square;
import framework.graphics.textures.text.TextAtlas;

/**
 * Created by Morsecode Gaming on 2015-03-09.
 */
public abstract class Button {
    protected OpenGLShape shape;
    protected boolean pressed;
    protected int x, y, width, height;
    protected String text;
    private int[] textIndices = new int[0];
    private int color, pressedColor, textColor;

    public Button(int x, int y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;

        pressedColor = Color.rgb(Color.red(color)/2, Color.green(color)/2, Color.blue(color)/2);
    }

    public Button(String text, int x, int y, int width, int height, int color, int textColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.textColor = textColor;

        setText(text);

        pressedColor = Color.rgb(Color.red(color) / 2, Color.green(color) / 2, Color.blue(color) / 2);
    }

    public void generateShape() {
        shape = new Square(x, y, width, height, color);
    };

    public boolean checkClick(float clickX, float clickY) {
        return shape.isVisible() && (((clickX > shape.getX()) && (clickX < (shape.getX()+shape.getWidth()))) && ((clickY > shape.getY()) && (clickY < (shape.getY()+shape.getHeight()))));
    }

    public boolean checkClick(PointF point) {
        return (checkClick(point.x, point.y));
    }

    public boolean checkClick(MotionEvent event) {
        return (checkClick(event.getX(), event.getY()));
    }

    public abstract void click();

    public void press() {
        pressed = true;
        shape.setColor(pressedColor);
    }

    public void release() {
        pressed = false;
        shape.setColor(color);
    }

    public void hide() {
        shape.hide();
        hideText();
    }

    private void hideText() {

    }

    public void show() {
        shape.show();
        setText(text);
    }

    public boolean isPressed() {
        return pressed;
    }

    public OpenGLShape getShape() {
        return shape;
    }

    public void setText(String text) {
        if (this.text != null && this.text.equals(text)) {
            Log.w("BUTTON", "That text is the same as the text already set");
            return;
        }

        textIndices = TextAtlas.getInstance().addText(text, x, y, 1.25f, textColor);
        this.text = text;
    }

    public int[] getTextIndices() {
        return textIndices;
    }
}

package framework;

import framework.graphics.shape.OpenGLShape;
import framework.physics.PhysicsObject;
import framework.utils.Line;

/**
 * Created by Morsecode Gaming on 2015-02-27.
 */
public abstract class GameObject extends PhysicsObject {
    protected OpenGLShape shape;
    protected boolean scrollsWithBackground = false;

    // Constructors
    public GameObject() {
        super();
    }

    // Shape Methods
    protected abstract void generateShape();

    // Setters
    public void hide() {
        shape.hide();
    }

    public void show() {
        shape.show();
    }

    // Getters
    public OpenGLShape getShape() {
        return shape;
    }

    public Line getMovementLine() {
        float position[] = shape.getTransformedVector();
        return new Line(position[0], position[1], position[0]+velocity[0], position[1]+velocity[1]);
    }

    public boolean doesScrollWithBackground() {
        return scrollsWithBackground;
    }
}

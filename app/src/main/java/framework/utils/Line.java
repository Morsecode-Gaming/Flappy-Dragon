package framework.utils;

import framework.graphics.shape.OpenGLShape;

/**
 * Created by Morsecode Gaming on 2015-03-05.
 */
public class Line {
    float x1, y1;
    float x2, y2;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public float length() {
        return (float) (Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2)));
    }

    public boolean intersects(Line line) {
        float x1 = this.x1;
        float y1 = this.y1;
        float x2 = this.x2;
        float y2 = this.y2;
        float x3 = line.getX1();
        float y3 = line.getY1();
        float x4 = line.getX2();
        float y4 = line.getY2();

        x2 -= x1;
        y2 -= y1;
        x3 -= x1;
        y3 -= y1;
        x4 -= x1;
        y4 -= y1;

        float AvB = x2 * y3 - x3 * y2;
        float AvC = x2 * y4 - x4 * y2;
        if (AvB == 0.0 && AvC == 0.0) {
            if (x2 != 0.0) {
                return (x4 * x3 <= 0.0)
                        || ((x3 * x2 >= 0.0) && (x2 > 0.0 ? x3 <= x2 || x4 <= x2 : x3 >= x2
                        || x4 >= x2));
            }
            if (y2 != 0.0) {
                return (y4 * y3 <= 0.0)
                        || ((y3 * y2 >= 0.0) && (y2 > 0.0 ? y3 <= y2 || y4 <= y2 : y3 >= y2
                        || y4 >= y2));
            }
            return false;
        }
        float BvC = x3 * y4 - x4 * y3;

        return (AvB * AvC <= 0.0) && (BvC * (AvB + BvC - AvC) <= 0.0);
    }

    public boolean intersects(OpenGLShape shape) {
        for (Line edge : shape.getEdges()) {
            if (intersects(edge)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWithin(OpenGLShape shape) {
        int edgesHit = 0;
        for (Line edge : shape.getEdges()) {
            if (intersects(edge)) {
                edgesHit++;
            }
        }
        return edgesHit % 2 == 1;
    }

    // Getters
    public float getX1() {
        return x1;
    }

    public float getX2() {
        return x2;
    }

    public float getY1() {
        return y1;
    }

    public float getY2() {
        return y2;
    }
}

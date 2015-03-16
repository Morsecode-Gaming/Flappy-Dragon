package framework.physics.forces;

import framework.utils.MathUtils;

/**
 * Created by Morsecode Gaming on 2015-02-27.
 */
public class Force {
    private float magnitude;
    private float directionVector[];

    public boolean isTimed = false;
    public boolean expired = false;
    private float duration;
    private long startTime;

    // Constructors
    public Force() {
        magnitude = 0;
        directionVector = new float[] {0f,0f,0f};
    }

    public Force(float magnitude, float directionVector[]) {
        this.magnitude = magnitude;

        if (MathUtils.vectorLength(directionVector) != 1) {
            directionVector = MathUtils.convertToUnitVector(directionVector);
        }
        this.directionVector = directionVector;
    }

    public Force(float magnitude, float directionVector[], long duration) {
        this.magnitude = magnitude;
        this.duration = duration;

        isTimed = true;
        startTime = System.currentTimeMillis();

        if (MathUtils.vectorLength(directionVector) != 1) {
            this.directionVector = MathUtils.convertToUnitVector(directionVector);
        } else {
            this.directionVector = directionVector;
        }
    }

    // Getters
    public float getMagnitude() {
        return magnitude;
    }

    public float[] getDirectionVector() {
        return directionVector;
    }

    public void updateTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > duration) {
            expired = true;
        }
    }
}
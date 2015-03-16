package framework.physics;

import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-03-03.
 */
public class PhysicsConstants {
    public static float gravityStrength(float mass) {
        return 9.81f*mass;
    }

    public static Force gravity(float mass) {
        return new Force(mass*9.81f, new float[]{0f, 1f, 0f});
    }
}

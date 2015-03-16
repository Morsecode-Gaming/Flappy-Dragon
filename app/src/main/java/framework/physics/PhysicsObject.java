package framework.physics;

import java.util.ArrayList;

import framework.physics.forces.Force;

/**
 * Created by Morsecode Gaming on 2015-02-27.
 */
public class PhysicsObject {
    protected float[] acceleration = new float[] {0f,0f,0f};
    protected float[] velocity = new float[] {0f,0f,0f};
    protected float mass = 1f;

    ArrayList<Force> forces = new ArrayList<>();

    // Constructors
    public PhysicsObject() {
        applyForce(PhysicsConstants.gravity(mass));
    }

    // Forces
    public void applyForce(Force force) {
        forces.add(force);
        calculateAcceleration();
    }

    protected void setNotAffectedByGravity() {
        Force normalForce = new Force(PhysicsConstants.gravityStrength(mass), new float[]{0f,-1f,0f});
        applyForce(normalForce);
    };

    // Movement
    public void calculateAcceleration() {
        float netForce[] = {0f,0f,0f};
        for (Force force : forces) {
            netForce[0] += force.getMagnitude()*force.getDirectionVector()[0];
            netForce[1] += force.getMagnitude()*force.getDirectionVector()[1];
            netForce[2] += force.getMagnitude()*force.getDirectionVector()[2];
        }

        acceleration[0] = netForce[0];
        acceleration[1] = netForce[1];
        acceleration[2] = netForce[2];
    }

    public void calculateVelocity() {
        for (int i = 0; i < forces.size(); i++) {
            Force force = forces.get(i);
            if (force.isTimed) {
                force.updateTime();
                if (force.expired) {
                    forces.remove(force);
                    calculateAcceleration();
                }
            }
        }
        velocity[0] += acceleration[0]/100f;
        velocity[1] += acceleration[1]/100f;
        velocity[2] += acceleration[2]/100f;
    }

    // Getters
    public float[] getVelocity() {
        return velocity;
    }
}

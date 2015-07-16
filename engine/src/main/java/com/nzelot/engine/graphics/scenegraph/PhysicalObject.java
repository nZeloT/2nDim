/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 nZeloT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nzelot.engine.graphics.scenegraph;

import com.nzelot.engine.utils.Constants;
import lombok.experimental.Delegate;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.*;
import org.joml.Matrix4f;

/**
 * @author nZeloT
 */
//todo add doc
public abstract class PhysicalObject extends Object{

    private final Matrix4f mT;
    private final Matrix4f mS;
    private final Matrix4f mR;


    private @Delegate(types = BodyDelegates.class) Body physicalBody;
    private AABB aabb;

    /**
     * the constructor
     *
     * @param name the name of the entity; can be used to find the entity in the SceneGraph
     */
    PhysicalObject(String name) {
        super(name);
        physicalBody = new Body();

        mR = new Matrix4f();
        mT = new Matrix4f();
        mS = new Matrix4f();
    }

    @Override
    public Vector2 getTranslation() {
        return physicalBody.getTransform().getTranslation();
    }

    @Override
    public void setTranslation(Vector2 pos) {
        physicalBody.getTransform().setTranslation(pos);
    }

    @Override
    public double getRotation() {
        return physicalBody.getTransform().getRotation();
    }

    @Override
    public void setRotation(double rad) {
        physicalBody.getTransform().setRotation(rad);
    }

    @Override
    void updateMatrix() {
        Transform transform = physicalBody.getTransform();
        transMat.identity();

        mR.identity();
        mR.rotateZ((float) Math.toDegrees(transform.getRotation()));

        mT.identity();
        mT.translate((float) transform.getTranslationX(), (float) transform.getTranslationY(), 0);

        mS.identity();
        mS.scale((float) (aabb.getWidth() * Constants.PHY_SCALE), (float) (aabb.getHeight() * Constants.PHY_SCALE), 1);

        mR.mul(mS, transMat);
        mT.mul(transMat, transMat);
    }

    public void rotateAtMassCenter(double rad){
        physicalBody.rotateAboutCenter(rad);
    }

    public void addWeldJoint(PhysicalObject target, Vector2 anchor){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            w.addJoint(new WeldJoint(physicalBody, target.getBody(), anchor));
        }
    }

    protected boolean isPartOfWorld(){
        return physicalBody.getWorld() != null;
    }

    Body getBody(){
        return physicalBody;
    }

    protected BodyFixture addFixture(Convex convex) {
        BodyFixture fix =  physicalBody.addFixture(convex);
        aabb = physicalBody.createAABB();
        return fix;
    }

    protected BodyFixture addFixture(Convex convex, double density) {
        BodyFixture fix = physicalBody.addFixture(convex, density);
        aabb = physicalBody.createAABB();
        return fix;
    }

    protected BodyFixture addFixture(Convex convex, double density, double friction, double restitution) {
        BodyFixture fix =  physicalBody.addFixture(convex, density, friction, restitution);
        aabb = physicalBody.createAABB();
        return fix;
    }

    protected Body addFixture(BodyFixture fixture) {
        Body b = physicalBody.addFixture(fixture);
        aabb = physicalBody.createAABB();
        return b;
    }

    //todo add doc
    private interface BodyDelegates {
        Body setMass();
        Body setMass(Mass.Type type);
        Body setMass(Mass mass);
        Body setMassType(Mass.Type type);
        Mass getMass();

        Body applyForce(Vector2 force);
        Body applyForce(Force force);
        Body applyTorque(double torque);
        Body applyTorque(Torque torque);
        Body applyForce(Vector2 force, Vector2 point);
        Body applyImpulse(Vector2 impulse);
        Body applyImpulse(double impulse);
        Body applyImpulse(Vector2 impulse, Vector2 point);
        void clearForce();
        void clearTorque();
        Vector2 getForce();
        double getTorque();

        boolean isStatic();
        boolean isKinematic();
        boolean isDynamic();

        void setAutoSleepingEnabled(boolean flag);
        boolean isAutoSleepingEnabled();

        boolean isAsleep();
        void setAsleep(boolean flag);

        boolean isActive();
        void setActive(boolean flag);

        boolean isBullet();
        void setBullet(boolean flag);

        void rotate(double theta, double x, double y);
        void rotate(double theta, Vector2 point);
        void rotate(double theta);

        void translate(double x, double y);
        void translate(Vector2 vector);
        void translateToOrigin();

        Vector2 getLocalCenter();

        void setLinearVelocity(Vector2 velocity);
        void setLinearVelocity(double x, double y);
        double getAngularVelocity();
        void setAngularVelocity(double angularVelocity);
    }
}

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


import com.nzelot.engine.graphics.rendering.Shader;
import com.nzelot.engine.graphics.rendering.VertexArray;
import com.nzelot.engine.utils.Constants;
import lombok.*;
import lombok.experimental.Delegate;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.joint.*;
import org.dyn4j.geometry.*;
import org.joml.Matrix4f;

/**
 * The Entity Class represents the most basic Object within the whole Scenegraph.
 * It implements parent/child graph relations.
 *
 * the scenegraph will build up as tree structure
 *
 * @author nZeloT
 */
public abstract class GameObject {

    private @Getter String name;

    private @Getter int zIndex;

    protected @Getter Matrix4f transMat;

    private @Getter Universe universe;

    private @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) Shader shader;
    private @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) VertexArray geometry;

    /**
     * the constructor
     *
     * @param name the name of the entity; can be used to find the entity in the SceneGraph
     */
    GameObject(String name, Shader shad, VertexArray geo) {
        this.zIndex     = 0;
        this.transMat = new Matrix4f().identity();

        setName(name);
        setGeometry(geo);
        setShader(shad);
        physicalBody = new Body();
    }

    /**
     * update the entity a given delta time
     *
     * @param delta the time gone since the last update
     */
    public abstract void update(double delta);

    /**
     * render the current entity. Rendering of child entities is done for ya
     *
     * @param transformation the transformation matrix to pass to the shader; it was calculated according to the current position, rotation and scale
     */
    public void render(Matrix4f transformation){
        shader.setUniformMat4f("mv_matrix", transMat);
        shader.setUniformMat4f("cm_matrix", getUniverse().getCameraMat());
        shader.setUniformMat4f("pr_matrix", getUniverse().getProjectionMat());

        shader.bind();
        geometry.bind();

        geometry.render();
    }

    //todo add doc
    protected abstract void onAddToUniverse();
    protected abstract void onRemoveFromUniverse();

    //todo: add doc
    public void setName(String name) {
        this.name       = name;

        if(name == null){
            this.name = toString();
        }
    }

    //todo add doc
    public void setZIndex(int newZIndex){
        this.zIndex = newZIndex;
        universe.zIndexChanged();
    }

    /**
     * update the entity and afterwards update all child entities
     * followed by a reordering of the children based on their new z values if necessary
     * and finally a recalculation of the transformation matrix if necessary
     *
     * @param delta the delta time to update
     */
    public void updateWrap(double delta) {
        update(delta);
        updateMatrix();
    }

    //todo add doc
    public void renderWrap() {
        this.render(transMat);
    }

    //todo add doc
    protected void setUniverse(Universe universe) {
        this.universe = universe;
    }

    private @Delegate(types = BodyDelegates.class)
    Body physicalBody;
    private AABB aabb;

    //doc
    public Vector2 getTranslation() {
        return physicalBody.getTransform().getTranslation();
    }

    //doc
    public void setTranslation(@NonNull Vector2 pos) {
        physicalBody.getTransform().setTranslation(pos);
    }

    //doc
    public double getRotation() {
        return physicalBody.getTransform().getRotation();
    }

    //doc
    public void setRotation(double rad) {
        physicalBody.getTransform().setRotation(rad);
    }

    //doc
    void updateMatrix() {
        Transform transform = physicalBody.getTransform();
        transMat.identity();

        //the order is correct because of the way joml calculates the new matrix
        transMat.translate((float) transform.getTranslationX(), (float) transform.getTranslationY(), 0);
        transMat.rotateZ((float) transform.getRotation());
        transMat.scale((float) (aabb.getWidth() * Constants.PHY_SCALE), (float) (aabb.getHeight() * Constants.PHY_SCALE), 1);
    }

    //doc
    public void rotateAtMassCenter(double rad){
        physicalBody.rotateAboutCenter(rad);
    }

    //doc
    public WeldJoint addWeldJoint(@NonNull GameObject target, @NonNull Vector2 anchor){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            WeldJoint j = new WeldJoint(physicalBody, target.getBody(), anchor);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public DistanceJoint addDistanceJoint(@NonNull GameObject target, @NonNull Vector2 anchor1, @NonNull Vector2 anchor2){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            DistanceJoint j = new DistanceJoint(physicalBody, target.getBody(), anchor1, anchor2);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public RevoluteJoint addRevoluteJoint(@NonNull GameObject target, @NonNull Vector2 anchor){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            RevoluteJoint j = new RevoluteJoint(physicalBody, target.getBody(), anchor);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public PrismaticJoint addPrismaticJoint(@NonNull GameObject target, @NonNull Vector2 anchor, @NonNull Vector2 axis){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            PrismaticJoint j = new PrismaticJoint(physicalBody, target.getBody(), anchor, axis);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public PulleyJoint addPulleyJoint(@NonNull GameObject target, @NonNull Vector2 anchor1, @NonNull Vector2 anchor2,
                                      @NonNull Vector2 bodyAnchor1, @NonNull Vector2 bodyAnchor2){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            PulleyJoint j = new PulleyJoint(physicalBody, target.getBody(), anchor1, anchor2, bodyAnchor1, bodyAnchor2);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public RopeJoint addRopeJoint(@NonNull GameObject target, @NonNull Vector2 anchor1, @NonNull Vector2 anchor2){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            RopeJoint j = new RopeJoint(physicalBody, target.getBody(), anchor1, anchor2);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    public WheelJoint addWheelJoint(@NonNull GameObject wheel, @NonNull Vector2 anchor, @NonNull Vector2 axis){
        if(isPartOfWorld()){
            World w = physicalBody.getWorld();
            WheelJoint j = new WheelJoint(physicalBody, wheel.getBody(), anchor, axis);
            w.addJoint(j);
            return j;
        }

        return null;
    }

    //doc
    protected boolean isPartOfWorld(){
        return physicalBody.getWorld() != null;
    }

    //doc
    Body getBody(){
        return physicalBody;
    }

    //doc
    BodyFixture addFixture(@NonNull Convex convex) {
        BodyFixture fix =  physicalBody.addFixture(convex);
        aabb = physicalBody.createAABB();
        return fix;
    }

    //doc
    BodyFixture addFixture(@NonNull Convex convex, double density) {
        BodyFixture fix = physicalBody.addFixture(convex, density);
        aabb = physicalBody.createAABB();
        return fix;
    }

    //doc
    BodyFixture addFixture(@NonNull Convex convex, double density, double friction, double restitution) {
        BodyFixture fix =  physicalBody.addFixture(convex, density, friction, restitution);
        aabb = physicalBody.createAABB();
        return fix;
    }

    //doc
    Body addFixture(@NonNull BodyFixture fixture) {
        Body b = physicalBody.addFixture(fixture);
        aabb = physicalBody.createAABB();
        return b;
    }

    //doc
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

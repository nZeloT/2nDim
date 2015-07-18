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


import lombok.Getter;
import org.dyn4j.geometry.Vector2;
import org.joml.Matrix4f;

/**
 * The Entity Class represents the most basic Object within the whole Scenegraph.
 * It implements parent/child graph relations.
 *
 * the scenegraph will build up as tree structure
 *
 * @author nZeloT
 */
public abstract class Object {

    private static final String CLASS_NAME = Object.class.getName();

    private @Getter String name;

    private @Getter int zIndex;

    protected @Getter Matrix4f transMat;

    private @Getter Universe universe;

    /**
     * the constructor
     *
     * @param name the name of the entity; can be used to find the entity in the SceneGraph
     */
    Object(String name) {
        this.zIndex     = 0;
        this.transMat = new Matrix4f().identity();

        setName(name);
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
    public abstract void render(Matrix4f transformation);

    //todo add doc
    public abstract Vector2 getTranslation();
    public abstract void setTranslation(Vector2 pos);

    public abstract double getRotation();
    public abstract void setRotation(double rad);

    public abstract void rotate(double rad);
    public abstract void rotate(double rad, Vector2 axis);
    public abstract void translate(Vector2 delta);
    public abstract void translate(double x, double y);

    //todo add doc
    abstract void updateMatrix();
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
    void setUniverse(Universe universe) {
        this.universe = universe;
    }
}

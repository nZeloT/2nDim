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


import org.joml.Vector3f;

import java.util.List;
import java.util.Vector;

/**
 * The Entity Class represents the most basic Object within the whole Scenegraph.
 * It implements positioning and parent/child graph relations.
 */
public abstract class Entity {

    private static final Vector3f dummy = new Vector3f();

    private final Vector3f position;
    private final List<Entity> children;
    private final Vector3f absolutePosition;
    private Entity parent;
    private boolean changed;


    /**
     * A basic Constructor. Leaving the parent field as null.
     *
     * @param position the initial Position of the new Entity. Be Aware that the positioning mode is ABSOLUTE at the beginning.
     */
    public Entity(Vector3f position) {
        this(position, null);
    }

    /**
     * An alternative Constructor, which also allows you to set the parent field.
     *
     * @param pos the initial position, relative to the parent. positioning mode will be set to RELATIVE at the beginning.
     * @param parent the parent Entity
     */
    public Entity(Vector3f pos, Entity parent) {

        this.position = new Vector3f(pos);
        this.absolutePosition = new Vector3f();

        this.parent = parent;

        this.children = new Vector<>();

        this.changed = true;
    }

    /**
     * update the entity a given delta time
     *
     * @param delta the time gone since the last update
     */
    public abstract void update(double delta);

    /**
     * render the current entity. Rendering of child entities is done for ya
     */
    public abstract void render();

    public Entity getParent() {
        return parent;
    }

    /**
     * Set a new parent entity. Be aware, that the relative position to the old parent will remain, but will be held to the new parent.
     *
     * @param parent the new parent entity
     */
    public void setParent(Entity parent) {
        setParent(parent, false);
    }

    public int getChildrenCount() {
        return children.size();
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
     *
     * @param entity the entity to add
     */
    public void addChild(Entity entity) {
        addChild(entity, false);
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
     *
     * @param entity       the entity to add
     * @param keepPosition leaves control about the keepPosition flag from setParent() to you
     */
    public void addChild(Entity entity, boolean keepPosition) {
        entity.setParent(this, keepPosition);
        children.add(entity);
    }

    /**
     * Set a new parent entity.
     *
     * @param parent       the new parent entity
     * @param keepPosition if set true, the entity will remain at its current position and not move to its new relative position according to its new parent
     */
    public void setParent(Entity parent, boolean keepPosition) {

        this.parent = parent;

        if (parent != null) {

            if (keepPosition) {
                Vector3f parentPos = parent.getPosition();

                position.x = position.x - parentPos.x;
                position.y = position.y - parentPos.y;

                changed = true;
            }

        }
    }

    /**
     * @return the relative position
     */
    public Vector3f getPosition() {
        return position;
    }

    public void removeChild(Entity o) {
        o.setParent(null);
        children.remove(o);
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
     *
     * @param index the child`s index which is to be removed
     * @return the removed entity
     */
    public Entity removeChild(int index) {
        Entity e = children.remove(index);
        e.setParent(null);
        return e;
    }

    public Entity getChild(int index) {
        return children.get(index);
    }

    public int indexOfChild(Entity e) {
        return children.indexOf(e);
    }

    /**
     * remove all child entities. the setParent() method will be called with parameter "null"
     */
    public void clearChildren() {

        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParent(null);
        }

        children.clear();
    }

    public Vector3f getAbsolutePosition() {
        if (changed) {
            Vector3f pp = Entity.dummy;
            if (parent != null)
                pp = parent.getAbsolutePosition();

            absolutePosition.x = position.x + pp.x;
            absolutePosition.y = position.y + pp.y;

            changed = false;
        }
        return absolutePosition;
    }

    /**
     * move the entity a certain amount of units
     *
     * @param delta the amount of units to move relative to the parent
     */
    public void move(Vector3f delta) {
        position.x += delta.x;
        position.y += delta.y;

        changed = true;

        moveChildren(delta);
    }

    /**
     * move the entity to a new position
     *
     * @param newPos the new position, relative to its parent or (0,0)
     */
    public void moveToPoint(Vector3f newPos) {
        Vector3f pp = Entity.dummy;
        if (parent != null)
            pp = parent.getAbsolutePosition();

        Vector3f delta = new Vector3f(position);

        position.x = pp.x + newPos.x;
        position.y = pp.y + newPos.y;

        delta.x = position.x - delta.x;
        delta.y = position.y - delta.y;

        changed = true;

        moveChildren(delta);
    }

    private void moveChildren(Vector3f delta) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).move(delta);
        }
    }

    public void renderAll() {

        this.render();

        int n = children.size();
        for (int i = 0; i < n; ++i) {
            children.get(i).renderAll();
        }
    }

    public void updateAll(double delta) {

        this.update(delta);

        int n = children.size();
        for (int i = 0; i < n; ++i) {
            children.get(i).updateAll(delta);
        }
    }
}

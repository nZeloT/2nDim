package com.nzelot.engine.graphics;

import com.nzelot.engine.graphics.rendering.Renderer;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

/**
 * The Entity Class represents the most basic Object within the whole Scenegraph.
 * It implements positioning and parent/child graph relations.
 */
public abstract class Entity {

    private Point[] relativePos;

    private Point[] absolutePos;

    private Point size;

    private Entity parent;
    private List<Entity> children;

    private boolean visible;
    private boolean resize;

    private PositionMode positionMode;


    /**
     * update the entity a given delta time
     * @param delta the time gone since the last update
     */
    public abstract void update(long delta);

    /**
     * Doe render the current entity. Rendering of child entities is done for ya
     * @param r the renderer used for rendering
     */
    public abstract void render(Renderer r);


    /**
     * A basic Constructor. Leaving the parent field as null.
     * @param position the initial Position of the new Entity. Be Aware that the positioning mode is PM_ABSOLUTE at the beginning.
     * @param size the initial Size of the new Entity.
     */
    public Entity(Point position, Point size) {
        this(position, size, null);
    }

    /**
     * An alternative Constructor, which also allows you to set the parent field.
     * @param relPos the initial position, relative to the parent. positioning mode will be set to PM_RELATIVE at the beginning.
     * @param size the initial Size of the new Entity.
     * @param parent the parent Entity
     */
    public Entity(Point relPos, Point size, Entity parent) {

        this.relativePos = new Point[2];
        this.relativePos[0] = relPos;
        this.relativePos[1] = size;
        this.relativePos[1].x += relPos.x;
        this.relativePos[1].y += relPos.y;

        this.absolutePos = new Point[2];
        this.absolutePos[0] = new Point();
        this.absolutePos[1] = new Point();

        if (parent != null)
            this.positionMode = PositionMode.PM_RELATIVE;
        else
            this.positionMode = PositionMode.PM_ABSOLUTE;

        this.parent = parent;

        this.children = new Vector<>();

        this.visible = true;

        this.resize = true;
        this.size = getSize();

        updatePositionData();
    }


    public Entity getParent() {
        return parent;
    }

    /**
     * Set a new parent entity. Be aware, that the relative position to the old parent will remain, but will be held to the new parent.
     * @param parent the new parent entity
     */
    public void setParent(Entity parent){
        setParent(parent, false);
    }

    /**
     * Set a new parent entity.
     * @param parent the new parent entity
     * @param keepPosition if set true, the entity will remain at its current position and not move to its new relative position according to its new parent
     */
    public void setParent(Entity parent, boolean keepPosition) {

        this.parent = parent;

        if (parent != null) {

            positionMode = PositionMode.PM_RELATIVE;

            if(keepPosition) {
                getSize();

                Point parentPos = parent.getPosition(PositionMode.PM_ABSOLUTE);

                relativePos[0].x = absolutePos[0].x - parentPos.x;
                relativePos[0].y = absolutePos[0].y - parentPos.y;

                relativePos[1].x = relativePos[0].x + size.x;
                relativePos[1].y = relativePos[1].y + size.y;
            }

            updatePositionData();
        } else
            positionMode = PositionMode.PM_ABSOLUTE;
    }

    public int getChildrenCount() {
        return children.size();
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
     * @param entity the entity to add
     */
    public void addChild(Entity entity) {
        addChild(entity, false);
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
     * @param entity the entity to add
     * @param keepPosition leaves control about the keepPosition flag from setParent() to you
     */
    public void addChild(Entity entity, boolean keepPosition){
        entity.setParent(this, keepPosition);
        children.add(entity);
    }

    public void removeChild(Entity o) {
        o.setParent(null);
        children.remove(o);
    }

    /**
     * add a a new child to the entity. the parent field of the child will be set through the setParent() Method.
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

    /**
     * @return the position of the top-left corner in the current position mode
     */
    public Point getPosition() {
        return getPosition(this.positionMode);
    }

    /**
     * @param mode the point to which the position should be relative to
     * @return the position of the top-left corner in the defined position mode
     */
    public Point getPosition(PositionMode mode) {

        if (mode != this.positionMode)
            updatePositionData();

        return mode == PositionMode.PM_RELATIVE ? relativePos[0] : absolutePos[0];
    }

    /**
     * move the entity a certain amount of units
     * @param delta the amount of units to move
     */
    public void move(Point delta) {
        Point[] points = getPositionArray();

        for (int i = 0; i < 2; i++) {
            points[i].x += delta.x;
            points[i].y += delta.y;
        }

        updatePositionData();
    }

    /**
     * move the entity to a new position
     * @param newPos the new position, either relative or absolute depending on the objects current positioning mode
     */
    public void moveToPoint(Point newPos) {
        Point size = getSize();
        Point[] points = getPositionArray();

        Point pp = new Point();
        if (parent != null)
            pp = parent.getPosition();

        points[0].x = pp.x + newPos.x;
        points[0].y = pp.y + newPos.y;

        points[1].x = points[0].x + size.x;
        points[1].y = points[1].y + size.y;

        updatePositionData();
    }


    public Point getSize() {

        if (resize) {
            Point[] p = getPositionArray();

            size.x = p[1].x - p[0].x;
            size.y = p[1].y - p[0].y;

            resize = false;
        }

        return size;
    }

    /**
     * @param newSize the new size
     */
    public void resize(Point newSize) {

        Point[] points = getPositionArray();
        points[1].x = points[0].x + newSize.x;
        points[1].y = points[0].y + newSize.y;

        resize = true;

        updatePositionData();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public PositionMode getPositionMode() {
        return positionMode;
    }

    public void setPositionMode(PositionMode positionMode) {
        updatePositionData();
        this.positionMode = positionMode;
    }

    private void updatePositionData() {

        Point pp = new Point();
        if (parent != null)
            pp = parent.getPosition(PositionMode.PM_ABSOLUTE);

        if (positionMode == PositionMode.PM_ABSOLUTE) {

            relativePos[0].x = absolutePos[0].x - pp.x;
            relativePos[0].y = absolutePos[0].y - pp.y;

            relativePos[1].x = relativePos[0].x + absolutePos[1].x - absolutePos[0].x;
            relativePos[1].y = relativePos[0].y + absolutePos[1].y - absolutePos[0].y;

        } else {

            absolutePos[0].x = pp.x + relativePos[0].x;
            absolutePos[0].y = pp.y + relativePos[0].y;

            absolutePos[1].x = absolutePos[0].x + relativePos[1].x - relativePos[0].x;
            absolutePos[1].y = absolutePos[0].y + relativePos[1].y - relativePos[0].y;

        }
    }

    private Point[] getPositionArray() {
        return this.positionMode == PositionMode.PM_RELATIVE ? relativePos : absolutePos;
    }

    public void renderAll(Renderer r){

        this.render(r);

        int n = children.size();
        for (int i = 0; i < n; ++i) {
            children.get(i).renderAll( r );
        }
    }

    public void updateAll(long delta){

        this.update(delta);

        int n = children.size();
        for (int i = 0; i < n; ++i) {
            children.get(i).updateAll(delta);
        }
    }
}

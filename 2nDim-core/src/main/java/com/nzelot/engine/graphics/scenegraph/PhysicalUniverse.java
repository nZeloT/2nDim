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

import com.nzelot.engine.game.Game;
import lombok.NonNull;
import lombok.experimental.Delegate;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

/**
 * @author nZeloT
 */
//doc
public class PhysicalUniverse extends Universe{

    private @Delegate(types = WorldDelegates.class) World physics;

    //doc
    public PhysicalUniverse(@NonNull Game game) {
        super(game);

        physics = new World();
    }

    //doc
    @Override
    public void addObject(@NonNull GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.setUniverse(this);
        if(gameObject instanceof PhysicalObject)
            physics.addBody(((PhysicalObject) gameObject).getBody());

        gameObject.onAddToUniverse();
    }

    //doc
    @Override
    public void removeObject(@NonNull GameObject gameObject){
        gameObject.onRemoveFromUniverse();

        gameObjects.remove(gameObject);
        gameObject.setUniverse(null);
        if(gameObject instanceof PhysicalObject)
            physics.removeBody(((PhysicalObject) gameObject).getBody());
    }

    //doc
    @Override
    public void update(double delta) {
        physics.update(delta);

        super.update(delta);
    }

    //doc
    private interface WorldDelegates {
        boolean removeJoint(Joint joint);
        void removeAllJoints(boolean notify);

        Settings getSettings();
        void setSettings(Settings settings);

        void setGravity(Vector2 gravity);
        Vector2 getGravity();

        void setBounds(Bounds bounds);
        Bounds getBounds();
    }
}

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
import com.nzelot.engine.graphics.rendering.Texture;
import com.nzelot.engine.graphics.rendering.VertexArray;
import lombok.Getter;
import lombok.NonNull;
import org.dyn4j.dynamics.World;
import org.joml.Matrix4f;

import java.util.Vector;

/**
 * The root entity of the scene graph. it has no parent and holds the physics world but is not part of it
 *
 * @author nZeloT
 * @implNote the <code>render()</code>-Method calls the <code>unbind()</code>-Methods of
 * <code>VertexArray, Texture</code> and <code>Shader</code> classes
 */
public class Universe {

    private static final String CLASS_NAME = Universe.class.getName();

    private Vector<Object> gameObjects;
    private @Getter Matrix4f projMat;

    private World physics;
    private boolean physicsActive;

    private boolean reorderObjects;

    //todo add doc
    public Universe(@NonNull Matrix4f projMat) {
        this.projMat = projMat;
        gameObjects = new Vector<>(32);

        reorderObjects = false;

        enablePhysics();
        physics = new World();
    }

    //todo add doc
    public void enablePhysics(){
        physicsActive = true;
    }

    //todo add doc
    public void disablePhysics(){
        physicsActive = false;
    }

    //todo add doc
    public void update(double delta) {
        if(physicsActive)
            physics.update(delta);

        gameObjects.forEach(object -> object.updateWrap(delta));

        changeRenderOrder();
    }

    //todo add doc
    public void render() {
        VertexArray.unbind();
        Texture.unbind();
        Shader.unbind();

        gameObjects.forEach(Object::renderWrap);
    }

    //todo add doc
    public void addObject(@NonNull Object object){
        gameObjects.add(object);
        object.setUniverse(this);
        if(object instanceof PhysicalObject)
            physics.addBody(((PhysicalObject) object).getBody());

        object.onAddToUniverse();
    }

    //todo add doc
    public void removeObject(@NonNull Object object){
        object.onRemoveFromUniverse();

        gameObjects.remove(object);
        object.setUniverse(null);
        if(object instanceof PhysicalObject)
            physics.removeBody(((PhysicalObject) object).getBody());
    }

    //todo add doc
    public Object findByName(@NonNull String name){
        for (Object o : gameObjects)
            if(o.getName().equals(name))
                return o;
        return null;
    }

    //todo add doc
    void zIndexChanged(){
        reorderObjects = true;
    }

    //todo add doc
    private void changeRenderOrder(){
        if(reorderObjects){
            gameObjects.sort((Object e1, Object e2) -> {

                        //order this list by the z-index of the entities to make sure the rendering is done in the right
                        //also to make transparency work correctly

                        if (e1.getZIndex() > e2.getZIndex())
                            return 1;
                        if (e1.getZIndex() < e2.getZIndex())
                            return -1;

                        //z-index are equal
                        return 0;
                    }
            );
            reorderObjects = false;
        }
    }
}

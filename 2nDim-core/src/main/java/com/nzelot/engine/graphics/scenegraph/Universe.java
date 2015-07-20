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
import com.nzelot.engine.graphics.Window;
import com.nzelot.engine.graphics.rendering.*;
import lombok.Getter;
import lombok.NonNull;
import org.joml.Matrix4f;

import java.util.Vector;

/**
 * The root entity of the scene graph. it has no parent and holds the physics world but is not part of it
 *
 * @author nZeloT
 * @implNote the <code>render()</code>-Method calls the <code>unbind()</code>-Methods of
 * <code>VertexArray, Texture</code> and <code>Shader</code> classes
 */
//doc
public class Universe {

    private static final String CLASS_NAME = Universe.class.getName();

    Vector<GameObject> gameObjects;

    private @Getter Camera mainCamera;

    private boolean reorderObjects;

    //fixme this is only temporary i think. only until i implemented the use of FBO's
    private @Getter Game game;

    //doc
    public Universe(@NonNull Game game) {

        this.game = game;

        gameObjects = new Vector<>(32);

        Window w = game.getWindow();
        mainCamera = new Camera((float)w.getHeight() / (float)w.getWidth());

        reorderObjects = false;
    }

    //doc
    public void update(double delta) {
        gameObjects.forEach(object -> object.updateWrap(delta));

        changeRenderOrder();
    }

    //doc
    public void render() {
        VertexArray.unbind();
        Texture.unbind();
        Shader.unbind();

        gameObjects.forEach(GameObject::renderWrap);
    }

    //doc
    public void addObject(@NonNull GameObject gameObject){
        gameObjects.add(gameObject);
        gameObject.setUniverse(this);

        gameObject.onAddToUniverse();
    }

    //doc
    public void removeObject(@NonNull GameObject gameObject){
        gameObject.onRemoveFromUniverse();

        gameObjects.remove(gameObject);
        gameObject.setUniverse(null);
    }

    //doc
    public GameObject findByName(@NonNull String name){
        for (GameObject o : gameObjects)
            if(o.getName().equals(name))
                return o;
        return null;
    }

    //doc
    public Matrix4f getProjectionMat(){
        return mainCamera.getProjectionMat();
    }

    //doc
    public Matrix4f getCameraMat(){
        return mainCamera.getCameraMat();
    }

    //doc
    void zIndexChanged(){
        reorderObjects = true;
    }

    //doc
    private void changeRenderOrder(){
        if(reorderObjects){
            gameObjects.sort((GameObject e1, GameObject e2) -> {

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

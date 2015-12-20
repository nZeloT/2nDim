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
import lombok.experimental.Delegate;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    final Vector<GameObject> gameObjects;

    private final @Getter Camera mainCamera;

    private final VertexArray vao;
    private final Shader shader;
    private final Matrix4f projMat;
    private final Matrix4f camMat;
    private final Matrix4f modMat;

    private Texture test;

    private boolean reorderObjects;

    private @Delegate(types = WorldDelegates.class) World physics;

    //fixme this is only temporary i think. only until i implemented the use of FBO's
    private final @Getter Game game;

    //doc
    public Universe(@NonNull Game game) {

        this.game = game;

        gameObjects = new Vector<>(32);

        Window w = game.getWindow();
        mainCamera = new Camera(new Vector3f(), w.getWidth(), w.getHeight(), 20);

        vao = VertexArrayManager.instance.get(VertexArrayManager.STANDARD.SQUARE);
        shader = ShaderManager.instance.get(ShaderManager.STANDARD.SQUARE_TEXTURE);
        projMat = new Matrix4f().setOrtho2D(-w.getWidth() / 2, w.getWidth() / 2, -w.getHeight() / 2, w.getHeight() / 2);
        camMat = new Matrix4f().identity();
        modMat = new Matrix4f().scaling(w.getWidth(), w.getHeight(), 0);

        shader.setUniform1i("tex", 1);
        shader.setUniformMat4f("pr_matrix", projMat);
        shader.setUniformMat4f("cm_matrix", camMat);
        shader.setUniformMat4f("mv_matrix", modMat);

        test = TextureManager.instance.get(TextureManager.STANDARD.NOT_FOUND);

        reorderObjects = false;

        physics = new World();
    }

    //doc
    public void update(double delta) {
        physics.update(delta);

        gameObjects.forEach(object -> object.updateWrap(delta));

        changeRenderOrder();
    }

    //doc
    public void render() {
        VertexArray.unbind();
        Texture.unbind();
        Shader.unbind();
        FrameBuffer.unbind();

        //clear the fbo; nice effect otherwise :D
        mainCamera.cleanUp();

        //render to the camera fbo
        mainCamera.activateForRendering();

        gameObjects.forEach(GameObject::renderWrap);

        mainCamera.deactivateForRendering();

        //Render the main camera fbo texture to the screen
        mainCamera.getRenderedTexture().bind();
        shader.setUniform1i("tex", 1);
        //fixme always setting these matrices is not very usable. find a better solution
        //fixme as this also heavily influences the maximum fps
        shader.setUniformMat4f("pr_matrix", projMat);
        shader.setUniformMat4f("cm_matrix", camMat);
        shader.setUniformMat4f("mv_matrix", modMat);

        shader.bind();
        vao.bind();

        vao.render();
    }

    //doc
    public void addObject(@NonNull GameObject gameObject){
        gameObjects.add(gameObject);
        gameObject.setUniverse(this);
        physics.addBody(gameObject.getBody());
    }

    //doc
    public void removeObject(@NonNull GameObject gameObject){
        gameObject.onRemoveFromUniverse();

        gameObjects.remove(gameObject);
        gameObject.setUniverse(null);
        physics.removeBody(gameObject.getBody());
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

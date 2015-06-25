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

package com.nzelot.engine.game;

import com.nzelot.engine.graphics.rendering.Shader;
import com.nzelot.engine.graphics.rendering.Texture;
import com.nzelot.engine.graphics.rendering.VertexArray;
import com.nzelot.engine.graphics.scenegraph.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;

public class RuntimeTest {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());

        Game g = new Game(1280, 720, false, "Test") {
            @Override
            protected void initialize() {
                sceneGraph = new Universe();

                sceneGraph.addChild(new Picture(
                        new Vector3f(0, 0, 0),
                        "engine/testdata/images/rhino.png"
                ));

            }

            @Override
            protected void shutdown() {
            }
        };

        Runtime.get().runGame(g);
    }
}

class Picture extends Entity {

    private Texture texture;
    private Shader shader;
    private VertexArray geom;

    private Matrix4f transform;
    private Matrix4f rotation;
    private Matrix4f scale;

    public Picture(Vector3f relPos, String picture) {
        super(relPos);

        texture = new Texture(picture);
        shader = new Shader("engine/testdata/shader/pic.vert", "engine/testdata/shader/pic.frag");

        float[] vertices = new float[]{
                -1.0f, -1.0f, 0.0f, // 0-----1
                1.0f, -1.0f, 0.0f, // |     |
                1.0f, 1.0f, 0.0f, // |     |
                -1.0f, 1.0f, 0.0f  // 3-----2
        };

        byte[] indices = new byte[]{
                0, 1, 2,
                0, 2, 3
        };

        float[] tcs = new float[]{
                0, 1,
                1, 1,
                1, 0,
                0, 0
        };

        geom = new VertexArray(vertices, indices, tcs);

        Matrix4f pr_matrix = new Matrix4f();
        pr_matrix.identity().setOrtho(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);

        shader.setUniformMat4f("pr_matrix", pr_matrix);
        shader.setUniform1i("tex", 1);

        transform = new Matrix4f().identity();
        rotation = new Matrix4f().identity();
        scale = new Matrix4f().identity().scale(5.0f);

        shader.setUniformMat4f("mv_matrix", scale);
    }

    @Override
    public void update(double delta) {
    }

    @Override
    public void render() {
        texture.bind();
        shader.enable();
        geom.bind();

        geom.draw();

    }
}

class Universe extends Entity {

    public Universe() {
        super(new Vector3f());
    }

    @Override
    public void update(double delta) {
        //Dummy
    }

    @Override
    public void render() {
        VertexArray.unbind();
        Shader.disable();
        Texture.unbind();
    }
}
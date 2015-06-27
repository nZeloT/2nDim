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

import com.nzelot.engine.graphics.rendering.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author nZeloT
 */
//TODO: add some doc
public class Square extends Entity {

    protected Shader shader;
    protected VertexArray geom;

    private Matrix4f transform;
    private Matrix4f translate;
    private Matrix4f rotation;
    private Matrix4f scaling;

    private float scale;
    private float angle;

    public Square(Vector3f pos, ShaderManager.STANDARD std) {
        super(pos);

        this.shader = ShaderManager.getShader(std);
        this.scale = 1.0f;
        this.angle = 0.0f;

        geom = VertexArrayManager.getVertexArray(VertexArrayManager.STANDARD.SQUARE);

        //TODO: move this; it is indeed very ugly to specify the pr_matrix in here
        Matrix4f pr_matrix = new Matrix4f();
        pr_matrix.identity().setOrtho(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);

        shader.setUniformMat4f("pr_matrix", pr_matrix);

        transform = new Matrix4f().identity();
        translate = new Matrix4f().identity();
        rotation = new Matrix4f().identity();
        scaling = new Matrix4f().identity();
    }

    public void rotate(float angle) {
        rotation.rotateZ(angle);
        this.angle += angle;
    }

    public void scale(float s) {
        scaling.scale(s);
        this.scale *= s;
    }

    public float getRotation() {
        return angle;
    }

    public void setRotation(float angle) {
        rotation.rotationZ(angle);
        this.angle = angle;
    }

    public float getScaling() {
        return scale;
    }

    public void setScaling(float s) {
        scaling.scaling(s, s, s);
        this.scale = s;
    }

    @Override
    public void update(double delta) {

        //Apply translation according to position
        translate.translation(getAbsolutePosition());

        //Calculate the transformation matrix
        //transform = scaling * translate * rotate
        // => t = translate * rotate
        // => transform = scaling * t

        transform.identity();

        Matrix4f.mul(translate, rotation, transform);
        Matrix4f.mul(scaling, transform, transform);
    }

    @Override
    public void render() {
        shader.setUniformMat4f("mv_matrix", transform);
        geom.bind();

        geom.render();
    }

}

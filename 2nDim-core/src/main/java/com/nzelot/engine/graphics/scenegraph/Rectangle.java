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

import com.nzelot.engine.graphics.rendering.Color;
import com.nzelot.engine.graphics.rendering.ShaderManager;
import com.nzelot.engine.graphics.rendering.VertexArrayManager;
import lombok.NonNull;
import org.joml.Matrix4f;

/**
 * @author nZeloT
 */
//doc
// for textures also set this.shader.setUniform1i("tex", 1);
public class Rectangle extends GameObject {

    private Color color;

    //doc
    public Rectangle(String name, double sizeX, double sizeY, @NonNull Color color) {
        super(name,
                ShaderManager.instance.get(ShaderManager.STANDARD.SQUARE),
                VertexArrayManager.instance.get(VertexArrayManager.STANDARD.SQUARE)
        );

        //setup physics
        addFixture(new org.dyn4j.geometry.Rectangle(sizeX, sizeY));
        setMass();

        //setup appearance
        this.color = color;
    }

    //doc
    @Override
    protected void onAddToUniverse() {
        getShader().setUniformMat4f("pr_matrix", getUniverse().getProjectionMat());
    }

    //doc
    @Override
    protected void onRemoveFromUniverse() {
        //NOP
    }

    //doc
    @Override
    public void update(double delta) {
        //NOP
    }

    //doc
    @Override
    public void render(Matrix4f transformation) {
        getShader().setUniform4f("col", color.asVector4f());
        super.render(transformation);
    }
}

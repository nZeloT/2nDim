/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 nZeloT
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
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Matrix4f;

/**
 * @author nZeloT
 */
public class TexturedRectangle extends Rectangle {

    private @Getter(AccessLevel.PROTECTED) Texture tex;

    TexturedRectangle(String name, double sizeX, double sizeY, Texture tex, Shader shader, VertexArray geo) {
        super(name, sizeX, sizeY, shader, geo);
        this.tex = tex;
    }

    public TexturedRectangle(String name, double sizeX, double sizeY, Texture tex) {
        this(name, sizeX, sizeY, tex,
                ShaderManager.instance.get(ShaderManager.STANDARD.SQUARE_TEXTURE),
                VertexArrayManager.instance.get(VertexArrayManager.STANDARD.SQUARE)
        );
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(Matrix4f transformation) {
        tex.bind();
        getShader().setUniform1i("tex", 1);
        super.render(transformation);
    }

    @Override
    protected void onRemoveFromUniverse() {

    }
}

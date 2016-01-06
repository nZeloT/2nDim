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

import com.nzelot.engine.graphics.rendering.ShaderManager;
import com.nzelot.engine.graphics.rendering.Texture;
import com.nzelot.engine.graphics.rendering.VertexArrayManager;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * @author nZeloT
 */
public class Sprite extends TexturedRectangle {

    private int texPerRow;

    protected @Getter int current;

    //0 - row; 1 = col; 2 = width; 3 = height
    private Vector4f spriteData;

    protected boolean changed;

    public Sprite(String name, double sizeX, double sizeY, Texture tex, int current, int texPerRow, float texWidth, float texHeight) {
        super(name, sizeX, sizeY, tex,
                ShaderManager.instance.get(ShaderManager.STANDARD.SQUARE_SPRITE),
                VertexArrayManager.instance.get(VertexArrayManager.STANDARD.SQUARE)
        );

        this.current = current;
        this.texPerRow = texPerRow;

        this.spriteData = new Vector4f();
        this.spriteData.z = texWidth;
        this.spriteData.w = texHeight;
        this.changed = true;
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(Matrix4f transformation) {
        if(changed){
            spriteData.x = (int)((current+0.0f) / texPerRow);
            spriteData.y = (int)(current - spriteData.x * texPerRow);
            changed = false;
        }

        getShader().setUniform4f("sprite", spriteData);
        super.render(transformation);
    }

    @Override
    protected void onRemoveFromUniverse() {

    }

    public void setCurrent(int current) {
        this.current = current;
        this.changed = true;
    }
}

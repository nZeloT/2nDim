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

package com.nzelot.engine.graphics.rendering;

import com.nzelot.engine.definition.ManagedObject;
import com.nzelot.engine.utils.BufferUtils;
import lombok.NonNull;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * taken from https://github.com/TheCherno/Flappy/blob/master/src/com/thecherno/flappy/graphics/VertexArray.java
 *
 * @author TheCherno
 */
public class VertexArray extends ManagedObject {

    private int vao, vbo, ibo, tbo;
    private int count;

    private boolean enabled;

    //VertexArray(int count) {
    //    this.count = count;
    //    vao = glGenVertexArrays();
    //}

    VertexArray(@NonNull float[] vertices, @NonNull byte[] indices, @NonNull float[] textureCoordinates) {
        count = indices.length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(Shader.VERTEX_ATTRIB, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);

        tbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
        glVertexAttribPointer(Shader.TCOORD_ATTRIB, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(Shader.TCOORD_ATTRIB);

        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createByteBuffer(indices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        enabled = true;
    }

    public static void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void bind() {
        if(enabled) {
            glBindVertexArray(vao);
            if (ibo > 0)
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        }
    }

    public void render() {
        render(GL_TRIANGLES);
    }

    public void render(int glBeginMode){
        if(enabled) {
            if (ibo > 0)
                glDrawElements(glBeginMode, count, GL_UNSIGNED_BYTE, 0);
            else
                glDrawArrays(glBeginMode, 0, count);
        }
    }

    protected void delete(){
        if(enabled) {
            enabled = false;
            VertexArray.unbind();
            glDeleteVertexArrays(vao);
            glDeleteBuffers(vbo);
            glDeleteBuffers(ibo);
            glDeleteBuffers(tbo);
        }
    }

    @Override
    public boolean isActive() {
        return enabled;
    }
}
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
import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;


/**
 * @author nZeloT
 */
public class FrameBuffer extends ManagedObject {

    private static int bound;

    private int fboID;
    private @Getter Texture tex;
    private @Getter int width, height;
    private boolean enabled;

    FrameBuffer(int width, int height) {
        this.enabled = true;
        this.width = width;
        this.height = height;

        if(width <= 0 || height <= 0){
            Logger.log(FrameBufferManager.class, "Tried to create FrameBuffer with width and/or height <= 0. This is not permitted.", Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to create FrameBuffer with width and/or height <= 0. This is not permitted.");
        }

        create();
    }

    private void create(){
        int fboID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        //validate that everything worked well
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            Logger.log(FrameBuffer.class, "Failed to create a complete Frambuffer. glCheckFrameBufferStatus(): " + glCheckFramebufferStatus(GL_FRAMEBUFFER), Logger.LEVEL.ERROR);
            throw new IllegalStateException("Failed to create a complete Frambuffer.");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        this.tex = new Texture(tex);
        this.fboID = fboID;
    }

    public void clear(){
        if(enabled){
            bind();

            glClear(GL_COLOR_BUFFER_BIT);

        }
    }

    public void bind(){
        if(enabled && FrameBuffer.bound != fboID){
            glBindFramebuffer(GL_FRAMEBUFFER, fboID);
            FrameBuffer.bound = fboID;
        }
    }

    public static void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        FrameBuffer.bound = 0;
    }

    @Override
    protected void delete() {
        if(enabled){
            FrameBuffer.unbind();
            glDeleteFramebuffers(fboID);
            tex.delete();
            fboID = -1;
            tex = null;
            enabled = false;
        }
    }

    @Override
    public boolean isActive() {
        return enabled;
    }
}

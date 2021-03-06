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
import lombok.Getter;
import third.party.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * holds a OpenGL Texture
 *
 * @author nZeloT
 */
//doc
public class Texture extends ManagedObject {

    private static int bound;

    private @Getter int width, height;
    private int texID;
    private boolean enabled;

    Texture(InputStream inputStream) {
        this.texID = load(inputStream);
        this.enabled = true;
    }

    Texture(int texID){
        this.texID = texID;
        this.enabled = true;
    }

    private int load(InputStream in) {
        int result;

        try {

            PNGDecoder decoder = new PNGDecoder(in);
            this.width = decoder.getWidth();
            this.height = decoder.getHeight();

            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, 4 * width, PNGDecoder.Format.RGBA);
            buffer.flip();

            //TODO: add opengl error handling here
            result = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, result);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glBindTexture(GL_TEXTURE_2D, 0);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return result;
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
        Texture.bound = 0;
    }

    public void bind() {
        if (enabled && Texture.bound != texID) {
            glBindTexture(GL_TEXTURE_2D, texID);
            Texture.bound = texID;
        }
    }

    @Override
    protected void delete() {
        if(enabled){
            Texture.unbind();
            glDeleteTextures(texID);
            texID = -1;
            enabled = false;
        }
    }

    @Override
    public boolean isActive() {
        return enabled;
    }
}

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

import com.nzelot.engine.definition.Manager;
import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * @author nZeloT
 */
//doc
public class VertexArrayManager extends Manager<VertexArray> {

    //fixme this is only temporary
    public static final VertexArrayManager instance = new VertexArrayManager();

    //prevent instantiation
    private VertexArrayManager() {
    }


    public VertexArray get(STANDARD std) {
        return get(std.getKey());
    }

    public VertexArray create(@NonNull String key,
                                                @NonNull float[] vertices,
                                                @NonNull byte[] indices,
                                                @NonNull float[] textureCoordinates){
        if (objects.containsKey(key)) {
            Logger.log(VertexArrayManager.class, "Tried to store already stored VertexArray with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to store already stored VertexArray with key: " + key);
        }

        VertexArray va = new VertexArray(vertices, indices, textureCoordinates);
        objects.put(key, va);

        return va;
    }

    /**
     * initialize the <code>ShaderManager</code>. This will be called from within the engine.
     */
    protected void initSTD(Map<String, VertexArray> objects) {
            //load all the standard shader
            VertexArray s;

            STANDARD[] standards = STANDARD.values();
            for (STANDARD standard : standards) {
                s = new VertexArray(standard.getVert(), standard.getInd(), standard.getTcs());

                objects.put(standard.getKey(), s);
            }
    }

    //*************************************************
    //enum Standard
    public enum STANDARD {
        SQUARE(
                "com.nzelot.2nDim.vertexarray.standard.square",
                new float[]{
                        -0.5f, -0.5f, 0.0f, // 0-----1
                        0.5f, -0.5f, 0.0f, // |     |
                        0.5f, 0.5f, 0.0f, // |     |
                        -0.5f, 0.5f, 0.0f  // 3-----2
                },
                new byte[]{
                        0, 1, 2,
                        0, 2, 3
                },
                new float[]{
                        0, 1,
                        1, 1,
                        1, 0,
                        0, 0
                }
        ),
        //a 16 vert circle
        CIRCLE16(
                "com.nzelot.2nDim.vertexarray.standard.circle16",
                CircleGenerator.vertGen(16),
                CircleGenerator.indGen(16),
                CircleGenerator.tcsGen(16)
        );

        private @Getter String key;
        private @Getter float[] vert;
        private @Getter byte[] ind;
        private @Getter float[] tcs;

        STANDARD(String key, float[] vert, byte[] ind, float[] tcs) {
            this.key = key;
            this.vert = vert;
            this.ind = ind;
            this.tcs = tcs;
        }

    }

}

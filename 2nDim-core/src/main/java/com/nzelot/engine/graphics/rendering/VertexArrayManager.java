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

import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nZeloT
 */
//TODO: add some doc
public class VertexArrayManager {

    private static final String CLASS_NAME = VertexArrayManager.class.getName();
    private static Map<String, VertexArray> arrays;
    //end enum standard
    //*************************************************
    private static boolean init;

    //prevent instantiation
    private VertexArrayManager() {
    }


    public static VertexArray getVertexArray(STANDARD std) {
        return getVertexArray(std.getKey());
    }

    public static VertexArray getVertexArray(String key) {
        VertexArray va = arrays.get(key);

        if (va == null) {
            Logger.log(CLASS_NAME + ": Tried to access non existent VertexArray with key: " + key, Logger.LEVEL.WARNING);
            throw new IllegalArgumentException("Tried to access non existent VertexArray with key: " + key);
        }

        return va;
    }

    public static void store(String key, VertexArray vertexArray) {
        if (arrays.containsKey(key)) {
            Logger.log(CLASS_NAME + ": Tried to store already stored VertexArray with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to store already stored VertexArray with key: " + key);
        }

        arrays.put(key, vertexArray);
    }

    /**
     * initialize the <code>ShaderManager</code>. This will be called from within the engine.
     */
    public static void init() {
        //prevent from calling this multiple times
        if (!init) {

            VertexArrayManager.arrays = new HashMap<>();

            //load all the standard shader
            VertexArray s;

            STANDARD[] standards = STANDARD.values();
            for (STANDARD standard : standards) {
                s = new VertexArray(standard.getVert(), standard.getInd(), standard.getTcs());

                arrays.put(standard.getKey(), s);
            }

            VertexArrayManager.init = true;
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

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

import java.util.HashMap;
import java.util.Map;

/**
 * @author nZeloT
 */
//TODO: add some doc here
public class ShaderManager {

    private static final String CLASS_NAME = ShaderManager.class.getName();

    //*************************************************
    // standard enum
    private static Map<String, Shader> shaders;

    // end enum standard
    //*************************************************

    //*************************************************
    // access Methods
    private static boolean init;

    //prevent instantiation
    private ShaderManager() {
    }

    /**
     * Retrieve a <code>Shader</code>-Object from the standard library.<br>
     * Note: for the same <code>key</code>, always the same <code>Shader</code>-Object is returned.
     *
     * @param std the requested standard shader
     * @return the <code>Shader</code>-Object
     */
    public static Shader getShader(STANDARD std) {
        return getShader(std.getKey());
    }

    // end access methods
    //*************************************************

    /**
     * Retrieve a <code>Shader</code>-Object identified with the given <code>key</code>.<br>
     * Note: for the same <code>key</code>, always the same <code>Shader</code>-Object is returned.
     *
     * @param key the key for the requested <code>Shader</code>-Object
     * @return the <code>Shader</code>-Object
     */
    public static Shader getShader(String key) {
        Shader s = shaders.get(key);

        if (s == null) {
            Logger.log(CLASS_NAME + ": Tried to access non existent shader with key: " + key, Logger.LEVEL.WARNING);
            throw new IllegalArgumentException("Tried to access non existent shader with key: " + key);
        }

        return s;
    }

    public static Shader loadShader(String key, String vertPath, String fragPath) {
        if (shaders.containsKey(key)) {
            Logger.log(CLASS_NAME + ": Tried to load already existing shader with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to load already existing shader with key: " + key);
        }

        Shader s = new Shader(vertPath, fragPath);

        shaders.put(key, s);

        return s;
    }

    /**
     * initialize the <code>ShaderManager</code>. This will be called from within the engine.
     */
    public static void init() {
        //prevent from calling this multiple times
        if (!init) {

            ShaderManager.shaders = new HashMap<>();

            //load all the standard shader
            Shader s;

            STANDARD[] standards = STANDARD.values();
            for (STANDARD standard : standards) {
                s = new Shader(standard.getVertPath(), standard.getFragPath());

                shaders.put(standard.getKey(), s);
            }

            ShaderManager.init = true;
        }
    }

    /**
     * enum declaring a basic shader library
     */
    public enum STANDARD {

        SQUARE("com.nzelot.engine.shader.standard.square", "engine/resources/shader/colSquare.vert", "engine/resources/shader/colSquare.frag"),
        SQUARE_TEXTURE("com.nzelot.engine.shader.standard.square_tex", "engine/resources/shader/texSquare.vert", "engine/resources/shader/texSquare.frag");

        private String key;

        //TODO: this needs improvement. it is indeed very ugly and not dynamic at all!
        private String vertPath;
        private String fragPath;

        STANDARD(String key, String vertPath, String fragPath) {
            this.key = key;
            this.vertPath = vertPath;
            this.fragPath = fragPath;
        }

        public String getKey() {
            return key;
        }

        public String getFragPath() {
            return fragPath;
        }

        public String getVertPath() {
            return vertPath;
        }
    }
}

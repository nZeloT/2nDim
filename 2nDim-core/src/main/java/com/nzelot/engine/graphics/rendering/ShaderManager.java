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
import com.nzelot.engine.utils.FileUtils;
import com.nzelot.engine.utils.ResourceUtils;
import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;

import java.util.Map;

/**
 * @author nZeloT
 */
//doc here
public class ShaderManager extends Manager<Shader> {

    //fixme this is only temporary
    public static final ShaderManager instance = new ShaderManager();

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
    public Shader get(STANDARD std) {
        return get(std.getKey());
    }

    /**
     * load one of your own shaders.
     *
     * @param key      the key of the newly loaded shader
     * @param vertPath the path to the vertex shader
     * @param fragPath the path to the fragment shader
     * @return the newly created shader object
     * @implNote replacing a shader with another one is not supported
     */
    public Shader create(String key, String vertPath, String fragPath) {
        if (objects.containsKey(key)) {
            Logger.log(ShaderManager.class, "Tried to load already existing shader with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to load already existing shader with key: " + key);
        }

        String vert = FileUtils.loadAsString(vertPath);
        String frag = FileUtils.loadAsString(fragPath);
        Shader s = new Shader(vert, frag);

        objects.put(key, s);

        return s;
    }

    /**
     * replace one of the standard shader with your implementation to add extra features
     *
     * @param std      the standard shader to replace
     * @param vertPath the path to the new vertex shader
     * @param fragPath the path to the new fragment shader
     * @return the new Standard Shader
     */
    public Shader replaceStandardShader(STANDARD std, String vertPath, String fragPath) {
        String vert = FileUtils.loadAsString(vertPath);
        String frag = FileUtils.loadAsString(fragPath);
        Shader s = new Shader(vert, frag);

        objects.put(std.getKey(), s);

        return s;
    }

    /**
     * initialize the <code>ShaderManager</code>. This will be called from within the engine.
     */
    protected void initSTD(Map<String, Shader> objects) {
            //load all the standard shader
            Shader s;

            STANDARD[] standards = STANDARD.values();
            for (STANDARD standard : standards) {
                String vert = ResourceUtils.loadAsString(standard.getVertPath());
                String frag = ResourceUtils.loadAsString(standard.getFragPath());
                s = new Shader(vert, frag);

                objects.put(standard.getKey(), s);
            }

    }

    /**
     * enum declaring a basic shader library
     */
    public enum STANDARD {

        SQUARE("com.nzelot.2nDim.shader.standard.square", "res/shader/simple.vert", "res/shader/simpleAmbiant.frag"),
        SQUARE_TEXTURE("com.nzelot.2nDim.shader.standard.square_tex", "res/shader/simpleTex.vert", "res/shader/simpleTexturedAmbiant.frag"),
        CIRCLE("com.nzelot.2nDim.shader.standard.circle", "res/shader/simpleTex.vert", "res/shader/simpleAmbiantCircle.frag"),
        CIRLCE_TEXTURE("com.nzelot.2nDim.shader.standard.circle_tex", "res/shader/simpleTex.vert", "res/shader/simpleTexturedAmbiantCircle.frag");

        private @Getter String key;

        //fixme: improved as they are included in the generated jar, but still not fine with the solution!
        private @Getter String vertPath;
        private @Getter String fragPath;

        STANDARD(String key, String vertPath, String fragPath) {
            this.key = key;
            this.vertPath = vertPath;
            this.fragPath = fragPath;
        }
    }
}

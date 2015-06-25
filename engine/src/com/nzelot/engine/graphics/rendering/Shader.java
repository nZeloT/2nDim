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

import com.nzelot.engine.utils.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * initially taken from https://github.com/TheCherno/Flappy/blob/master/src/com/thecherno/flappy/graphics/Shader.java
 * <p>
 * slightly modified
 */
public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;
    private static int enabled;
    private final int ID;
    private Map<String, Integer> locationCache;
    private FloatBuffer buffer;

    public Shader(String vertex, String fragment) {
        ID = ShaderUtils.load(vertex, fragment);
        locationCache = new HashMap<>();
        buffer = BufferUtils.createFloatBuffer(16);
    }

    public static void disable() {
        glUseProgram(0);
        Shader.enabled = 0;
    }

    public void setUniform1i(String name, int value) {
        if (enabled != ID) enable();
        glUniform1i(getUniform(name), value);
    }

    public void enable() {
        if (Shader.enabled != ID) {
            glUseProgram(ID);
            Shader.enabled = ID;
        }
    }

    public int getUniform(String name) {
        if (locationCache.containsKey(name))
            return locationCache.get(name);

        int result = glGetUniformLocation(ID, name);
        if (result == -1)
            System.err.println("Could not find uniform variable '" + name + "'!");
        else
            locationCache.put(name, result);
        return result;
    }

    public void setUniform1f(String name, float value) {
        if (enabled != ID) enable();
        glUniform1f(getUniform(name), value);
    }

    public void setUniform2f(String name, float x, float y) {
        if (enabled != ID) enable();
        glUniform2f(getUniform(name), x, y);
    }

    public void setUniform3f(String name, Vector3f vector) {
        if (enabled != ID) enable();
        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    public void setUniformMat4f(String name, Matrix4f matrix) {
        if (enabled != ID) enable();

        matrix.get(buffer);
        buffer.position(16); //Because reasons ...
        buffer.flip(); // For the sake of god. Do not forget!
        glUniformMatrix4fv(getUniform(name), false, buffer);
        buffer.clear(); //Prepare for next round :D
    }

}
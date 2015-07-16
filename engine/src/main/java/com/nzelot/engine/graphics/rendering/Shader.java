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
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Use <code>ShaderManager</code> to retrieve a shader object
 * <p>
 * initially taken from https://github.com/TheCherno/Flappy/blob/master/src/com/thecherno/flappy/graphics/Shader.java
 * <p>
 * slightly modified to use the <code>joml</code> library and have a static <code>bind()</code>-method
 *
 * @author TheCherno
 * @author nZeloT
 */
public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;
    private static final String CLASS_NAME = Shader.class.getName();
    private static int enabled;
    private final int ID;
    private Map<String, Integer> locationCache;
    private FloatBuffer buffer;

    // only visible within the package to prevent instantiation from outside the ShaderManager
    Shader(String vertex, String fragment) {
        ID = ShaderUtils.create(vertex, fragment);
        locationCache = new HashMap<>();
        buffer = BufferUtils.createFloatBuffer(16);
    }

    /**
     * unbind any previously bound shader
     */
    public static void unbind() {
        glUseProgram(0);
        Shader.enabled = 0;
    }

    /**
     * push a new value to a <code>uniform int</code>
     *
     * @param name  the name of the int
     * @param value the new value
     */
    public void setUniform1i(String name, int value) {
        if (enabled != ID) bind();
        glUniform1i(getUniform(name), value);
    }

    /**
     * bind the shader for usage
     * all the setXXX methods call bind if necessary
     */
    public void bind() {
        if (Shader.enabled != ID) {
            glUseProgram(ID);
            Shader.enabled = ID;
        }
    }

    /**
     * get the id of the uniform with the spcified name
     *
     * @param name the name of the uniform
     * @return the ID of the uniform
     */
    private int getUniform(String name) {
        if (locationCache.containsKey(name))
            return locationCache.get(name);

        int result = glGetUniformLocation(ID, name);
        if (result == -1) {
            Logger.log(CLASS_NAME + ": Tried to get OpenGL ID of uniform value: " + name, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to get OpenGL ID of uniform value: " + name);
        }else
            locationCache.put(name, result);
        return result;
    }

    /**
     * push a new value to a <code>uniform float</code>
     * @param name the name of the uniform
     * @param value the new value
     */
    public void setUniform1f(String name, float value) {
        if (enabled != ID) bind();
        glUniform1f(getUniform(name), value);
    }

    public void setUniform2f(String name, float x, float y) {
        if (enabled != ID) bind();
        glUniform2f(getUniform(name), x, y);
    }

    /**
     * push new values to a <code>uniform vec3</code>
     * @param name the name of the uniform
     * @param vector the new values
     */
    public void setUniform3f(String name, Vector3f vector) {
        if (enabled != ID) bind();
        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    /**
     * push new values to a <code>uniform vec4</code>
     *
     * @param name   the name of the uniform
     * @param vector the new values
     */
    public void setUniform4f(String name, Vector4f vector) {
        if (enabled != ID) bind();
        glUniform4f(getUniform(name), vector.x, vector.y, vector.z, vector.w);
    }

    /**
     * push new values to a <code>uniform mat4</code>
     * @param name the name of the matrix
     * @param matrix the new values
     */
    public void setUniformMat4f(String name, Matrix4f matrix) {
        if (enabled != ID) bind();

        matrix.get(buffer);
        buffer.position(16); //Because reasons ...
        buffer.flip(); // For the sake of god. Do not forget!
        glUniformMatrix4fv(getUniform(name), false, buffer);
        buffer.clear(); //Prepare for next round :D
    }

}

/**
 * taken from https://github.com/TheCherno/Flappy/tree/master/src/com/thecherno/flappy/utils
 * <p>
 * moved here because it should not be accessible from outside this class/package
 *
 * @author TheCherno
 */
class ShaderUtils {

    private static final String CLASS_NAME = ShaderUtils.class.getName();

    //prevent instantiation
    private ShaderUtils() {
    }

    /**
     * create a shader program from a vertex and fragment shader
     *
     * @param vert the vertex shader
     * @param frag the fragment shader
     * @return the newly generated shader ID
     */
    public static int create(String vert, String frag) {
        int program = glCreateProgram();
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertID, vert);
        glShaderSource(fragID, frag);

        glCompileShader(vertID);
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = "Could not compile vertex shader!\n";
            error += glGetShaderInfoLog(vertID);
            Logger.log(CLASS_NAME + ": " + error, Logger.LEVEL.ERROR);
            throw new IllegalStateException(error);
        }

        glCompileShader(fragID);
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = "Could not compile fragment shader!\n";
            error += glGetShaderInfoLog(fragID);
            Logger.log(CLASS_NAME + ": " + error, Logger.LEVEL.ERROR);
            throw new IllegalStateException(error);
        }

        glAttachShader(program, vertID);
        glAttachShader(program, fragID);
        glLinkProgram(program);
        glValidateProgram(program);

        glDeleteShader(vertID);
        glDeleteShader(fragID);

        return program;
    }
}
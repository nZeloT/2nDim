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

package com.nzelot.engine.graphics;

import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A wrapper class handling all the glfw stuff. Including all the callbacks.
 * <p>
 * <p>
 * Initial Version taken from: https://github.com/TheCherno/Sparky/blob/master/Sparky-core/src/graphics/window.cpp<br>
 * and transferred to Java along with some modifications
 *
 * @author TheCherno
 * @author nZeloT
 */
public class Window {

    private static final int KEY_COUNT = 1024;
    private static final int BTN_COUNT = 32;

    private final String title;
    private @Getter int width, height;
    private long windowID;
    private boolean fullscreen;

    private boolean keys[];
    private boolean keyState[];
    private boolean keyTyped[];
    private boolean mouseBtn[];
    private boolean mouseState[];
    private boolean mouseClicked[];

    private @Getter Vector2f mousePosition;

    private GLFWFramebufferSizeCallback windowResize;
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;

    public Window(String title, int width, int height, boolean fullscreen) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        this.windowID = -1;

        this.keys = new boolean[KEY_COUNT];
        this.keyState = new boolean[KEY_COUNT];
        this.keyTyped = new boolean[KEY_COUNT];

        this.mouseBtn = new boolean[BTN_COUNT];
        this.mouseClicked = new boolean[BTN_COUNT];
        this.mouseState = new boolean[BTN_COUNT];

        this.mousePosition = new Vector2f();
    }

    public boolean init() {
        if (glfwInit() != GL_TRUE) {
            Logger.log("Could not initGame GLFW!", Logger.LEVEL.ERROR);
            glfwTerminate();
            return false;
        }

        if (!setUpWindow()) {
            glfwTerminate();
            return false;
        }

        Logger.log("OpenGL Version: " + glGetString(GL_VERSION));

        return true;
    }

    private boolean setUpWindow() {
        // Create new window
        ByteBuffer vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (fullscreen) {
            width = GLFWvidmode.width(vidMode);
            height = GLFWvidmode.height(vidMode);
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        long monitor = fullscreen ? glfwGetPrimaryMonitor() : NULL;
        long grabContext = windowID == -1 ? NULL : windowID;
        long newWindowID = glfwCreateWindow(width, height, title, monitor, grabContext);

        if (windowID == NULL) {
            Logger.log("Could not create Window!", Logger.LEVEL.ERROR);
            return false;
        }

        if (windowID > -1)
            glfwDestroyWindow(windowID);
        windowID = newWindowID;

        glfwMakeContextCurrent(windowID);
        glfwSwapInterval(1);

        setUpCallbacks();

        GLContext.createFromCurrent();

        if (!fullscreen) {
            glfwSetWindowPos(windowID, (GLFWvidmode.width(vidMode) - width) / 2, (GLFWvidmode.height(vidMode) - height) / 2);
        }

        glfwShowWindow(windowID);

        return true;
    }

    private void setUpCallbacks() {
        windowResize = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                glViewport(0, 0, w, h);
                width = w;
                height = h;
            }
        };

        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = action != GLFW_RELEASE;
            }
        };

        mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseBtn[button] = action != GLFW_RELEASE;
            }
        };

        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mousePosition.x = (float) xpos;
                mousePosition.y = (float) ypos;
            }
        };

        glfwSetFramebufferSizeCallback(this.windowID, windowResize);
        glfwSetKeyCallback(this.windowID, keyCallback);
        glfwSetMouseButtonCallback(this.windowID, mouseButtonCallback);
        glfwSetCursorPosCallback(this.windowID, cursorPosCallback);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void update() {
        long error = glGetError();
        if (error != GL_NO_ERROR) {
            Logger.log("OpenGL Error: " + error, Logger.LEVEL.ERROR);
        }

        glfwSwapBuffers(this.windowID);
    }

    public void updateInput() {
        glfwPollEvents();

        for (int i = 0; i < KEY_COUNT; i++)
            keyTyped[i] = keys[i] && !keyState[i];

        for (int i = 0; i < BTN_COUNT; i++)
            mouseClicked[i] = mouseBtn[i] && !mouseState[i];

        System.arraycopy(keys, 0, keyState, 0, KEY_COUNT);
        System.arraycopy(mouseBtn, 0, mouseState, 0, BTN_COUNT);
    }

    public boolean closed() {
        return glfwWindowShouldClose(this.windowID) == GL_TRUE;
    }

    public boolean isKeyPressed(int keycode) {
        if (keycode >= KEY_COUNT) {
            Logger.log("Tried isKeyPressed on key: " + keycode + "; KEY_COUNT is " + KEY_COUNT, Logger.LEVEL.WARNING);
            return false;
        }

        return keys[keycode];
    }

    public boolean isKeyTyped(int keycode) {
        if (keycode >= KEY_COUNT) {
            Logger.log("Tried isKeyTyped on key: " + keycode + "; KEY_COUNT is " + KEY_COUNT, Logger.LEVEL.WARNING);
            return false;
        }

        return keyTyped[keycode];
    }

    public boolean isMouseButtonPressed(int btn) {
        if (btn >= BTN_COUNT) {
            Logger.log("Tried isMouseButtonPressed on button: " + btn + "; BTN_COUNT is " + BTN_COUNT, Logger.LEVEL.WARNING);
            return false;
        }

        return mouseBtn[btn];
    }

    public boolean isMouseButtonClicked(int btn) {
        if (btn >= BTN_COUNT) {
            Logger.log("Tried isMouseButtonClicked on button: " + btn + "; BTN_COUNT is " + BTN_COUNT, Logger.LEVEL.WARNING);
            return false;
        }

        return mouseClicked[btn];
    }

    public void exit() {

        glfwDestroyWindow(this.windowID);

        windowResize.release();
        keyCallback.release();
        mouseButtonCallback.release();
        cursorPosCallback.release();

        glfwTerminate();
    }

}

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
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private static final int KEY_COUNT = 1024;
    private static final int BTN_COUNT = 32;

    private final String title;
    private int width, height;
    private long windowID;

    private boolean keys[];
    private boolean keyState[];
    private boolean keyTyped[];
    private boolean mouseBtn[];
    private boolean mouseState[];
    private boolean mouseClicked[];

    private Vector2f mousePosition;
    private GLFWFramebufferSizeCallback windowResize = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            glViewport(0, 0, w, h);
            width = w;
            height = h;
        }
    };
    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            keys[key] = action != GLFW_RELEASE;
        }
    };
    private GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseBtn[button] = action != GLFW_RELEASE;
        }
    };
    private GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            mousePosition.x = (float) xpos;
            mousePosition.y = (float) ypos;
        }
    };

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        if (!init())
            glfwTerminate();

        this.keys = new boolean[KEY_COUNT];
        this.keyState = new boolean[KEY_COUNT];
        this.keyTyped = new boolean[KEY_COUNT];

        this.mouseBtn = new boolean[BTN_COUNT];
        this.mouseClicked = new boolean[BTN_COUNT];
        this.mouseState = new boolean[BTN_COUNT];

        this.mousePosition = new Vector2f();
    }

    private boolean init() {
        if (glfwInit() != GL_TRUE) {
            Logger.log("Could not initialize GLFW!", Logger.LEVEL.ERROR);
            return false;
        }

        this.windowID = glfwCreateWindow(width, height, title, NULL, NULL);

        if (windowID == NULL) {
            Logger.log("Could not create Window!", Logger.LEVEL.ERROR);
            return false;
        }

        glfwMakeContextCurrent(this.windowID);
        glfwSetFramebufferSizeCallback(this.windowID, windowResize);
        glfwSetKeyCallback(this.windowID, keyCallback);
        glfwSetMouseButtonCallback(this.windowID, mouseButtonCallback);
        glfwSetCursorPosCallback(this.windowID, cursorPosCallback);

        glfwSwapInterval(1);

        GLContext.createFromCurrent();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Logger.log("OpenGL Version: " + glGetString(GL_VERSION));

        return true;
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
        glfwPollEvents();
    }

    public void updateInput() {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public Vector2f getMousePosition() {
        return mousePosition;
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

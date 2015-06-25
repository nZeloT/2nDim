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

package com.nzelot.engine.game;

import com.nzelot.engine.graphics.scenegraph.Entity;
import com.nzelot.engine.input.InputHandler;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * the base class for all games. it already provides a ready to go game loop. you only need to setup the scenegraph etc.
 */
//TODO: adapt to the new window class
public abstract class Game {

    protected Entity sceneGraph;

    private boolean running;

    private int targetFPS;

    private int width, oldWidth;
    private int height, oldHeight;
    private boolean fullscreen;

    private String windowTitle;
    private long windowID;

    public Game(int width, int height, boolean fullscreen, String windowTitle) {
        this.running    = false;
        this.targetFPS  = 60;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        this.windowTitle = windowTitle;
    }

    public void run() throws RuntimeException {
        if (!running) {
            initializeRenderer();

            initialize();

            if (sceneGraph == null)
                throw new IllegalStateException("No Scene Graph defined!");

            enterGameLoop();

            shutdown();

            shutdownRenderer();
        }
    }

    /**
     * initialize the rendering platform and set up required stuff
     *
     */
    private void initializeRenderer() throws RuntimeException {
        if (glfwInit() != GL_TRUE) throw new RuntimeException("Could not Initialize GLFW!");

        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        this.windowID = glfwCreateWindow(width, height, windowTitle, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

        if (windowID == NULL) throw new RuntimeException("Could not create Window!");

        ByteBuffer vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowID, (GLFWvidmode.width(vidMode) - width) / 2, (GLFWvidmode.height(vidMode) - height) / 2);

        glfwSetKeyCallback(windowID, new InputHandler(windowID));

        glfwMakeContextCurrent(windowID);
        glfwShowWindow(windowID);
        glfwSwapInterval(1);

        GLContext.createFromCurrent();

        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    }

    /**
     * called before the game loop actually starts. Use this method to set up your scenegraph etc.
     */
    protected abstract void initialize();

    private void enterGameLoop(){

        long lastTime = System.nanoTime();
        double delta = 0.0d;
        double ns = 1000000000.0 / targetFPS;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        this.running = true;

        while (running){

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1.0) {
                update(delta);
                updates++;
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " ups " + frames + " fps");
                updates = 0;
                frames = 0;
            }

            if (glfwWindowShouldClose(windowID) == GL_TRUE)
                running = false;
        }

    }

    /**
     * called after the game loop has ended. Use it to e.g. save the game state etc.
     */
    protected abstract void shutdown();

    /**
     * shutdown the rendering platform and tear down required stuff
     */
    public void shutdownRenderer() {
        //TODO: free remaining resources

        glfwDestroyWindow(windowID);
        glfwTerminate();

    }

    private void update(double delta) {

        glfwPollEvents();
        sceneGraph.updateAll(delta);

    }

    private void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        sceneGraph.renderAll();

        //Print occurring errors
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.err.println(error);
        }

        glfwSwapBuffers(windowID);

    }

    public void haltGameLoop() {
        this.running = false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void toggleFullScreen() {

        this.fullscreen = !this.fullscreen;

        ByteBuffer vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (fullscreen) {

            oldWidth = width;
            oldHeight = height;

            width = GLFWvidmode.width(vidMode);
            height = GLFWvidmode.height(vidMode);

        } else {
            width = oldWidth;
            height = oldHeight;
        }

        // Create new window
        long newWindowID = glfwCreateWindow(width, height, windowTitle, fullscreen ? glfwGetPrimaryMonitor() : NULL, windowID);
        glfwDestroyWindow(windowID);
        windowID = newWindowID;

        glfwSetKeyCallback(windowID, new InputHandler(windowID));

        glfwMakeContextCurrent(windowID);
        glfwSwapInterval(1);

        GLContext.createFromCurrent();

        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetWindowPos(windowID, (GLFWvidmode.width(vidMode) - width) / 2, (GLFWvidmode.height(vidMode) - height) / 2);

        glfwShowWindow(windowID);
    }
}

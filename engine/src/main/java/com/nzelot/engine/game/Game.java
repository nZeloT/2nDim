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

import com.nzelot.engine.graphics.Window;
import com.nzelot.engine.graphics.rendering.ShaderManager;
import com.nzelot.engine.graphics.rendering.VertexArrayManager;
import com.nzelot.engine.graphics.scenegraph.Universe;
import com.nzelot.engine.utils.logging.Logger;

/**
 * The base class for a game. Engine initialization and so forth (i.e. all the boilerplate code) is done for you.<br>
 *
 * Just use the four methods: <code>initGame(), exitGame(), update(float delta), render()</code> to build your game logic.
 *
 * @author nZeloT
 */
public abstract class Game {

    private static final String CLASS_NAME = Game.class.getName();

    private Universe universe;
    private Window window;

    private boolean running;
    private int targetFPS;

    //todo add doc
    public Game(int width, int height, boolean fullscreen, String windowTitle) {
        this.running    = false;
        this.targetFPS  = 60;
        this.window = new Window(windowTitle, width, height, fullscreen);
    }

    /**
     * Kick-off the game. Here starts the magic :)<br>
     * <p>
     * This just calls
     * <code><ul>
     * <li>initEngine()</li>
     * <li>initGame()</li>
     * <li>enterGameLoop()</li>
     * <li>endGame()</li>
     * <li>endEngine()</li>
     * </ul></code> in exact that order.
     */
    //todo update doc
    void run() {
        if (!running) {
            initEngine();

            universe = initGame();

            if (universe == null) {
                Logger.log(CLASS_NAME + ": No Universe defined!", Logger.LEVEL.ERROR);
                throw new IllegalStateException(CLASS_NAME + ": No Universe defined!");
            }

            enterGameLoop();

            endGame();

            endEngine();
        }
    }

    /**
     * init the rendering platform and set up required stuff. i.e. create the window and initialize the different managers.
     */
    private void initEngine() {
        if (!window.init()) {
            Logger.log(CLASS_NAME + ": Could not init Engine!", Logger.LEVEL.ERROR);
            throw new RuntimeException(CLASS_NAME + ": Could not Initialize Engine!");
        }

        VertexArrayManager.init();
        ShaderManager.init();
    }

    /**
     * this is called after <code>initEngine()</code> has set up the engine to start going.<br>
     * use it to setup your scenegraph and prepare for the game loop.
     *
     * @return the root object of the scenegraph. e.g. an instance of <code>Universe</code>
     */
    protected abstract Universe initGame();

    /**
     * the real game loop. calls <code>update()</code> and <code>render()</code> every frame.
     * it also checks for window close requests
     */
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
                Logger.log(updates + " ups " + frames + " fps");
                updates = 0;
                frames = 0;
            }

            if (window.closed())
                running = false;
        }

    }

    /**
     * called after the game loop has ended. Use it to e.g. save the game state and free self allocated data
     */
    protected abstract void endGame();

    /**
     * shutdown the rendering platform and tear down required stuff
     */
    private void endEngine() {
        this.window.exit();
    }

    /**
     * update the scenegraph the specified amount of time.<br>
     * this calls:
     * <code><ul>
     *     <li>Window.updateInput()</li>
     *     <li>updateGame(delta)</li>
     *     <li>SceneGraph.update(delta)</li>
     * </ul></code> in exactly this order.
     * @param delta the amount of time to update
     */
    protected void update(double delta) {

        window.updateInput();

        updateGame(delta);
        universe.update(delta);

    }

    /**
     * render the current game state to the screen.
     * it calls:
     * <code><ul>
     *     <li>Window.clear()</li>
     *     <li>renderGame()</li>
     *     <li>SceneGraph.render()</li>
     *     <li>Window.update()</li>
     * </ul></code> in that order.
     */
    protected void render() {

        window.clear();

        renderGame();
        universe.render();

        window.update();
    }

    /**
     * update the game
     * @param delta the delta time to update
     */
    protected abstract void updateGame(double delta);

    /**
     * prepare for rendering of the scene graph
     */
    protected abstract void renderGame();

    /**
     * halt the game immediately
     */
    void haltGameLoop() {
        this.running = false;
    }

    /**
     * provides access to the Window
     * @return the actual <code>Window</code> Object
     */
    public Window getWindow() {
        return window;
    }

    /**
     * provides access to the scene graph / world root
     *
     * @return the scene graph / world root node
     */
    public Universe getUniverse() {
        return universe;
    }
}

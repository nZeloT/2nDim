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
import com.nzelot.engine.graphics.scenegraph.Entity;
import com.nzelot.engine.utils.logging.Logger;

/**
 * the base class for all games. it already provides a ready to go game loop. you only need to setup the scenegraph etc.
 *
 * @author nZeloT
 */
//TODO: add some more doc
public abstract class Game {

    protected Entity world;
    protected Window window;
    private boolean running;
    private int targetFPS;

    public Game(int width, int height, boolean fullscreen, String windowTitle) {
        this.running    = false;
        this.targetFPS  = 60;
        this.window = new Window(windowTitle, width, height, fullscreen);
    }

    public void run() throws RuntimeException {
        if (!running) {
            initEngine();

            initGame();

            if (world == null) {
                Logger.log("No Scene Graph defined!", Logger.LEVEL.ERROR);
                throw new IllegalStateException("No Scene Graph defined!");
            }

            enterGameLoop();

            endGame();

            endEngine();
        }
    }

    /**
     * init the rendering platform and set up required stuff
     */
    private void initEngine() throws RuntimeException {
        if (!window.init()) {
            Logger.log("Could not init Engine!", Logger.LEVEL.ERROR);
            throw new RuntimeException("Could not Initialize Engine!");
        }

        VertexArrayManager.init();
        ShaderManager.init();
    }

    /**
     * called before the game loop actually starts. Use this method to set up your scenegraph etc.
     */
    protected abstract void initGame();

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
     * called after the game loop has ended. Use it to e.g. save the game state etc.
     */
    protected abstract void endGame();

    /**
     * shutdown the rendering platform and tear down required stuff
     */
    public void endEngine() {
        this.window.exit();
    }

    private void update(double delta) {

        window.updateInput();

        updateGame(delta);
        world.updateAll(delta);

    }

    private void render(){

        window.clear();

        renderGame();
        world.renderAll();

        window.update();
    }

    protected abstract void updateGame(double delta);

    protected abstract void renderGame();

    public void haltGameLoop() {
        this.running = false;
    }

}

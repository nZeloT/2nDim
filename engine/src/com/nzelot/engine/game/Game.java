package com.nzelot.engine.game;

import com.nzelot.engine.graphics.Entity;
import com.nzelot.engine.graphics.rendering.Renderer;

import java.util.List;
import java.util.Vector;

/**
 * the base class for all games. it already provides a ready to go game loop. you only need to setup the scenegraph etc.
 */
public abstract class Game {

    protected List<Entity> sceneGraph;

    private Renderer renderer;

    private boolean running;

    private int targetFPS;
    private int milliseconds;

    public Game(Renderer renderer) {
        this.running    = false;
        this.sceneGraph = new Vector<>();
        this.targetFPS  = 60;
        this.renderer   = renderer;

        this.milliseconds = (int) (1000.0d / targetFPS);
    }

    /**
     * called before the game loop actually starts. Use this method to set up your scenegraph etc.
     */
    protected abstract void initialize();

    /**
     * called after the game loop has ended. Use it to e.g. save the game state etc.
     */
    protected abstract void shutdown();


    public void run(){
        if(!running) {
            initialize();

            enterGameLoop();

            shutdown();
        }
    }

    private void enterGameLoop(){

        long oldTime = System.currentTimeMillis();
        long newTime;

        long delta;

        this.running = true;

        while (running){
            newTime = System.currentTimeMillis();

            delta = newTime - oldTime;

            if(delta < milliseconds){
                try {
                    Thread.sleep( milliseconds - delta );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                newTime = System.currentTimeMillis();

                delta = newTime - oldTime;
            }

            update(delta);
            render();

            oldTime = newTime;
        }

    }

    public void haltGameLoop(){
        this.running = false;
    }

    private void update(long delta){

        int n = sceneGraph.size();
        for (int i = 0; i < n; ++i) {
            sceneGraph.get(i).updateAll(delta);
        }

    }

    private void render(){

        int n = sceneGraph.size();
        for (int i = 0; i < n; ++i) {
            sceneGraph.get(i).renderAll(renderer);
        }

    }

    public int getTargetFPS() {
        return targetFPS;
    }
}

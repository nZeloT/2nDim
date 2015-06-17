package com.nzelot.engine.game;

public class Runtime {

    private static Runtime instance = new Runtime();
    private Game game;

    private Runtime() {
    }

    public static Runtime get() {
        return instance;
    }

    //TODO Add display mode settings
    public void runGame(Game game) {
        this.game = game;

        game.run();

    }

    public void stopGame() {

        if (game != null)
            game.haltGameLoop();

    }

}

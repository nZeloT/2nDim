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

import com.nzelot.engine.utils.logging.Logger;

/**
 * used to start a game. provides a possibility to stop the game due to the singleton implementation
 *
 * @author nZeloT
 */
//TODO: add some more doc
public class Runtime {

    private static final String CLASS_NAME = Runtime.class.getName();

    private static final Runtime instance = new Runtime();
    private Game game;

    //prevent instantiation
    private Runtime() {
    }

    public static Runtime get() {
        return instance;
    }

    public void runGame(Game game) throws IllegalStateException {

        if (this.game == null) {

            this.game = game;

            // use a method reference lambda. looks weird :P
            Thread gameThread = new Thread(game::run);

            gameThread.start();
        } else {
            Logger.log(CLASS_NAME + ": Tried to launch second game! This is currently not supported.", Logger.LEVEL.ERROR);
            throw new IllegalStateException("Game already running!");
        }
    }

    public void stopGame() {

        if (game != null)
            game.haltGameLoop();

    }

}

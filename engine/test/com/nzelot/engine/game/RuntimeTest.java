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

import com.nzelot.engine.graphics.rendering.Color;
import com.nzelot.engine.graphics.rendering.Texture;
import com.nzelot.engine.graphics.scenegraph.ColoredSquare;
import com.nzelot.engine.graphics.scenegraph.TexturedSquare;
import com.nzelot.engine.graphics.scenegraph.Universe;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * some random playground to test features
 *
 * @author nZeloT
 */
public class RuntimeTest {

    public static void main(String[] args) {
        Game g = new Game(1280, 720, false, "Test") {

            private TexturedSquare pic;
            private ColoredSquare col;

            @Override
            protected void initGame() {
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_DEPTH_TEST);
                glActiveTexture(GL_TEXTURE1);

                world = new Universe();

                pic = new TexturedSquare(
                        new Vector3f(0, 0, 0),
                        new Texture("engine/testdata/images/rhino.png")
                );
                col = new ColoredSquare(
                        new Vector3f(0, 1, 0),
                        new Color(0.2f, 0.4f, 0.8f)
                );

                world.addChild(pic);
                world.addChild(col);

            }

            @Override
            protected void endGame() {
            }

            @Override
            protected void updateGame(double delta) {
                pic.rotate(1.0f);
                col.rotate(-1.0f);
            }

            @Override
            protected void renderGame() {

            }
        };

        Runtime.get().runGame(g);
    }
}
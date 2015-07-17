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

package com.nzelot.sandbox;

import com.nzelot.engine.game.Game;
import com.nzelot.engine.game.Runtime;
import com.nzelot.engine.graphics.rendering.Color;
import com.nzelot.engine.graphics.scenegraph.PhysicalRectangle;
import com.nzelot.engine.graphics.scenegraph.Universe;
import com.nzelot.engine.utils.logging.Logger;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.Vector;

/**
 * some random playground to test new features
 *
 * @author nZeloT
 */
public class RuntimeTest {

    private static final String CLASS_NAME = RuntimeTest.class.getName();

    public static double STEP = (2*Math.PI) / 300.0d;

    public static void main(String[] args) {

        Logger.log(CLASS_NAME + ": Welcome to the Sandbox!", Logger.LEVEL.INFO);

        Game g = new Game(1280, 720, false, "Sandbox") {

            private PhysicalRectangle red;
            private PhysicalRectangle blu;
            private PhysicalRectangle gre;

            private Vector<PhysicalRectangle> qubes;

            private int c = 0;

            @Override
            protected Universe initGame() {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL13.glActiveTexture(GL13.GL_TEXTURE1);

                Matrix4f pr_matrix = new Matrix4f();
                pr_matrix.identity().setOrtho(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);

                Universe universe = new Universe(pr_matrix);

                red = new PhysicalRectangle(
                        "Red",
                        3,0.5,
                        new Color(0.8f, 0.2f, 0.4f)
                );

                blu = new PhysicalRectangle(
                        "Blu",
                        0.5, 2,
                        new Color(0.2f, 0.4f, 0.8f)
                );

                gre = new PhysicalRectangle(
                        "Gre",
                        10, 0.5,
                        new Color(0.4f, 0.8f, 0.2f)
                );

                universe.addObject(red);
                universe.addObject(blu);
                universe.addObject(gre);

                red.translate(1.5, 0);
                blu.translate(3, -0.75);
                gre.translate(0, -2.5);

                red.addWeldJoint(blu, new Vector2(3, 0));

                blu.setAutoSleepingEnabled(false);

                red.setMass(Mass.Type.INFINITE);
                gre.setMass(Mass.Type.INFINITE);
                red.setAngularVelocity(-100*STEP);


                for (int i = 0; i < 10; i++) {
                    PhysicalRectangle o = new PhysicalRectangle("Q" + i, 0.5,0.5, new Color(0.4f,0.4f,0.4f));
                    universe.addObject(o);
                }

                return universe;
            }

            @Override
            protected void endGame() {
            }

            @Override
            protected void updateGame(double delta) {

            }

            @Override
            protected void renderGame() {

            }
        };

        Runtime.runGame(g);
    }
}
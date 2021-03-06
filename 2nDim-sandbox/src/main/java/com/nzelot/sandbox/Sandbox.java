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
import com.nzelot.engine.graphics.scenegraph.*;
import com.nzelot.engine.utils.logging.Logger;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * some random playground to test new features
 *
 * @author nZeloT
 */
public class Sandbox {

    public static void main(String[] args) {

        Logger.setCurrentOutputLevel(Logger.LEVEL.INFO);
        Logger.log(Sandbox.class, "Welcome to the Sandbox!", Logger.LEVEL.INFO);

        Game g = new Game(1280, 720, false, "2nDim Sandbox! Enjoy :)") {

            private Vehicle vehic;
            private GameObject floor;
            private Camera camera;

            @Override
            protected Universe initGame() {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL13.glActiveTexture(GL13.GL_TEXTURE1);

                Universe universe = new Universe(this);
                //welcome to the moon :D
                universe.setGravity(new Vector2(0, -9.81f * 1/16.0f));
                camera = universe.getMainCamera();

                floor = new ColoredRectangle(
                        "Floor",
                        20, 0.5,
                        new Color(0.4f, 0.8f, 0.2f)
                );

                vehic = new Vehicle(universe);

                universe.addObject(floor);

                floor.translate(0, -2.5);
                floor.setMass(Mass.Type.INFINITE);

                //Generate the Bridge :D
                GameObject prev = floor;
                GameObject curr = null;

                for (int i = 0; i < 31; i++) {
                    curr = new ColoredRectangle("BridgeElem" + i, 1, 0.25, randomColor());
                    universe.addObject(curr);

                    curr.translate(-14.5 + i, 4);

                    RevoluteJoint rev = prev.addRevoluteJoint(curr, new Vector2(-15+i,4));
                    rev.setLimitEnabled(false);
                    rev.setCollisionAllowed(false);

                    prev = curr;
                }

                RevoluteJoint rev = curr.addRevoluteJoint(floor, new Vector2(16,4));
                rev.setLimitEnabled(false);
                rev.setCollisionAllowed(false);

                return universe;
            }

            @Override
            protected void endGame() {
            }

            @Override
            protected void updateGame(double delta) {
                //some weird rotation to test the camera
                // looks pretty WTF :D
                camera.rotate(0.0125f);
                //camera.translate(0.1f,0.1f);
            }

            @Override
            protected void renderGame() {

            }
        };

        Runtime.runGame(g);
    }

    public static Color randomColor(){
        return new Color(
                (float)Math.random() * 0.5f + 0.5f,
                (float)Math.random() * 0.5f + 0.5f,
                (float)Math.random() * 0.5f + 0.5f,
                1.0f);
    }
}

class Vehicle {

    public GameObject car;
    public GameObject wheel1;
    public GameObject wheel2;

    public Vehicle(Universe universe){
        car = new ColoredRectangle(
                "Car",
                3,0.5,
                new Color(0.8f, 0.2f, 0.4f)
        );

        wheel1 = new Circle(
                "Wheel1",
                0.25,
                new Color(0.2f, 0.4f, 0.8f)
        );

        wheel2 = new Circle(
                "Wheel2",
                0.25,
                new Color(0.2f, 0.4f, 0.8f)
        );

        universe.addObject(car);
        universe.addObject(wheel1);
        universe.addObject(wheel2);

        car.translate(-3, 6.25);
        wheel1.translate(-4, 5.6);
        wheel2.translate(-2, 5.6);

        WheelJoint m1 = car.addWheelJoint(wheel1, new Vector2(-4, 5.6), new Vector2(0, 1));
        WheelJoint m2 = car.addWheelJoint(wheel2, new Vector2(-2, 5.6), new Vector2(0, 1));

        m1.setFrequency(8);
        m1.setDampingRatio(0.4);
        m1.setMotorEnabled(true);
        m1.setMotorSpeed(Math.toRadians(180));
        m1.setMaximumMotorTorque(1000);
        m1.setCollisionAllowed(true);

        m2.setFrequency(8);
        m2.setDampingRatio(0.4);
        m2.setMotorEnabled(false);
        m2.setCollisionAllowed(true);
    }
}
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

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;


public class Dyn4JPlayground {

    /** The scale 45 pixels per meter */
    public static final double SCALE = 45.0;

    /** The conversion factor from nano to base */
    public static final double NANO_TO_BASE = 1.0e9;

    /**
     * Custom Body class to add drawing functionality.
     * @author William Bittle
     * @version 3.0.2
     * @since 3.0.0
     */
    public static class GameObject extends Body {
        /** The color of the object */
        protected float[] color = new float[4];

        /**
         * Default constructor.
         */
        public GameObject() {
            // randomly generate the color
            this.color[0] = (float)Math.random() * 0.5f + 0.5f;
            this.color[1] = (float)Math.random() * 0.5f + 0.5f;
            this.color[2] = (float)Math.random() * 0.5f + 0.5f;
            this.color[3] = 1.0f;
        }

        /**
         * Draws the body.
         * <p>
         * Only coded for polygons.
         */
        public void render() {
            // save the original transform
            GL11.glPushMatrix();

            // transform the coordinate system from world coordinates to local coordinates
            GL11.glTranslated(this.transform.getTranslationX(), this.transform.getTranslationY(), 0.0);
            // rotate about the z-axis
            GL11.glRotated(Math.toDegrees(this.transform.getRotation()), 0.0, 0.0, 1.0);

            // loop over all the body fixtures for this body
            for (BodyFixture fixture : this.fixtures) {
                // get the shape on the fixture
                Convex convex = fixture.getShape();
                // check the shape type
                if (convex instanceof Polygon) {
                    // since Triangle, Rectangle, and Polygon are all of
                    // type Polygon in addition to their main type
                    Polygon p = (Polygon) convex;

                    // set the color
                    GL11.glColor4f(this.color[0], this.color[1], this.color[2], 0);

                    // fill the shape
                    GL11.glBegin(GL11.GL_POLYGON);
                    for (Vector2 v : p.getVertices()) {
                        GL11.glVertex3d(v.x, v.y, 0.0);
                    }
                    GL11.glEnd();

                    // set the color
                    GL11.glColor4f(this.color[0] * 0.8f, this.color[1] * 0.8f, this.color[2] * 0.8f, 1.0f);

                    // draw the shape
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    for (Vector2 v : p.getVertices()) {
                        GL11.glVertex3d(v.x, v.y, 0.0);
                    }
                    GL11.glEnd();
                }
                // circles and other curved shapes require a little more work, so to keep
                // this example short we only include polygon shapes; see the RenderUtilities
                // in the Sandbox application
            }

            // set the original transform
            GL11.glPopMatrix();
        }
    }

    /** The dynamics engine */
    protected World world;

    /** The time stamp for the last iteration */
    protected long last;

    private long windowID;
    private boolean running;

    /**
     * Default constructor for the window
     */
    public Dyn4JPlayground() {

        GLFW.glfwInit();

        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);

        windowID = GLFW.glfwCreateWindow(1280, 720, "dyn4j", MemoryUtil.NULL, MemoryUtil.NULL);

        GLFW.glfwMakeContextCurrent(windowID);
        GLFW.glfwSwapInterval(1);

        GLContext.createFromCurrent();

        GLFW.glfwShowWindow(windowID);

        init();

        // setup the world
        this.initializeWorld();
    }

    /**
     * Creates game objects and adds them to the world.
     * <p>
     * Basically the same shapes from the Shapes test in
     * the TestBed.
     */
    protected void initializeWorld() {
        // create the world
        this.world = new World();

        // create all your bodies/joints

        // create the floor
        Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(Mass.Type.INFINITE);
        // move the floor down a bit
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);

        // create a triangle object
        Triangle triShape = new Triangle(
                new Vector2(0.0, 0.5),
                new Vector2(-0.5, -0.5),
                new Vector2(0.5, -0.5));
        GameObject triangle = new GameObject();
        triangle.addFixture(triShape);
        triangle.setMass();
        triangle.translate(-1.0, 2.0);
        // test having a velocity
        triangle.getLinearVelocity().set(5.0, 0.0);
        this.world.addBody(triangle);

        // try a rectangle
        Rectangle rectShape = new Rectangle(1.0, 1.0);
        GameObject rectangle = new GameObject();
        rectangle.addFixture(rectShape);
        rectangle.setMass();
        rectangle.translate(0.0, 2.0);
        rectangle.getLinearVelocity().set(-5.0, 0.0);
        this.world.addBody(rectangle);

        // try a polygon with lots of vertices
        Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
        GameObject polygon = new GameObject();
        polygon.addFixture(polyShape);
        polygon.setMass();
        polygon.translate(-2.5, 2.0);
        // set the angular velocity
        polygon.setAngularVelocity(Math.toRadians(-20.0));
        this.world.addBody(polygon);

        GameObject issTri = new GameObject();
        issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
        issTri.setMass();
        issTri.translate(2.0, 3.0);
        this.world.addBody(issTri);

        GameObject equTri = new GameObject();
        equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
        equTri.setMass();
        equTri.translate(3.0, 3.0);
        this.world.addBody(equTri);

        GameObject rightTri = new GameObject();
        rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
        rightTri.setMass();
        rightTri.translate(4.0, 3.0);
        this.world.addBody(rightTri);
    }

    /**
     * Start active rendering the example.
     * <p>
     * This should be called after the JFrame has been shown.
     */
    public void start() {
        // initialize the last update time
        this.last = System.nanoTime();

        this.running = true;

        while (running){

            display();

            GLFW.glfwSwapBuffers(windowID);

            if (GLFW.glfwWindowShouldClose(windowID) == GL11.GL_TRUE)
                running = false;
        }
    }


    public void init() {

        // set the matrix mode to projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        // initialize the matrix
        GL11.glLoadIdentity();
        // set the view to a 2D view
        GL11.glOrtho(-400, 400, -300, 300, 0, 1);

        // switch to the model view matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // initialize the matrix
        GL11.glLoadIdentity();

        // set the clear color to white
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void display() {

        // clear the screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        // switch to the model view matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // initialize the matrix (0,0) is in the center of the window
        GL11.glLoadIdentity();

        // render the scene
        this.render();

        // perform other operations at the end (it really
        // doesn't matter if its done at the start or end)
        this.update();
    }

    /**
     * Updates the Example and World.
     */
    protected void update() {
        // get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
        // convert from nanoseconds to seconds
        double elapsedTime = (double)diff / NANO_TO_BASE;
        // update the world with the elapsed time
        this.world.update(elapsedTime);
    }

    /**
     * Renders the example.
     */
    protected void render() {
        // apply a scaling transformation
        GL11.glScaled(SCALE, SCALE, SCALE);

        // lets move the view up some
        GL11.glTranslated(0.0, -1.0, 0.0);

        // draw all the objects in the world
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            // get the object
            GameObject go = (GameObject) this.world.getBody(i);
            // draw the object
            go.render();
        }
    }

    /**
     * Entry point for the example application.
     * @param args command line arguments
     */
    public static void main(String[] args) {

        Dyn4JPlayground window = new Dyn4JPlayground();
        // start it
        window.start();
    }
}
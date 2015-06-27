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

package com.nzelot.engine.graphics.scenegraph;


import org.joml.Vector3f;

/**
 * @author nZeloT
 */
public interface Shape {

    /**
     * Check whether the two shapes collide
     *
     * @param shape the shape to be checked
     * @return true if the two shapes collide
     */
    boolean collide(Shape shape);

    /**
     * Checks whether the specified point is within the shape
     *
     * @param point the point to check
     * @return true if the point is within the shape
     */
    boolean contains(Vector3f point);

    /**
     * Checks whether the specified shape is within the shape
     *
     * @param shape the shape to check
     * @return true if the point is within the shape
     */
    boolean contains(Shape shape);

    /**
     * join two shape to a new one
     *
     * @param shape the shape to join with
     * @param mode  the join mode; Either the intersection or the united shape will be created
     * @return the resulting shape
     */
    Shape join(Shape shape, JoinMode mode);

    /**
     * @return an rectangular shaped bounding box
     */
    Vector3f[] getBoundingBox();

    /**
     * this describes the two possible join modes for a shape
     */
    enum JoinMode {

        INTERSECTION,
        UNION

    }
}

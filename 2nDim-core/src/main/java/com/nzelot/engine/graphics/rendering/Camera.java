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

package com.nzelot.engine.graphics.rendering;

import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.nzelot.engine.utils.Constants.*;

/**
 * @author nZeloT
 */
//doc
public class Camera {

    private static final String CLASS_NAME = Camera.class.getName();

    private Vector3f position;
    private @Getter float rotation;
    private @Getter float zoomLevel;
    private @Getter float screenRation;

    private boolean recalcCamera;
    private boolean recalcProjection;

    private Matrix4f projectionMat;
    private Matrix4f cameraMat;

    private Vector2f dummy;
    private Matrix4f tmpRot;

    //doc
    public Camera(Vector3f position, float screenRation, float zoomLevel) {
        this.position = new Vector3f(position);
        this.rotation = 0;

        this.zoomLevel = zoomLevel;
        this.screenRation = screenRation;

        this.projectionMat = new Matrix4f();
        this.cameraMat = new Matrix4f();

        this.dummy = new Vector2f();
        this.tmpRot = new Matrix4f();

        recalcCamera = true;
        recalcProjection = true;
        recalcProjectionMat();
        recalcCameraMat();
    }

    //doc
    public Camera(Vector3f position, float screenRation) {
        this(position, screenRation, 20);
    }

    //doc
    public Camera(float screenRation) {
        this(new Vector3f(), screenRation, 20);
    }

    //doc
    public Camera(float screenRation, float zoomLevel) {
        this(new Vector3f(), screenRation, zoomLevel);
    }

    //doc
    public void translate(float x, float y){
        position.add(x, y, 0);
        recalcCamera = true;
    }

    //doc
    public void rotate(float rad){
        this.rotation += rad;

        //bring the rotation back into the desired value range
        while(this.rotation < 0)
            this.rotation += TWO_PI;

        while(this.rotation > TWO_PI)
            this.rotation -= TWO_PI;

        recalcCamera = true;
    }

    //doc
    public void setPosition(float x, float y){
        position.set(x, y, 0);
        recalcCamera = true;
    }

    //doc
    public Vector2f getPosition(){
        dummy.x = position.x;
        dummy.y = position.y;
        return dummy;
    }

    //doc
    public void setRotation(float rad) {
        if (rad < 0) {
            rad = 0;
            Logger.log(CLASS_NAME + ": tried set the rotation below 0. This is not allowed! " +
                    "The rotation to be set was: " + rad + ". It was instead set to 0.", Logger.LEVEL.WARNING);
        }

        if (rad > TWO_PI) {
            rad = (float) TWO_PI;
            Logger.log(CLASS_NAME + ": tried to set the rotation above 2 * Pi. This is not allowed! " +
                    "The rotation to be set was: " + rad + ". It was instead set to 2 * Pi.", Logger.LEVEL.WARNING);
        }

        this.rotation = rad;
        this.recalcCamera = true;
    }

    //doc
    public void zoom(float factor){
        zoomLevel += factor;

        if(zoomLevel < 1) {
            zoomLevel = 1;
            Logger.log(CLASS_NAME + ": tried to lower the zoom level below 1. This is not allowed! " +
                    "The zoom factor to be set was: " + factor + ". It was instead set to 1.", Logger.LEVEL.WARNING);
        }
        if(zoomLevel > 75) {
            zoomLevel = 75;
            Logger.log(CLASS_NAME + ": tried to raise the zoom level above 75. This is not allowed! " +
                    "The zoom factor to be set was: " + factor + ". It was instead set to 75.", Logger.LEVEL.WARNING);
        }

        recalcProjection = true;
    }

    //doc
    public void setScreenRation(int width, int height){
        if(width <= 0 || height <= 0){
            Logger.log(CLASS_NAME + " tried to set a new Screen Ratio with negative values!", Logger.LEVEL.ERROR);
            throw new IllegalArgumentException(CLASS_NAME + " tried to set a new Screen Ratio with negative values!");
        }

        screenRation = (float)height / (float)width;
        recalcProjection = true;
    }

    //doc
    public void setScreenRation(float ratio){
        if(ratio <= 0){
            Logger.log(CLASS_NAME + ": tried to lower the screen ratio below 1. This is not allowed! " +
                    "The zoom factor to be set was: " + ratio + ". It was instead set to 1.", Logger.LEVEL.WARNING);
            ratio = 1;
        }

        this.screenRation = ratio;
    }

    //doc
    public Matrix4f getCameraMat(){
        recalcCameraMat();
        return cameraMat;
    }

    //doc
    public Matrix4f getProjectionMat(){
        recalcProjectionMat();
        return projectionMat;
    }

    //doc
    private void recalcCameraMat(){
        if(recalcCamera){
            cameraMat.identity();
            cameraMat.translate(position);
            cameraMat.rotateZ(rotation);
            recalcCamera = false;
        }
    }

    //doc
    private void recalcProjectionMat(){
        if(recalcProjection){
            projectionMat.identity();
            projectionMat.setOrtho2D(
                    -1 * zoomLevel,                 // left
                    1 * zoomLevel,                  //right
                    -1 * zoomLevel * screenRation,  //bottom
                    1 * zoomLevel * screenRation    //top
            );
            recalcProjection = false;
        }
    }

}

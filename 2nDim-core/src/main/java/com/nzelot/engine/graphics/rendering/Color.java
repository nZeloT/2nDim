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
import lombok.NonNull;
import org.joml.Vector4f;

//doc

/**
 * @author nZeloT
 */
public class Color {

    private static final float FACTOR = 0.75f;

    public Vector4f color;

    public Color(@NonNull Vector4f color) {
        this.color = new Vector4f(color);
    }

    public Color(float r, float g, float b, float a) {
        this.color = new Vector4f(r, g, b, a);
    }

    public Color(float r, float g, float b) {
        this.color = new Vector4f(r, g, b, 1.0f);
    }

    public Color(int r, int g, int b){
        this(r, g, b, 0);
    }

    public Color(int r, int g, int b, int a){
        this.color = new Vector4f();
        setRed(r);
        setGreen(g);
        setBlue(b);
        setAlpha(a);
    }

    public Vector4f asVector4f() {
        return color;
    }

    public String asRGBHexCode(){
        int c =   ((int)(getRed()   *255)) << 16
                | ((int)(getGreen() *255)) <<  8
                | ((int)(getBlue()  *255));
        return Integer.toHexString(c);
    }

    public float getRed() {
        return color.x;
    }

    public void setRed(float r) {
        if(checkBoundsFloat(r))
            color.x = r;
    }

    public void setRed(int r){
        if(checkBoundsInt(r))
            color.x = r / 255.0f;
    }

    public float getGreen() {
        return color.y;
    }

    public void setGreen(float g) {
        if(checkBoundsFloat(g))
            color.y = g;
    }

    public void setGreen(int g){
        if(checkBoundsInt(g))
            color.y = g / 255.0f;
    }

    public float getBlue() {
        return color.z;
    }

    public void setBlue(float b) {
        if(checkBoundsFloat(b))
            color.z = b;
    }

    public void setBlue(int b){
        if(checkBoundsInt(b))
            color.z = b / 255.0f;
    }

    public float getAlpha() {
        return color.w;
    }

    public void setAlpha(float a) {
        if(checkBoundsFloat(a))
            color.w = a;
    }

    public void setAlpha(int a) {
        if (checkBoundsInt(a))
            color.w = a / 255.0f;
    }

    public Color brighter() {
        int r = (int) (getRed()*255);
        int g = (int) (getGreen()*255);
        int b = (int) (getBlue()*255);
        int alpha = (int) (getAlpha()*255);

        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            setAlpha(alpha);
            setBlue(i);
            setRed(i);
            setGreen(i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int) (r / FACTOR), 255),
                Math.min((int) (g / FACTOR), 255),
                Math.min((int) (b / FACTOR), 255),
                alpha);
    }

    public Color darker() {
        return new Color(
                Math.max((int)(getRed()*255  *FACTOR), 0),
                Math.max((int)(getGreen()*255*FACTOR), 0),
                Math.max((int)(getBlue()*255 *FACTOR), 0),
                (int)(getAlpha()*255));
    }

    private boolean checkBoundsFloat(float f){
        if(f < 0 || f > 1) {
            Logger.log(Color.class, "Tried to set a component value out of Range!", Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to set a component value out of Range!");
        }

        return true;
    }

    private boolean checkBoundsInt(int f){
        if(f < 0 || f > 255) {
            Logger.log(Color.class, "Tried to set a component value out of Range!", Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to set a component value out of Range!");
        }

        return true;
    }
}

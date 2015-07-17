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

import lombok.NonNull;
import org.joml.Vector4f;

//TODO: add some doc

/**
 * @author nZeloT
 */
public class Color {

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

    public Vector4f asVector4f() {
        return color;
    }

    public float getRed() {
        return color.x;
    }

    public void setRed(float r) {
        color.x = r;
    }

    public float getGreen() {
        return color.y;
    }

    public void setGreen(float g) {
        color.x = g;
    }

    public float getBlue() {
        return color.z;
    }

    public void setBlue(float b) {
        color.x = b;
    }

    public float getAlpha() {
        return color.w;
    }

    public void setAlpha(float a) {
        color.x = a;
    }

}

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 nZeloT
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

import com.nzelot.engine.graphics.rendering.Texture;

/**
 * @author nZeloT
 */
public class AnimatedSprite extends Sprite {

    private int texCount;
    private double speed;

    private double passed;

    public AnimatedSprite(String name, double sizeX, double sizeY, Texture tex, int current, int texCount, double speed, int texPerRow, float texWidth, float texHeight) {
        super(name, sizeX, sizeY, tex, current, texPerRow, texWidth, texHeight);
        this.texCount = texCount;
        this.speed = speed;
        this.passed = 0;
    }

    @Override
    public void update(double delta) {
        super.update(delta);

        passed += delta;
        if(passed >= speed){
            passed = 0;
            current = (current+1)%texCount;
            changed = true;
        }
    }
}

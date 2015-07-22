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

import com.nzelot.engine.definition.Manager;
import com.nzelot.engine.utils.logging.Logger;
import lombok.NonNull;

import java.util.Map;

/**
 * @author nZeloT
 */
public class FrameBufferManager extends Manager<FrameBuffer> {

    //fixme this is only temporary
    public static final FrameBufferManager instance = new FrameBufferManager();

    //prevent instantiation
    private FrameBufferManager() {
    }

    public FrameBuffer create(@NonNull String key,
                              int width,
                              int heigth){
        if (objects.containsKey(key)) {
            Logger.log(FrameBufferManager.class, "Tried to store already stored FrameBuffer with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to store already stored FrameBuffer with key: " + key);
        }

        FrameBuffer fbo = new FrameBuffer(width, heigth);
        objects.put(key, fbo);

        return fbo;
    }

    @Override
    protected void initSTD(Map<String, FrameBuffer> map) {
        //NOP No STANDARD FBO's so far. And I cannot think of any ...
    }
}

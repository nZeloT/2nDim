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

package com.nzelot.engine.definition;

import com.nzelot.engine.graphics.rendering.ShaderManager;
import com.nzelot.engine.utils.logging.Logger;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nZeloT
 */
public abstract class Manager<E extends ManagedObject> {

    protected Map<String, E> objects;

    private boolean init;

    protected abstract void initSTD(Map<String, E> map);

    public E get(@NonNull String key) {
        E s = objects.get(key);

        if (s == null) {
            Logger.log(ShaderManager.class, "Tried to access non existent managed object with key: " + key, Logger.LEVEL.WARNING);
        }

        return s;
    }

    public void init() {
        //prevent from calling this multiple times
        if (!init) {

            objects = new HashMap<>();

            //load all the standard objects as defined by the implementing managers
            initSTD(objects);

            init = true;
        }
    }

    public void exit(){
        if(init){

            Set<Map.Entry<String, E>> entries = objects.entrySet();
            for (Map.Entry<String, E> entry : entries) {
                entry.getValue().delete();
            }

            objects.clear();
            objects = null;

            init = false;
        }
    }
}

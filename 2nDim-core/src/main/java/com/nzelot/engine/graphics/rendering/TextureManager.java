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
import com.nzelot.engine.utils.FileUtils;
import com.nzelot.engine.utils.ResourceUtils;
import com.nzelot.engine.utils.logging.Logger;
import lombok.Getter;
import lombok.NonNull;

import java.io.InputStream;
import java.util.Map;

/**
 * @author nZeloT
 */
public class TextureManager extends Manager<Texture> {

    public static final TextureManager instance = new TextureManager();

    //prevent further instantiation
    private TextureManager(){
    }

    public Texture get(STANDARD std){
        return get(std.getKey());
    }

    public Texture create(@NonNull String key, @NonNull String texFile){
        if (objects.containsKey(key)) {
            Logger.log(VertexArrayManager.class, "Tried to store already stored Texture with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to store already stored Texture with key: " + key);
        }

        Texture t = new Texture(FileUtils.getInputStream(texFile));
        objects.put(key, t);

        return t;
    }

    public Texture create(@NonNull String key, @NonNull InputStream texFile){
        if (objects.containsKey(key)) {
            Logger.log(VertexArrayManager.class, "Tried to store already stored Texture with key: " + key, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to store already stored Texture with key: " + key);
        }

        Texture t = new Texture(texFile);
        objects.put(key, t);

        return t;
    }

    @Override
    protected void initSTD(Map<String, Texture> map) {
        //load all the standard shader
        Texture t;

        STANDARD[] standards = STANDARD.values();
        for (STANDARD standard : standards) {
            t = new Texture(ResourceUtils.getResourceStream(standard.getFileName()));

            objects.put(standard.getKey(), t);
        }
    }

    public enum STANDARD {
        NOT_FOUND("com.nzelot.2nDim.tex.not_found", "res/tex/not_found.png");

        private @Getter String fileName;
        private @Getter String key;

        STANDARD(String key, String fileName) {
            this.key = key;
            this.fileName = fileName;
        }
    }
}

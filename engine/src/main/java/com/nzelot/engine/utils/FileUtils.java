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

package com.nzelot.engine.utils;

import com.nzelot.engine.utils.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * taken from https://github.com/TheCherno/Flappy/tree/master/src/com/thecherno/flappy/utils
 * <p>
 * added some improvements
 *
 * @author TheCherno
 * @author nZeloT
 */
//TODO: add some doc
public class FileUtils {

    private static final String CLASS_NAME = FileUtils.class.getName();

    //prevent instantiation
    private FileUtils() {
    }

    public static String loadAsString(String file) {

        //Does file exist?
        if (!new File(file).exists()) {
            Logger.log(CLASS_NAME + ": Tried to read non existing file: " + file, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to read non existing file: " + file);
        }

        String r = null;

        try {
            r = ResourceUtils.readIntoString(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return r;
    }

}

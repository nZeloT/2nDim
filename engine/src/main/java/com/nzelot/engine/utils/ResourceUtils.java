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
import lombok.NonNull;

import java.io.*;

/**
 * similar to <code>FileUtils</code> but using the <code>System.class.getResourceAsStream()</code> Method for reading the data instead of the <code>File</code> API
 *
 * @author nZeloT
 */
public class ResourceUtils {

    private static final String CLASS_NAME = ResourceUtils.class.getName();

    //prevent instantiation
    private ResourceUtils() {
    }

    public static String loadAsString(String resourceName) {
        InputStream in = getResourceStream(resourceName);
        return ResourceUtils.readIntoString(new InputStreamReader(in));
    }

    public static InputStream getResourceStream(String resourceName){
        InputStream in =  ResourceUtils.class.getClassLoader().getResourceAsStream(resourceName);

        if (in == null) {
            Logger.log(CLASS_NAME + ": Tried to read non existing resource: " + resourceName, Logger.LEVEL.ERROR);
            throw new IllegalArgumentException("Tried to read non existing resource: " + resourceName);
        }

        return in;
    }

    public static String readIntoString(@NonNull Reader in) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(in);
            String buffer;
            while ((buffer = br.readLine()) != null)
                result.append(buffer).append("\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}

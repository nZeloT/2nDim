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

package com.nzelot.engine.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple Resource Manager with some basic features
 */
//TODO: rework
public class ResourceManager {

    private static final ResourceManager instance = new ResourceManager();
    private final Map<String, IResourceLoader> loader;

    //******************************************************************************************************************
    private final Map<String, Resource> resourceMap;
    private ResourceManager(){
        loader      = new HashMap<>();
        resourceMap = new HashMap<>();
    }

    public static ResourceManager get() {
        return instance;
    }

    /**
     * Add a new ResourceLoader to load a resource file. Resource files need to end with @type
     * @param type the file extension of the resources files to be loaded by
     * @param loader the IResourceLoader Object used to load the resources
     */
    public void addResourceLoader(String type, IResourceLoader loader){
        this.loader.put(type.toUpperCase(), loader);
    }

    /**
     * Removes the specified IResourceLoader from the list of used resource loaders
     * @param type the file extension to which the IResourceLoader is registered
     */
    public void removeResourceLoader(String type){
        this.loader.remove(type.toUpperCase());
    }

    /**
     * Initiates the loading mechanism. It iterates through every file from every sub-directory and tries to load it
     * with the resource loader specified for the concrete file extension.
     * @param dir the directory to start the scan
     */
    public void loadResources(File dir){
        loadResources(dir, "/");
    }

    /**
     * Retrieve one single, loaded resource
     *
     * @param identifier the resource identifier
     * @return the loaded resource
     */
    public Resource getResource(String identifier){
        Resource r = resourceMap.get(identifier);
        return r.getClass().cast(r);
    }

    private void loadResources(File dir, String prefix){

        File[] files = dir.listFiles();

        if(files != null) {
            for (File f : files) {

                if (f.isDirectory())
                    loadResources(f, prefix + f.getName() + "/");

                String extension = "";
                int i = f.getName().lastIndexOf('.');
                if (i >= 0)
                    extension = f.getName().substring(i + 1);

                extension = extension.toUpperCase();

                if (loader.containsKey(extension)) {
                    Resource r = loader.get(extension).load(f);

                    if (r != null) {
                        resourceMap.put(prefix + f.getName().substring(0, i), r);

                        System.out.println(prefix + f.getName().substring(0, i));
                    }
                }
            }
        }
    }

}

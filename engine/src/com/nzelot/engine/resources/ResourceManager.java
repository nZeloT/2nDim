package com.nzelot.engine.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple Resource Manager with some basic features
 */
public class ResourceManager {

    private static ResourceManager instance = new ResourceManager();

    public static ResourceManager get(){
        return instance;
    }

    //******************************************************************************************************************

    private Map<String, IResourceLoader>    loader;
    private Map<String, Resource>           resourceMap;


    private ResourceManager(){
        loader      = new HashMap<>();
        resourceMap = new HashMap<>();
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

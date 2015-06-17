package com.nzelot.engine.resources;

import java.io.File;

public interface IResourceLoader {

    /**
     * Method that gets called to a registered IResourceLoader when a file with the specified file extension is to be
     * loaded
     * @param f the file to be loaded
     * @return the resource object which contains the necessary data
     */
    Resource load(File f);

}

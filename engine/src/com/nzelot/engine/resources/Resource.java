package com.nzelot.engine.resources;

/**
 * A single loaded resource of type T
 * @param <T> the resource type
 */
public class Resource<T> {

    private T data;

    public Resource(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

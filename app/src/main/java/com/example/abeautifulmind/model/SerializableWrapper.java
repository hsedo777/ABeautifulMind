package com.example.abeautifulmind.model;

import java.io.Serializable;

public class SerializableWrapper<T extends Serializable> implements Serializable {
    final private T object;

    public SerializableWrapper(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}

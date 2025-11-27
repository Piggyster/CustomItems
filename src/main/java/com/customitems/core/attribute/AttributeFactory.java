package com.customitems.core.attribute;

import java.lang.reflect.InvocationTargetException;

public class AttributeFactory<T> {

    private final Class<? extends Attribute<T>> clazz;
    private String key;

    public AttributeFactory(Class<? extends Attribute<T>> clazz) {
        this.clazz = clazz;

        Attribute<T> attribute = newInstance();
        if(attribute != null) {
            key = attribute.getKey();
        }
    }

    public String getKey() {
        return key;
    }

    public Attribute<T> newInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}

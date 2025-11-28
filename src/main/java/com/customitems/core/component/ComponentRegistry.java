package com.customitems.core.component;

import com.customitems.core.component.impl.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentRegistry {

    static {
        register(UniqueComponent.class);
        register(InteractableComponent.class);
        register(CategoryComponent.class);
        register(BackpackComponent.class);
        register(ArmorComponent.class);
    }

    private static Map<String, Method> componentDeserializers;

    public static void register(Class<? extends Component> clazz) {
        if(componentDeserializers == null) {
            componentDeserializers = new ConcurrentHashMap<>();
        }

        String key = null;

        try {
            Field field = clazz.getDeclaredField("KEY");
            field.setAccessible(true);
            key = (String) field.get(null);
        } catch(NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        Method deserializer = null;
        try {
            deserializer = clazz.getDeclaredMethod("deserialize", JsonElement.class);
            deserializer.setAccessible(true);
        } catch(NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        componentDeserializers.put(key, deserializer);
    }

    public static Component deserialize(String key, JsonElement json) {
        Method deserializer = componentDeserializers.get(key);
        if(deserializer == null) return null;

        try {
            Component component = (Component) deserializer.invoke(null, json);
            return component;
        } catch (InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

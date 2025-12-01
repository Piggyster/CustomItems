package com.customitems.core.component;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.impl.*;
import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ComponentRegistry {

    static {
        register(UniqueComponent.class);
        register(InteractableComponent.class);
        register(CategoryComponent.class);
        register(BackpackComponent.class);
        register(ArmorComponent.class);
        register(CraftableComponent.class);
        register(ModelComponent.class);
        register(PickaxeComponent.class);
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
            try {
                deserializer = clazz.getDeclaredMethod("deserialize", JsonElement.class, String.class);
                deserializer.setAccessible(true);
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        componentDeserializers.put(key, deserializer);
    }

    public static Component deserialize(String key, JsonElement json, String templateId) {
        Method deserializer = componentDeserializers.get(key);
        if(deserializer == null) return null;

        try {
            Component component = null;
            if(deserializer.getParameterCount() == 2) {
                component = (Component) deserializer.invoke(null, json, templateId);
            } else {
                component = (Component) deserializer.invoke(null, json);
            }


            return component;
        } catch (InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

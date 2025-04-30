package com.customitems.core.property;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertyRegistry {

    private static final Map<String, Function<ReadWriteNBT, Property>> nbtFactories = new ConcurrentHashMap<>();
    private static final Map<String, Function<JsonObject, Property>> jsonFactories = new ConcurrentHashMap<>();


    public static void registerNbt(String propertyType, Function<ReadWriteNBT, Property> factory) {
        nbtFactories.put(propertyType, factory);
    }

    public static void registerJson(String propertyType, Function<JsonObject, Property> factory) {
        jsonFactories.put(propertyType, factory);
    }

    public static Property fromNbt(String propertyType, ReadWriteNBT nbt) {
        Function<ReadWriteNBT, Property> factory = nbtFactories.get(propertyType);
        if (factory == null) {
            return null;
        }
        return factory.apply(nbt);
    }

    public static Property fromJson(String propertyType, JsonObject json) {
        Function<JsonObject, Property> factory = jsonFactories.get(propertyType);
        if (factory == null) {
            return null;
        }
        return factory.apply(json);
    }

    public static boolean hasJsonFactory(String propertyType) {
        return jsonFactories.containsKey(propertyType);
    }

    public static boolean hasNbtFactory(String propertyType) {
        return nbtFactories.containsKey(propertyType);
    }
}

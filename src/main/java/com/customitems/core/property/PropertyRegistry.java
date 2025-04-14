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

    private static final Map<String, Function<ReadWriteNBT, Property>> propertyFactories = new ConcurrentHashMap<>();
    private static final Map<String, Function<JsonObject, Property>> jsonPropertyFactories = new ConcurrentHashMap<>();


    public static void registerPropertyFactory(String propertyType, Function<ReadWriteNBT, Property> factory) {
        propertyFactories.put(propertyType, factory);
    }

    public static void registerJsonFactory(String propertyType, Function<JsonObject, Property> factory) {
        jsonPropertyFactories.put(propertyType, factory);
    }

    public static Function<ReadWriteNBT , Property> getFactory(String propertyType) {
        return propertyFactories.get(propertyType);
    }

    public static Function<JsonObject, Property> getJsonFactory(String propertyType) {
        return jsonPropertyFactories.get(propertyType);
    }

    public static boolean hasFactory(String propertyType) {
        return propertyFactories.containsKey(propertyType);
    }

    public static boolean hasJsonFactory(String propertyType) {
        return jsonPropertyFactories.containsKey(propertyType);
    }

    public static Property createProperty(String propertyType, ReadWriteNBT nbt) {
        Function<ReadWriteNBT, Property> factory = propertyFactories.get(propertyType);
        if (factory == null) {
            return null;
        }

        return factory.apply(nbt);
    }

    public static Property createPropertyFromJson(String propertyType, JsonObject json) {
        Function<JsonObject, Property> factory = jsonPropertyFactories.get(propertyType);
        if (factory == null) {
            return null;
        }

        return factory.apply(json);
    }
}

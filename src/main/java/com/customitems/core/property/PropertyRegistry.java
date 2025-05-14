package com.customitems.core.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Registry for properties.
 * <p>
 * This class is responsible for registering and retrieving properties based on their identifiers.
 * It supports both JSON and NBT formats for property creation.
 */

public class PropertyRegistry {

    private final Map<String, PropertyType<?>> byId;
    private final Map<Class<? extends Property>, PropertyType<?>> byClass;

    public PropertyRegistry() {
        byId = new ConcurrentHashMap<>();
        byClass = new ConcurrentHashMap<>();
    }

    public <T extends Property> void register(PropertyType<T> type) {
        byId.put(type.id(), type);
        byClass.put(type.clazz(), type);
    }


    public PropertyType<?> getType(String id) {
        return byId.get(id);
    }

    public PropertyType<?> getType(Class<? extends Property> clazz) {
        return byClass.get(clazz);
    }

    public Property fromNbt(String id, ReadableNBT nbt) {
        PropertyType<?> type = getType(id);
        return type == null ? null : type.nbtFactory().apply(nbt);
    }

    public Property fromJson(String id, JsonElement json) {
        PropertyType<?> type = getType(id);
        return type == null ? null : type.jsonFactory().apply(json);
    }

}

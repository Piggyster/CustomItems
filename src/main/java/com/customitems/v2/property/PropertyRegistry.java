package com.customitems.v2.property;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertyRegistry {

    private final Map<String, Class<? extends Property>> identifiers;
    private final Map<Class<? extends Property>, Function<JsonObject, Property>> jsonFactories;
    private final Map<Class<? extends Property>, Function<ReadableNBT, Property>> nbtFactories;

    public PropertyRegistry() {
        identifiers = new ConcurrentHashMap<>();
        jsonFactories = new ConcurrentHashMap<>();
        nbtFactories = new ConcurrentHashMap<>();
    }

    public void register(@NotNull Class<? extends Property> clazz, @NotNull String identifier,
                         @Nullable Function<JsonObject, Property> jsonFunction,
                         @Nullable Function<ReadableNBT, Property> nbtFunction) {
        identifiers.put(identifier.toLowerCase(), clazz);
        if(jsonFunction != null) {
            jsonFactories.put(clazz, jsonFunction);
        }
        if(nbtFunction != null) {
            nbtFactories.put(clazz, nbtFunction); //TODO strengthen these checks
        }
    }

    public @Nullable Property fromJson(Class<? extends Property> clazz, JsonObject json) {
        Function<JsonObject, Property> function = jsonFactories.get(clazz);
        if(function == null) return null;
        return function.apply(json);
    }

    public @Nullable Property fromNbt(Class<? extends Property> clazz, ReadableNBT nbt) {
        Function<ReadableNBT, Property> function = nbtFactories.get(clazz);
        if(function == null) return null;
        return function.apply(nbt);
    }

    public boolean hasJsonFactory(Class<? extends Property> clazz) {
        return jsonFactories.containsKey(clazz);
    }

    public boolean hasNbtFactory(Class<? extends Property> clazz) {
        return nbtFactories.containsKey(clazz);
    }

    public @Nullable Class<? extends Property> getClass(String identifer) {
        return identifiers.get(identifer.toLowerCase());
    }
}

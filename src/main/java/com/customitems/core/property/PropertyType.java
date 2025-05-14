package com.customitems.core.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record PropertyType<T extends Property>(String id, Class<T> clazz, @Nullable Function<JsonElement, T> jsonFactory, @Nullable Function<ReadableNBT, T> nbtFactory) {

    @Override
    public String toString() {
        return id;
    }

    public static <T extends Property> Builder<T> of(Class<T> clazz, String id) {
        return new Builder<>(clazz, id);
    }

    public static final class Builder<T extends Property> {

        private final Class<T> clazz;
        private final String id;
        private Function<JsonElement, T> jsonFactory;
        private Function<ReadableNBT, T> nbtFactory;

        public Builder(Class<T> clazz, String id) {
            this.clazz = clazz;
            this.id = id;
        }

        public Builder<T> json(Function<JsonElement, T> jsonFactory) {
            this.jsonFactory = jsonFactory;
            return this;
        }

        public Builder<T> nbt(Function<ReadableNBT, T> nbtFactory) {
            this.nbtFactory = nbtFactory;
            return this;
        }


        public PropertyType<T> build() {
            return new PropertyType<>(id, clazz, jsonFactory, nbtFactory);
        }
    }
}

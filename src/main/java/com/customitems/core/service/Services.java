package com.customitems.core.service;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Services {

    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private Services() {}

    public static <T> void register(@NotNull Class<T> serviceClass, @NotNull T service) {
        services.put(serviceClass, service);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }
}
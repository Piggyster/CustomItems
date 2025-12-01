package com.customitems.core.attribute;

import com.customitems.core.attribute.impl.BackpackDataAttribute;
import com.customitems.core.attribute.impl.PrimalAttribute;
import com.customitems.core.attribute.impl.UniqueAttribute;
import com.google.common.collect.ImmutableList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeRegistry {

    static {
        register(UniqueAttribute.class);
        register(BackpackDataAttribute.class);
        register(PrimalAttribute.class);
    }

    private static Map<Class<? extends Attribute<?>>, AttributeFactory<?>> factories;

    public static <T> void register(Class<? extends Attribute<T>> clazz) {
        if(factories == null) {
            factories = new ConcurrentHashMap<>();
        }

        AttributeFactory<T> factory = new AttributeFactory<>(clazz);
        factories.put(clazz, factory);
    }

    public static Collection<AttributeFactory<?>> getFactories() {
        return ImmutableList.copyOf(factories.values());
    }

    //TODO method to scan package for easy addition

}

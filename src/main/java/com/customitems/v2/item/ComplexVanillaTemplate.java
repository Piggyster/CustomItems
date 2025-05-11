package com.customitems.v2.item;

import com.customitems.v2.property.Property;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ComplexVanillaTemplate extends VanillaTemplate {

    private final Set<Supplier<Property>> defaultPropertySuppliers;

    public ComplexVanillaTemplate(Material material, Set<Supplier<Property>> defaultPropertySuppliers) {
        super(material);
        this.defaultPropertySuppliers = defaultPropertySuppliers;
    }

    @Override
    public Set<Property> getDefaultProperties() { //TODO
        Set<Property> properties = new HashSet<>();
        for(Supplier<Property> supplier : defaultPropertySuppliers) {
            properties.add(supplier.get());
        }
        return properties;
    }
}

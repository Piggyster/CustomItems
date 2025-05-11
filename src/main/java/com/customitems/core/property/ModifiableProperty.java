package com.customitems.core.property;

/**
 * Represents a property that can be modified.
 *
 * @param <T> the type of modification
 */

public interface ModifiableProperty<T extends PropertyModification> extends Property {

    void modify(T mod);

}

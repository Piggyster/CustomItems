package com.customitems.core.property;

/**
 * Represents a modification to a property.
 * This can be extended and used any way you'd like
 */

public interface PropertyModification {

    enum Operation {
        ADD,
        MULTIPLY
    }
}

package com.customitems.core.property;

/**
 * Enum representing the priority levels of properties.
 * The order of the properties is as follows:
 * 1. MASTER - Properties that usually receive modifications
 * 2. INTER - Properties that do simple modifications, like addition
 * 3. FINAL - And properties that should be last, like final multiplication
 *
 */

public enum PropertyPriority {
    MASTER,
    INTER,
    FINAL

}

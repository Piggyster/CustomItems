package com.customitems.core.property.impl;

import com.customitems.core.property.PropertyModification;

public record StatModification(StatType type, Operation operation, int value) implements PropertyModification {

}

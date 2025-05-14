package com.customitems.core.property.impl;

import com.customitems.core.property.PropertyModification;
import com.customitems.core.stat.StatType;

public record StatModification(StatType type, Operation operation, int value) implements PropertyModification {

}

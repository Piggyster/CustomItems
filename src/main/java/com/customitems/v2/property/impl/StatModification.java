package com.customitems.v2.property.impl;

import com.customitems.v2.property.PropertyModification;

public record StatModification(StatType type, Operation operation, int value) implements PropertyModification {

}

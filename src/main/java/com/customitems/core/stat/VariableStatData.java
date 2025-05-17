package com.customitems.core.stat;

public class VariableStatData extends StatData {

    private double current;

    public VariableStatData(double max) {
        super(max);
        this.current = max;
    }

    @Override
    public double getCurrent() {
        return current;
    }

    public VariableStatData current(double current) {
        this.current = current;
        return this;
    }
}

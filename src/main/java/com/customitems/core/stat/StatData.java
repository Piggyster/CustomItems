package com.customitems.core.stat;

public class StatData {

    private double max;

    public StatData(double max) {
        this.max = max;
    }

    public double getMax() {
        return max;
    }

    public double getCurrent() {
        return max;
    }

    public StatData set(double max) {
        this.max = max;
        return this;
    }
}

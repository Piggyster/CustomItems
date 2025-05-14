package com.customitems.core.stat;

import java.util.Map;

public interface StatProvider {

    void applyStats(Map<StatType, Double> stats);

    StatPhase getPhase();
}

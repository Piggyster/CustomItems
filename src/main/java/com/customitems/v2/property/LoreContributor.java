package com.customitems.v2.property;

import java.util.List;

public interface LoreContributor {

    int getLorePriority();

    void contributeLore(LoreVisitor visitor);

}

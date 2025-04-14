package com.customitems.core.property;

import java.util.List;

public interface LoreContributor {

    default int getLorePriority() {
        return 0;
    }

    List<String> contributeLore();
}

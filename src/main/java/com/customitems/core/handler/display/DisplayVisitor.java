package com.customitems.core.handler.display;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisplayVisitor {

    private String displayName;
    private final List<String> lore;

    public DisplayVisitor() {
        displayName = "";
        lore = new ArrayList<>();
    }

    public DisplayVisitor addLore(String line) {
        lore.add(line);
        return this;
    }

    public DisplayVisitor addLore(Collection<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public DisplayVisitor setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}

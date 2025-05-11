package com.customitems.v2.property;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoreVisitor {

    private List<String> lore;

    public LoreVisitor() {
        lore = new LinkedList<>();
    }

    public void visit(String line) {
        lore.add(line);
    }

    public void visit(List<String> lines) {
        lore.addAll(lines);
    }
}

package com.customitems.core.property;

import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;

/**
 * LoreVisitor is a class that collects lore lines.
 * It allows adding single lines or lists of lines to the lore.
 */

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

    public List<String> getLore() {
        return lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList();
    }
}

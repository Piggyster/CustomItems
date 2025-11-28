package com.customitems.core.component.impl;

import com.customitems.core.component.Component;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public class CategoryComponent extends Component {

    private static final String KEY = "category";

    public static CategoryComponent deserialize(JsonElement json) {
        return new CategoryComponent(json.getAsString());
    }

    private String category;

    public CategoryComponent(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}

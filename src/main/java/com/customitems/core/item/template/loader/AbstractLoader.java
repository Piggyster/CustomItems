package com.customitems.core.item.template.loader;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractLoader implements TemplateLoader {

    protected final File directory;
    protected final Gson gson;

    public AbstractLoader(File directory) {
        this.directory = directory;
        gson = new GsonBuilder().setPrettyPrinting().create();

        if(!directory.exists()) {
            directory.mkdirs();
            createExampleTemplate();
        }
    }


    @Override
    public List<Template> loadAllTemplates() {
        List<Template> templates = new ArrayList<>();
        try {
            List<File> files = listJsonFiles(directory);
            for (File file : files) {
                try {
                    Template template = loadTemplate(file);
                    if (template != null) {
                        templates.add(template);
                        Bukkit.getLogger().info("Loaded template: " + template.getId() + " from " + file.getName());
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Failed to load template from " + file.getName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to list template files: " + e.getMessage());
        }
        return templates;
    }

    @Override
    public List<File> listJsonFiles(File dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir.toPath(), 1)) {
            return stream
                    .filter(path -> !path.equals(dir.toPath()))
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }
}

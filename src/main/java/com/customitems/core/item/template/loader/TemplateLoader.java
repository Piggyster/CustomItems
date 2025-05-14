package com.customitems.core.item.template.loader;

import com.customitems.core.item.template.Template;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TemplateLoader {

    /**
     * Loads all templates from the specified directory.
     *
     * @return a list of loaded templates
     */
    List<Template> loadAllTemplates();

    /**
     * Loads a template from a JSON file.
     *
     * @param file the JSON file to load the template from
     * @return the loaded template
     */
    Template loadTemplate(File file);

    /**
     * Lists all JSON files in the specified directory.
     *
     * @param directory the directory to search for JSON files
     * @return a list of JSON files
     */
    List<File> listJsonFiles(File directory) throws IOException;

    /**
     * Creates an example template file in the specified directory.
     */
    void createExampleTemplate();
}

/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Max
 */
public class Schema extends CouchDbDocument {

    private String schema = "http://json-schema.org/draft-04/schema#",
            description, title, type;
    List<String> required;
    Map<String, Map<String, String>> properties;

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public String getSchema() {
        return schema;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        setId(title);
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Schema{" + "schema=" + schema + ", description=" + description + ", title=" + title + ", type=" + type + ", required=" + required + ", properties=" + properties + '}';
    }

    public Schema() {
        description = title = type = "";
        required = new ArrayList<>();
        properties = new HashMap<>();
    }

    public Map<String, Map<String, String>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Map<String, String>> properties) {
        this.properties = properties;
    }

}

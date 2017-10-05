/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.JSON;

import de.offenedaten.koeln.control.ReplacementManager;
import java.util.HashMap;
import java.util.Map;
import de.offenedaten.koeln.entity.schema.Schema;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Der SchemaCreator erzeugt ein JSON-Schema. Damit diese jedoch den Standards
 * eines JSON-Schemas entsprechen müssen für manche Daten, die Datentypen
 * konvertiert werden, so wird beispielsweise aus „ESRIDouble“ „decimal“.
 *
 * @author Max
 */
public class SchemaCreator {

    /**
     * Erzeugt aus dem fieldsArray das Schema, was in den Daten der DKAN API
     * immer enthalten ist.
     *
     * @param fieldsArray Daten zum Erzeugen des Schemas.
     * @param className Name des Schemas.
     * @param description Beschreibung die man dem Schema hinzufügen kann.
     * @return Schema der Daten.
     */
    public static Schema createSchema(JSONArray fieldsArray, String className, String description) {
        if (fieldsArray != null) {
            Map<String, Map<String, String>> types = new HashMap<>();
            for (int i = 0; i < fieldsArray.length(); i++) {
                Map<String, String> attr = new HashMap<>();
                JSONObject obj = fieldsArray.getJSONObject(i);
                String name = obj.optString("name");
                if (name.equals("")) {
                    name = obj.optString("id");
                }
                String type = obj.optString("type");
                attr.put("type", ReplacementManager.replaceInvalidKeys(type));
                types.put(name, attr);
            }
            Schema s = new Schema();
            s.setTitle(className);
            s.setDescription(description);
            s.setType("object");
            s.setProperties(types);
            return s;
        }
        return null;
    }
}

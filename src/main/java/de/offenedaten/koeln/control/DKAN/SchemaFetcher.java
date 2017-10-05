/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.DKAN;

import de.offenedaten.koeln.control.JSON.SchemaCreator;
import de.offenedaten.koeln.entity.dataset.Resource;
import de.offenedaten.koeln.entity.schema.Schema;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Die SchemaFetcher-Klasse extrahiert die Fields-Attribute und gibt diese an
 * den SchemaCreator weiter, da diese für die Erzeugung des JSON-Schemas
 * vonnöten sind. Diese Klasse implementiert das Konzept des Schema-Monitorings,
 * das für NoSQL-Datenbanken und Sammlung von JSON-Dateien erforderlich ist,
 * wenn das System während seiner Laufzeit auf Änderungen und neue Schemata
 * reagieren soll (vgl. [DBAP5]).
 *
 * @author Max
 */
public class SchemaFetcher {

    /**
     * Erzeugt ein Schema von einer angegeben Resource.
     *
     * @param r Resource von der das Schema erstellt werden soll.
     * @return Schemaklasse der Resourcen-Daten wird automatisch beim schreiben
     * in ein JSON-Schema umgewandelt.
     */
    public static Schema fetchSchema(Resource r) {
        JSONObject mainObj = ResourceFetcher.getJsonFromURL(r.getUrl());
        JSONArray fieldsArray = mainObj.optJSONArray("fields");
        if (fieldsArray == null) {
            fieldsArray = mainObj.optJSONObject("result").optJSONArray("fields");
        }
        return SchemaCreator.createSchema(fieldsArray, r.getName().replace(" ", ""), "");
    }
}

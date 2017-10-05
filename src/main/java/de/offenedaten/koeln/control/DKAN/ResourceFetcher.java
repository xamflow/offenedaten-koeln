/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.DKAN;

import de.offenedaten.koeln.control.ReplacementManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.offenedaten.koeln.entity.poi.POIContainer;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Sobald die „DkanApi“-Klasse die Datensätze erhalten hat, werden vom
 * ResourceFetcher, die entsprechenden Daten aus der URL abgefragt. Die Daten
 * liegen im JSON-Format vor, welche extrahiert werden und in eine Key-Value
 * basierte Liste (HashMap) eingefügt werden, welche ein Teil der
 * „POIContainer“-Klasse ist. Desweiteren kümmert sich die Klasse um einige
 * Fehlformatierungen der Daten um diese einheitlicher zu gestallten, dazu ist
 * unter anderem notwendig zu erkennen, ob die Daten vom ARCgis-Geoframework
 * bereitgestellt werden oder nicht.
 *
 * @author Max
 */
public class ResourceFetcher {

    private static final String FETCHING = "Fetching data for: ",
            RECORDS_ARRAY_ESRI = "features",
            ATTRIBUTES_OBJ_ESRI = "attributes",
            GEOMETRY_OBJ_ESRI = "geometry";

    /**
     * /LF10/ /LF30/
     * Extrahiert die JSON-Daten und instanziiert eine
     * POIContainer-Klasse mit den auf der URL gefunden JSON-Daten und trägt
     * idKey als _id ein.
     *
     * @param url URL zu den JSON Daten
     * @param idKey Primärschlüssel attribut in den JSON-Daten
     * @return Eine Liste von POIContainer Objekten, aus den JSON Daten der
     * angegebenen URL.
     */
    public static ArrayList<POIContainer> fetchPOI(String url, String idKey) {
        ArrayList<POIContainer> list = new ArrayList<>();
        JSONObject mainObj = getJsonFromURL(url);
        if (isEsriFormatted(mainObj)) {
            list = getEsriData(mainObj, idKey);
        } else {
            JSONObject tmpObj = mainObj.optJSONObject("result");
            JSONArray mainArray = tmpObj.optJSONArray("records");
            JSONArray fieldsArray = mainObj.optJSONArray("fields");
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject currentObj = mainArray.getJSONObject(i);
                POIContainer pc = new POIContainer();
                Iterator<?> keys = currentObj.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (key.equals(idKey)) {
                        pc.setId(currentObj.optString(key));
                    }
                    pc.addAttribute(ReplacementManager.replaceInvalidKeys(key), currentObj.optString(key));
                }
                pc.getAttributes().put("x", String.valueOf(pc.getX())); // maybe comment
                pc.getAttributes().put("y", String.valueOf(pc.getY())); //
                list.add(pc);
            }
        }

//        if (currentObj.get(key) instanceof JSONObject) 
        return list;
    }

    /**
     * /LF10/ /LF30/
     * Überprüft ein JSONObject, ob es Esri formatiert ist.
     *
     * @param mainObj JSONObject das es zu überprüfen gilt.
     * @return True, falls es Esri-Formatiert ist, sonst False.
     */
    private static boolean isEsriFormatted(JSONObject mainObj) {
        if (mainObj.has("result")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * /LF10/ /LF30/
     * Holt JSON-Daten von einer URL und speichert sie in einem JSONObject.
     *
     * @param url URL zu den JSON-Daten
     * @return JSONObject mit unter url gefundenen JSON-Daten
     */
    public static JSONObject getJsonFromURL(String url) {
        url = url.replace("http://", "https://");
        URL u;
        try {
            u = new URL(url);
            String htmlData = new Scanner(u.openStream()).useDelimiter("\\Z").next();
            return new JSONObject(htmlData);

        } catch (IOException ex) {
            Logger.getLogger(ResourceFetcher.class
                    .getName()).log(Level.SEVERE, null, ex);
            return new JSONObject();
        }

    }

    /**
     * /LF10/ /LF30/
     * Funktion zum extrahieren von Esri formatierten Daten.
     *
     * @param mainObj JSONObject, welches die Daten enthält
     * @param idKey Primärschlüssel, welcher für das _id Attributfeld
     * eingetragen werden soll.
     * @return Liste an POIContainer-Objekten, welche aus dem mainObj extrahiert
     * werden konnten.
     */
    private static ArrayList<POIContainer> getEsriData(JSONObject mainObj, String idKey) {
        ArrayList<POIContainer> list = new ArrayList<>();
        JSONArray mainArray = mainObj.getJSONArray(RECORDS_ARRAY_ESRI);
        JSONArray fieldsArray = mainObj.getJSONArray("fields");
        for (int i = 0; i < mainArray.length(); i++) {
            JSONObject obj = mainArray.getJSONObject(i);
            JSONObject attrObj = obj.getJSONObject(ATTRIBUTES_OBJ_ESRI);
            JSONObject geoObj = obj.getJSONObject(GEOMETRY_OBJ_ESRI);
            POIContainer pc = new POIContainer();
            Iterator<?> keys = attrObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equals(idKey)) {
                    String str = attrObj.optString(key);
                    /*
                     Abfangen eines Keys der keine Value hat und der Versuch es 
                     durch die Esri eigene ObjectID zu ersetzen, es gilt aber 
                     dennoch aufgrund der Datenqualität darauf zu achten das 
                     jeder Primary-Key auch einen sinnvollen Wert besitzt!
                     */
                    if (!str.isEmpty()) {
                        pc.setId(str);
                    } else {
                        pc.setId(attrObj.optString("OBJECTID"));
                    }
                }
                pc.addAttribute(key, attrObj.optString(key));
            }
            //Punktbasierte POI Daten 
            String x = String.valueOf(geoObj.optDouble("x"));
            String y = String.valueOf(geoObj.optDouble("y"));
            if (!x.equals("NaN") && !y.equals("NaN")) {
                pc.addAttribute("x", x);
                pc.addAttribute("y", y);
            };

            //Pfadbasierte POI Daten
            JSONArray pointArray = null;
            if (geoObj.optJSONArray("paths") != null) {
                pointArray = geoObj.optJSONArray("paths");
            }
            //Ring/Areal basierte POI Daten
            if (geoObj.optJSONArray("rings") != null) {
                pointArray = geoObj.optJSONArray("rings");
            }

            //Wenn eines der beiden vorliegt, füge werden die Punkte hinzugefügt
            if (pointArray != null) {
                /**
                 * Da ein "rings/paths" Array mehrere Ringe/Pfade haben kann,
                 * eine Umweltzone/LKW Verbotszone o.ä. allerdings nur einen
                 * Ring/Pfad hat pro Datensatz hat. Kann das Array für hierfür
                 * übersprungen werden.
                 */
                pointArray = pointArray.getJSONArray(0);
                addPointsToList(pc, pointArray);
            }
            list.add(pc);
        }
        return list;
    }

    /**
     * /LF10/ /LF30/
     * Wird nur von Klassen verwendet,welche nicht Punktbasiert sind. Linien der
     * Stadtbahn, Umweltzonen oder ähnliches.
     *
     * @param pc Point of Interest, welchem die Punkte hinzugefügt werden
     * sollen.
     * @param pointArray ARCgis GeoObj welches die geodätischen Daten beinhaltet
     */
    private static void addPointsToList(POIContainer pc, JSONArray pointArray) {
        for (int k = 0; k < pointArray.length(); k++) {
            String x = pointArray.getJSONArray(k).optString(0);
            String y = pointArray.getJSONArray(k).optString(1);
            pc.addPointToList(x, y);
        }
    }
}

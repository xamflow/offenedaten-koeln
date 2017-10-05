/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control;

import de.offenedaten.koeln.control.CouchDb.CouchDbManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Der ReplacementManager ist eine Klasse, die alle zu ersetzenden Datentypen
 * und Keys aus der „replacements“-Datei extrahiert. Dies ist z.B. nötig um die
 * ArcGIS-Datentypen in JSON-Schema konforme Datentypen umzuwandeln. Viel mehr
 * noch übernimmt die Klasse das ersetzen von falschen Keys, so das einheitlich
 * alle Geokoordinaten den Key „x“ bzw. „y“ bekommen anstelle von
 * „GeokoordinateOst“ o.ä. . In der Klasse können darüber hinaus,
 * Kommentarzeichen, Trennzeichen und Encoding für Zeichenketten und der
 * Dateiname geändert werden. Über die „replacements“ Datei können, weitere
 * ungewünschte Dateitypen eingetragen werden und wodurch diese zu ersetzt
 * werden. Die Syntax lautet wie folgt:
 *
 * replacement ::= Zu ersetzender Datentyp:Neuer Datentyp (vgl. [DBAP5])
 *
 * @author Max
 */

public class ReplacementManager {

    private static final String FILENAME = "replacements";
    private static final String ENCODING = "UTF-8";
    private static final String COMMENT_SIGN = "#";
    private static final String SEPARATOR = ":";

    /**
     * Liest die Daten der replacement Datei aus und stellt diese als HashMap
     * zur Verfügung. Da dies besonders günstig zur Überprüfung und zum ersetzen
     * der falschen Daten ist.
     *
     * @return HashMap. Key = ungewünschter String, zu ersetzen durch Value.
     */
    private static HashMap<String, String> getReplacementList() {
        File file = new File(FILENAME);
        HashMap<String, String> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(COMMENT_SIGN)) {
                    String[] strArray = line.toLowerCase().split(SEPARATOR);
                    list.put(strArray[0], strArray[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CouchDbManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CouchDbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * /LF10/ /LF30/
     * Funktion die von anderen Klassen aufgerufen werden kann. Somit wird die
     * Übersicht in der aufrufenden Klasse verbessert, da sich die Klasse nicht
     * um das beschaffen der Liste und die Überprüfung der Werte.
     *
     * @param givenKey im Datensatz gefundener Schlüssel, welcher überprüft
     * werden soll.
     * @return Erwünschter Schlüssel. Falls es ein erwünschter Schlüssel war
     * wird givenKey zurückgegeben, ansonsten wird der entsprechende Value der
     * HashMap zurückgegeben, welcher den erwünschten Schlüssel enthält.
     */
    public static String replaceInvalidKeys(String givenKey) {

        HashMap<String, String> replacementMap = getReplacementList();

        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (givenKey.toLowerCase().equals(key)) {
                return value;
            }
        }
        return givenKey;
    }
}

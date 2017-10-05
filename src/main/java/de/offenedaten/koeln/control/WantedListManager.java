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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Die Klasse WantedListManager, kümmert sich darum bestimmte Ressourcen aus dem
 * System „http://www.offenedaten-koeln.de“ in das CouchDB System einzulesen.
 * Die „Ressource“ Datei ist dafür Zuständig anzugeben, welche Ressourcen vom
 * System erfasst werden. Darüber hinaus legt sie fest, wie der Segmentname in
 * der Datenbank lauten soll und welches JSON-Attribut als Primary Key
 * herangezogen wird (um damit das „_id“ Attribut zu füllen). Die Syntax der
 * Datei ist wie folgt aufgebaut:
 * ressource ::= Name/Teilstring der ressource:Segmentname:Primary-Key-Attribut
 * In der Klasse können darüber hinaus, Kommentarzeichen, Trennzeichen und
 * Encoding für Zeichenketten und der Dateiname geändert werden.
 *
 * @author Max
 */
public class WantedListManager {

    private static final String FILENAME = "resource";
    private static final String ENCODING = "UTF-8";
    private static final String COMMENT_SIGN = "#";
    private static final String SEPARATOR = ":";

    /**
     * /LF10/ /LF20/ /LF30/
     * Liest die Daten der resource Datei aus und stellt diese als Liste
     * zur Verfügung. Jedes Listenelement besteht aus
     * [0] = Name der Resource
     * [1] = Segmentname / Datenbankname
     * [2] = Primärschlüssel
     *
     * @return Liste der Elemente aus der resource Datei.
     */
    public static ArrayList<String[]> getWantedList() {
        File file = new File(FILENAME);
        ArrayList<String[]> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(COMMENT_SIGN)) {
                    String[] strArray = line.split(SEPARATOR);
                    strArray[0] = strArray[0].toLowerCase();
                    //Um eine gewisse Syntax einzuhalten
                    if (strArray.length == 3) {
                        list.add(strArray);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CouchDbManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CouchDbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}

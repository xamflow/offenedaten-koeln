/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.poi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import org.ektorp.support.CouchDbDocument;

/**
 * Diese Klasse bildet die in den Ressourcen enthaltenen Daten ab und wird
 * benutzt um die Daten per Ektorp Bibliothek komfortabel in der Datenbank
 * abspeichern zu können. Diese Klasse verfügt über Funktionen, welche einem für
 * einen gegebenen Schlüssel den korrespondierenden Wert zurückgeben. Darüber
 * hinaus gibt es noch eine Funktion, welche dem Benutzer den Wert für den
 * Schlüssel x bzw. y zurückgibt, da jeder POI Punkt über eine geodätische
 * Position verfügt. Es gibt jedoch auch Datensätze die Beispielsweise eine
 * Strecke oder Fläche abbilden, dafür gibt es eine Liste an Double Arrays mit
 * den Koordinaten die sogenannte pointList, welche entweder die Fläche
 * umspannen oder den Streckenverlauf abbilden.
 *
 * @author Max
 */
public class POIContainer extends CouchDbDocument {

    private HashMap<String, String> attributes;
    private ArrayList<Double[]> pointList;

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    @JsonIgnore
    public double getX() {
        String x = attributes.getOrDefault("x", "0");
        if (x == null) {
            return 0.0;
        }
        x = x.replace(",", ".");
        return Double.valueOf(x);
    }

    @JsonIgnore
    public double getY() {
        String y = attributes.getOrDefault("y", "0");
        if (y == null) {
            return 0.0;
        }
        y = y.replace(",", ".");
        return Double.valueOf(y);
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public String toString() {
        return "id=" + getId() + " rev=" + getRevision() + " attributes=" + attributes;
    }

    public POIContainer(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public POIContainer(HashMap<String, String> attributes, ArrayList<Double[]> pointList) {
        this.attributes = attributes;
        this.pointList = pointList;
    }

    public POIContainer() {
        this.attributes = new HashMap<>();
        this.pointList = new ArrayList<>();
    }

    public ArrayList<Double[]> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<Double[]> pointList) {
        this.pointList = pointList;
    }

    public void addPointToList(String x, String y) {
        Double[] point = new Double[2];
        point[0] = Double.valueOf(x);
        point[1] = Double.valueOf(y);
        this.pointList.add(point);
    }

}

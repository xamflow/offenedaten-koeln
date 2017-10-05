/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control;

import de.offenedaten.koeln.boundary.POIMenuItem;
import de.offenedaten.koeln.control.CouchDb.CouchDbManager;
import de.offenedaten.koeln.entity.poi.POIContainer;
import java.util.ArrayList;
import org.ektorp.CouchDbConnector;

/**
 * Die GeoManager-Klasse beschäftigt sich ausschließlich mit der
 * Abstandsberechnung von geodätischen Daten, hierfür wird der
 * Haversine-Algorithmus verwendet. Dieser berechnet den Abstand zwischen zwei
 * Punkten auf einer Kugel.
 *
 * @author Max
 */
public class GeoManager {

    /**
     * Erdradius
     */
    public static final double RADIUS = 6372.8; // In kilometers
    //long = x lat = y

    /**
     * /LF20/ Abstandsberechnung von GPS-Punkten
     *
     * @param lat1 Breitengrad Punkt 1
     * @param lon1 Längengrad Punkt 1
     * @param lat2 Breitengrad Punkt 2
     * @param lon2 Längengrad Punkt 2
     * @return Abstand in Kilometern
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return RADIUS * c;
    }

    /**
     * /LF20/ Führt die Suche anhand der ausgewählten Menüpunkte durch.
     *
     * @param list Ausgewählte Menüpunkte
     * @param x Angegebene x Koordinate
     * @param y Angegebene y Koordinate
     * @param radius Suchradius
     */
    public static void poiSearch(ArrayList<POIMenuItem> list, double x, double y, double radius) {
        System.out.println("Your Location (N" + y + "° , E" + x + "°) radius: " + radius);
        CouchDbConnector db;
        //ArrayList<POIContainer> poiListe = new ArrayList<>();
        for (POIMenuItem item : list) {
            if (item.isActive()) {
                db = CouchDbManager.connect(item.getDbName());
                ArrayList<POIContainer> results = isInDistanceTo(db, x, y, radius);
                System.out.println("Kategorie: " + item.getName());
                System.out.println(results.size() + " Treffer");
                for (POIContainer poi : results) {
                    double poiY = poi.getY();
                    double poiX = poi.getX();
                    if (poiX == 0 && poiY == 0) {
                        System.out.println("Gebiet/Zone: " + poi);
                    } else {
                        System.out.println("In " + round(haversine(y, x, poiY, poiX), 2) + "km Entfernung :" + poi);
                    }
                }
                //poiListe.addAll(results);
            }
        }
    }

    /**
     * /LF20/ Mithilfe dieser Methode werden dann Punkte abgefragt, die einen
     * gewissen Abstand, zum angegebenen Punkt, nicht überschreiten. Dadurch hat
     * man eine klassische Point of Interest Suche in einem Radius indem gilt:
     * Distanz zwischen den Punkten ≤ Suchradius.
     *
     * @param db CouchDbConnector, welcher schon mit dem gewünschtem Segment
     * verbunden ist.
     * @param x Längengrad / longitude
     * @param y Breitengrad / latitude
     * @param radius Radius um den angegebenen Punkt, indem nach POI-Objekten
     * gesucht wird.
     * @return
     */
    private static ArrayList<POIContainer> isInDistanceTo(CouchDbConnector db, double x, double y, double radius) {
        ArrayList<POIContainer> list = new ArrayList<>(CouchDbManager.selectAll(db, POIContainer.class));
        ArrayList<POIContainer> inRange = new ArrayList<>();
        for (POIContainer pc : list) {
            double pcx, pcy;
            double distance;
            pcy = pc.getY();
            pcx = pc.getX();
            if (pcx != 0 || pcy != 0) {
                distance = haversine(pcy, pcx, y, x);
                if (distance <= radius) {
                    inRange.add(pc);
                }
            } else {
                for (Double[] points : pc.getPointList()) {
                    distance = haversine(points[1], points[0], y, x);
                    if (distance <= radius) {
                        inRange.add(pc);
                        break;
                    }
                }
            }

        }
        return inRange;
    }

    /**
     * /LF20/ Hilfsfunktion zum runden.
     *
     * @param value Zu rundender Wert
     * @param places Nachkommastellen auf die gerundet werden sollen
     * @return Gerundeter Wert.
     */
    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

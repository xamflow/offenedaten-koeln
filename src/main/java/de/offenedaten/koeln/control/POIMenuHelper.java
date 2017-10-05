/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control;

import de.offenedaten.koeln.boundary.POIMenuItem;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Diese Klasse ist eine „Helper“-Klasse, welche die abzufragenden Daten aus dem
 * WantedListManager erhält, diese kann sie über den CouchDbManager abfragen, um
 * dann ein dynamisches generiertes Menü anzuzeigen. Das Menü bietet eine
 * Checkbox Funktion für die einzelnen Point of Interest Elemente. Wenn die
 * Auswahl abgeschlossen ist, wird die Auswahl an die GeoManager-Klasse
 * übergeben, damit dieser für Auswahl eine Point of Interest Suche durchführen
 * kann.
 *
 * @author Max
 */
public class POIMenuHelper {

    /**
     * /LF20/ 
     * Menüfunktion, welche alle Resourcen mit Checkboxen versehen anzeigt. Bis
     * durch Eingabe der 0 der Auswahlvorgang abgeschlossen ist.
     *
     * @param keyboard Scanner Instanz zum einlesen der Eingabe, wird von der
     * showMenu() Funktion der OffeneDatenStadtKoeln-Klasse übergeben
     */
    public static void poiMenu(Scanner keyboard) {
        ArrayList<POIMenuItem> list = setupMenuItems();
        int choice = -1;
        while (choice != 0) {
            System.out.println("Hint: Please use comma instead of point for decimal notation!");
            System.out.println("Which POI?");
            for (POIMenuItem item : list) {
                System.out.println(item);
            }
            System.out.println("0)Continue...");
            System.out.println("Select: \n");
            choice = keyboard.nextInt();
            for (POIMenuItem item : list) {
                if (choice == item.getMenuNo()) {
                    item.setActive(!item.isActive());
                }
            }
        }
        //50.930415, 6.946829 
        double[] coords = geoCoordMenu(keyboard);
        double lon = coords[0];
        double lat = coords[1];
        double radius = 0;
        System.out.println("Your position is X:" + lon + " Y: " + lat + "\n");
        System.out.println("\n" + "Radius: \n");
        radius = keyboard.nextDouble();

        GeoManager.poiSearch(list, lon, lat, radius);
    }

    /**
     * /LF20/ 
     * Zeigt ein Menü an zur Eingabe der Geokoordinaten an. Es bietet darüber
     * hinaus die Möglichkeit zwischen 3 vorgegebenen Koordinaten zu wählen.
     *
     * @param keyboard Scanner Instanz zum einlesen der Eingabe, wird von der
     * poiMenu() Funktion der dieser Klasse übergeben.
     * @return double array of the given coords. first longitude, second
     * latitude
     */
    private static double[] geoCoordMenu(Scanner keyboard) {
        double lon, lat;
        lon = lat = 0;
        System.out.println("Wählen sie einen Ort: ");
        System.out.println("1)Hauptbahnhof");
        System.out.println("2)TH-Köln");
        System.out.println("3)Headquater");
        System.out.println("");
        System.out.println("5)Eigener Punkt");
        System.out.println("Eingabe: \n");

        int choice = keyboard.nextInt();
        switch (choice) {
            case 1:
                lon = 6.958580;
                lat = 50.943282;
                break;
            case 2:
                lon = 6.97249396;
                lat = 50.93686078;
                break;
            case 3:
                lon = 6.946829;
                lat = 50.930415;
                break;
            case 5:
                System.out.println("Geben sie eine X Koordinate ein: \n");
                lon = keyboard.nextDouble();
                System.out.println("Geben sie eine Y Koordinate ein: \n");
                lat = keyboard.nextDouble();
                break;
        }
        double[] coords = new double[2];
        coords[0] = lon;
        coords[1] = lat;
        return coords;
    }

    /**
     * /LF20/ 
     * Hilfsmethode zum erzeugen der Checkboxen für das POI Menü.
     *
     * @param flag Ob ein x gesetzt werden soll. True entspricht setze ein x.
     * @param name Name des Menüeintrags
     * @return Ein String mit Menüeintrag inklusive Checkbox.
     */
    private static String checkMenuPOI(boolean flag, String name) {
        if (flag) {
            name = "[x" + name.substring(2, name.length());
        } else {
            name = "[ " + name.substring(2, name.length());
        }
        return name;
    }

    /**
     * /LF20/ 
     * Methode zum erzeugen des Menüs. Dies muss dynamisch geschehen, da sich
     * die WantedList jeder Zeit ändern kann.
     *
     * @return Eine Liste an fertigen POIMenuItems, die aus der WantedList
     * erzeugt wurden.
     */
    private static ArrayList<POIMenuItem> setupMenuItems() {
        ArrayList<POIMenuItem> list = new ArrayList<>();
        ArrayList<String[]> wantedList = WantedListManager.getWantedList();
        final int SIZE = wantedList.size();
        boolean[] flags = new boolean[SIZE];
        String[] names = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            flags[i] = false;
            list.add(new POIMenuItem(wantedList.get(i)[0], flags[i], i + 1, wantedList.get(i)[1]));
        }
        return list;
    }
}

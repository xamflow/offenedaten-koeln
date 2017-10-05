/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln;

import java.util.Scanner;
import de.offenedaten.koeln.control.CouchDb.CouchDbManager;
import de.offenedaten.koeln.control.CouchDb.DataCatalogueManager;
import de.offenedaten.koeln.control.POIMenuHelper;
import java.io.IOException;

/**
 * Es handelt sich hierbei um die Hauptklasse, welche über die main Funktion verfügt. 
 * Sie startet ein Menü und kann mithilfe der POIMenuHelper Klasse eine Suche durchführen. 
 * Das Menü erfordert eine Benutzereingabe zum Wählen der Funktionalität. 
 * @author Max
 */
public class OffeneDatenStadtKoeln {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        showMenu();
    }

    /*
     * Zeigt das Hauptmenü an
     */
    private static void showMenu() {
        int result = -1;
        //Menüpunkte
        final int UPDATER = 1,
                POI = 2,
                REFRESH = 3,
                HISTORY = 4,
                EXIT = 0;
        Scanner keyboard = new Scanner(System.in);
        while (result != 0) {
            System.out.println("**********************************************");
            System.out.println(UPDATER + ") Change Updater polling");
            System.out.println(POI + ") Search for Point of interests");
            System.out.println(REFRESH + ") Refresh Data out of CouchDb Datacatalogue");
            System.out.println(HISTORY + ") Get Schema History");
            System.out.println("");
            System.out.println(EXIT + ") Exit");
            System.out.println("**********************************************");
            System.out.print("Select: ");
            result = keyboard.nextInt();
            switch (result) {
                case UPDATER:
                    //LF10/ 
                    System.out.print("\n" + "Updater Interval (0 = No Updating) int: ");
                    CouchDbManager.UpdateDatabase(keyboard.nextInt());
                    break;
                case POI:
                    POIMenuHelper.poiMenu(keyboard);
                    break;
                case REFRESH:
                    System.out.println("\n" + "Refresh Datapool (aus der CouchDB)");
                    DataCatalogueManager.getDatasetsFromDb();
                    break;
                case HISTORY:
                    CouchDbManager.getSchemaHistories();
                    break;
                case EXIT:
                    System.out.println("Goodbye!");
                    break;
            }
        }
    }

}

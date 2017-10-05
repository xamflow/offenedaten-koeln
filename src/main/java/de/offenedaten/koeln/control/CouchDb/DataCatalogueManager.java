/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.CouchDb;

import java.util.List;
import de.offenedaten.koeln.entity.dataset.Dataset;
import org.ektorp.CouchDbConnector;

/**
 *
 * @author Max
 */
public class DataCatalogueManager {
    
    /**
     * /LF30/
     * Holt den Datenkatalog aus der CouchDB anstelle der DKAN-API
     */
    public static void getDatasetsFromDb(){
        CouchDbConnector db = CouchDbManager.connect(CouchDbManager.DATA_CATALOGUE);
        List<Dataset> list = CouchDbManager.selectAll(db, Dataset.class);
        CouchDbManager.writeEntities(list);
    }

}

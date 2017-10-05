/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.CouchDb;

import de.offenedaten.koeln.control.WantedListManager;
import java.io.IOException;
import de.offenedaten.koeln.control.DKAN.ResourceFetcher;
import de.offenedaten.koeln.control.DKAN.DkanApi;
import de.offenedaten.koeln.control.DKAN.SchemaFetcher;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.offenedaten.koeln.entity.dataset.Dataset;
import de.offenedaten.koeln.entity.dataset.Group;
import de.offenedaten.koeln.entity.dataset.Resource;
import de.offenedaten.koeln.entity.dataset.Tag;
import de.offenedaten.koeln.entity.poi.POIContainer;
import de.offenedaten.koeln.entity.schema.Schema;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.Options;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.CouchDbDocument;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Die CouchDbManager-Klasse bildet das Kernstück der Datenbanksteuerung des
 * Systems. Hierbei beschränkt sich die Klasse jedoch nicht nur auf
 * Select-Abfragen und Insert-/Update-Befehle um es in Bezug auf SQL
 * auszudrücken. Da mein System nur Daten der Stadt zur Verfügung stellen soll
 * sind Delete Befehle nicht vonnöten. Das Spektrum der zu schreibenden Daten
 * beschränkt sich jedoch nicht nur auf Point of Interest Daten. Es fallen auch
 * Metadaten wie JSON-Schemata, Datenkataloge an, welche auch von der Klasse
 * erzeugt und geändert werden.
 *
 * @author wolf
 */
public class CouchDbManager {

    //Konstanten
    private final static int PORT = 5984;
    public final static String SERVER_URL = "http://h2505042.stratoserver.net",
            PW = "xxxxx",
            PARK_AND_RIDE = "parkandride",
            UMWELTZONE = "umweltzone",
            DATA_CATALOGUE = "datacatalogue",
            PARKSCHEINAUT = "parkscheinaut",
            SPIELSPORT = "spielsportplaetze",
            TANKSTELLEN = "tankstellen",
            WOHNDATEN = "wohndaten",
            SCHEMAS = "schemas",
            USER = "admin",
            SCHEMA_DDOC_URL = SERVER_URL + ":" + PORT + "/" + SCHEMAS + "/_design/ddoc/_view/show_by_id";
    private static Timer timer = new Timer();

    /**
     * /LF10/ Task der vom Updater gestartet wird.
     */
    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            System.out.println("Checking whether DKAN API is accessable...");
            if (DkanApi.checkPortal()) {
                System.out.println("DKAN API is responding");
                ArrayList<Dataset> datasetList = DkanApi.getDatasetListing();
                if (datasetList != null) {
                    //Update CouchDb's Datenkatalog
                    writeDataCatalogue(datasetList);
                    writeEntities(datasetList);
                } else {
                    System.out.println("HTML Request for Dataset Catalogue timed out");
                }
            } else {
                System.out.println("DKAN Api didnt respond properly!");
            }
        }
    };

    /**
     * /LF10/ /LF20/ /LF30/ /LF40/ /LF50/ Erzeugt ein Segment mit segmentName,
     * falls nicht vorhanden und liefert ein connector Objekt zurück
     *
     * @param segmentName Name des Segments auf das zugegriffen werden soll
     * @return CouchDbConnector Objekt um auf die Datenbank zugriff zu bekommen.
     *
     */
    public static CouchDbConnector connect(String segmentName) {
        //bool = true → anlegen und verbinden, wenn es nicht existiert
        //bool = false → nur verbinden
        boolean flag = true;
        try {
            //HTTP-Client:
            HttpClient httpcl = new StdHttpClient.Builder()
                    .url(SERVER_URL)
                    .username(USER)
                    .password(PW)
                    .port(PORT)
                    .build();
            CouchDbInstance dbInst = new StdCouchDbInstance(httpcl);
            CouchDbConnector db = dbInst.createConnector(segmentName, flag);
            return db;
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     * Obsolet war noch aus der Zeit als es nicht eine generische POI Klasse
     * gab. Fragt die Daten mit angegebener ID ab.
     *
     * @param <T> Klasse des Datensatzes
     * @param db Datenverbindung mit einem bestimmten Segement.
     * @param type Klassentyp
     * @param id Zu suchende ID
     * @return Daten mit der ID oder null falls nicht vorhanden.
     */
    public static <T extends CouchDbDocument> T selectById(CouchDbConnector db, Class<T> type, String id) {
        try {
            return db.get(type, id);
        } catch (NoClassDefFoundError | DocumentNotFoundException e) {
            return null;
        }
    }

    /**
     * /LF30/ Siehe Funktion selectByID (nur ohne ID Abfrage).
     *
     * @param <T> Klasse des Datensatzes
     * @param db Datenverbindung mit einem bestimmten Segement.
     * @param type Klassentyp
     * @return Liste aller Daten im Segement.
     */
    //@View(name="all", map = "function(doc) { if (doc){ emit(null, doc)}}")
    public static <T extends CouchDbDocument> List<T> selectAll(CouchDbConnector db, Class<T> type) {
        ViewQuery q = new ViewQuery()
                .allDocs()
                .includeDocs(true);

        List<T> list = db.queryView(q, type);
        return list;
    }

    /**
     * /LF40/ /LF50/ Gibt alle Schemata aus und für jedes Schema, die letzten 3
     * Revisionen. Damit man einen Überblick über Anderungen der Datenstruktur
     * bekommen kann.
     */
    public static void getSchemaHistories() {
        List<JSONObject> schemaList = getJSONSchemas();
        HashMap<String, String> urlEncoding = new HashMap<>();
        urlEncoding.put("+", "%2B");
        urlEncoding.put("ö", "%C3%B6");
        urlEncoding.put("ß", "%C3%9F");
        urlEncoding.put("ä", "%C3%A4");
        urlEncoding.put("&", "%26");
        urlEncoding.put(" ", "%20");
        for (JSONObject schema : schemaList) {
            String schemaName = schema.optString("_id");
            /**
             * Encoding durchführen
             */
            for (Map.Entry<String, String> entry : urlEncoding.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                schemaName = schemaName.replace(key, value);
            }
            String url = SERVER_URL + ":" + PORT + "/" + SCHEMAS + "/" + schemaName + "?revs=true";
            System.out.println("\nSchema History of: " + schema.optString("_id"));

            JSONObject schemaAndRevision = getJsonFromURL(url);
            JSONObject revision = schemaAndRevision.getJSONObject("_revisions");
            JSONArray revisionArray = revision.getJSONArray("ids");
            int start = revision.optInt("start", 0);
            ArrayList<Schema> recentSchemas = new ArrayList<>();
            for (int i = 0; i < start; i++) {
                String id = schemaAndRevision.getString("_id");
                String rev = String.valueOf(start - i) + "-" + revisionArray.getString(i);
                //System.out.println(id + " " + rev);
                CouchDbConnector db = CouchDbManager.connect(SCHEMAS);
                Options option = new Options().revision(rev);
                Schema s = db.get(Schema.class, id, option);
                recentSchemas.add(s);
            }

            /**
             * Überprüfung ob das vorherige Schema gleich dem letztem ist. Es
             * wird nur eine Ausgabe gemacht, falls die Schemata abweichen.
             */
            Schema previousSchema = null;
            for (Schema s : recentSchemas) {
                if (previousSchema == null) {
                    previousSchema = s;
                    System.out.println(s.getProperties());
                } else {
                    if (!previousSchema.getProperties().equals(s.getProperties())) {
                        previousSchema = s;
                        System.out.println(s.getProperties());
                    }
                }
            }
        }
    }

    /**
     * /LF40/ /LF50/ Stellt Daten als ein JSONObject von einer bestimmten URL
     * zur Verfügung. Hierbei handelt es sich bewusst um eine andere Version als
     * in der ResourceFetcher-Klasse da diese andere Funktionalitäten benötigt,
     * wie etwa "http://" durch "https://" ersetzen.
     *
     * @param url URL zu den JSON Daten.
     * @return JSONObject aus den Daten von der angegebenen URL
     */
    private static JSONObject getJsonFromURL(String url) {
        URL u;
        try {
            u = new URL(url);
            String htmlData = new Scanner(new InputStreamReader(u.openStream(), "UTF-8")).useDelimiter("\\Z").next();
            return new JSONObject(htmlData);
        } catch (IOException ex) {
            Logger.getLogger(ResourceFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return new JSONObject();
        }
    }

    /**
     * /LF10/ /LF30/ Erzeugt oder aktualisiert Datensätze im durch den
     * CouchDbConnector angegebenen Segment. Für eine bestimmte Klasse.
     *
     * @param <T> Klasse des Datensatzes
     * @param db Datenverbindung mit einem bestimmten Segement.
     * @param type Klassentyp
     * @param obj Objekt das in der Datenbankgespeichert werden soll.
     */
    public static <T extends CouchDbDocument> void createOrUpdate(CouchDbConnector db, Class<T> type, T obj) {
        T dbObj = CouchDbManager.selectById(db, type, obj.getId());
        if (dbObj == null) {
            System.out.println("Creating: " + obj.toString());
            db.create(obj);
        } else {
            System.out.println("Updating: " + obj.toString());
            obj.setRevision(dbObj.getRevision());
            db.update(obj);
        }
    }

    /**
     * /LF10/ Startet das Update in einem bestimmten Interval.
     *
     * @param interval Polling interval in Minuten
     */
    public static void UpdateDatabase(int interval) {
        timer.schedule(task, 0, interval * 60 * 1000);
    }

    /**
     * /LF10/ Schreibt den Datenkatalog in die Datenbank.
     *
     * @param list Datenkatalog, der in die Datenbank geschrieben werden soll.
     */
    public static void writeDataCatalogue(List<Dataset> list) {
        System.out.println("Current number of datasets: " + list.size());
        CouchDbConnector db = CouchDbManager.connect(CouchDbManager.DATA_CATALOGUE);
        for (Dataset ds : list) {
            System.out.println("Writing Dataset: " + ds);
            CouchDbManager.createOrUpdate(db, Dataset.class, ds);
        }
    }

    /**
     * /LF10/ /LF30/ Schreibt die POI-Daten in Datenbank.
     *
     * @param datasetList Datenkatalog aus dem die Resourcen bezogen werden
     * sollen.
     */
    public static void writeEntities(List<Dataset> datasetList) {
        ArrayList<String[]> wantedList = WantedListManager.getWantedList();
        for (String[] item : wantedList) {
            //System.out.println(item[0] + ":" + item[1] + ":" + item[2]);
        }
        for (Dataset d : datasetList) {
            for (Resource r : d.getResourceList()) {
                if (r.getMimetype().toLowerCase().contains("json")) {
                    String resourceName = r.getName().toLowerCase();
                    //System.out.println(resourceName);
                    for (String[] entry : wantedList) {
                        if (resourceName.contains(entry[0])
                                && !resourceName.contains("metadaten")) {
                            System.out.println(resourceName);
                            writeSchema(r);
                            CouchDbConnector db = CouchDbManager.connect(entry[1]);
                            for (POIContainer poi : ResourceFetcher.fetchPOI(r.getUrl(), entry[2])) {
                                POIContainer dbObj = db.find(POIContainer.class, poi.getId());
                                if (dbObj == null) {
                                    //System.out.println("Creating: " + p.toString());
                                    db.create(poi);
                                } else {
                                    poi.setRevision(dbObj.getRevision());
                                    //System.out.println("Updating: " + p.toString());
                                    db.update(poi);
                                }
                            }
                        }
                    }
                }
            }

            for (Group g : d.getGroupList()) {
                //System.out.println(g.getName() + " " + g.getTitle());
            }
            for (Tag g : d.getTagList()) {
                //System.out.println(g.getName() + " " + g.getId() + " " + g.getVocabulary_id());
            }
        }
    }

    /**
     * /LF40/ /LF50/ Holt die Daten der JSON-Schemata über ein Designdokument
     * der CouchDB aus dem Segment "schemas"
     *
     * @return Liefert die JSON Schemata als Liste von JSON Objekten.
     */
    public static List<JSONObject> getJSONSchemas() {
        JSONObject obj = null;
        List<JSONObject> schemaListe = new ArrayList<>();
        try {
            URL u;
            u = new URL(SCHEMA_DDOC_URL);
            String htmldata = new Scanner(new InputStreamReader(u.openStream(), "UTF-8")).useDelimiter("\\Z").next();
            obj = new JSONObject(htmldata);
            JSONArray results = obj.optJSONArray("rows");
            for (int i = 0; i < results.length(); i++) {
                JSONObject schema = results.getJSONObject(i);
                schema = schema.optJSONObject("value");
                schemaListe.add(schema);

            }
        } catch (IOException ex) {
            Logger.getLogger(ResourceFetcher.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return schemaListe;
    }

    /**
     * /LF10/ Schreibt ein Schema in die Datenbank für die angegebene Resource
     *
     * @param r Resource für die ein Schema in die Datenbank geschrieben werden
     * soll.
     */
    private static void writeSchema(Resource r) {
        CouchDbConnector db = CouchDbManager.connect(SCHEMAS);
        Schema schema = SchemaFetcher.fetchSchema(r);

        if (schema != null) {
            CouchDbManager.createOrUpdate(db, Schema.class, schema);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.control.DKAN;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.offenedaten.koeln.entity.dataset.Dataset;
import de.offenedaten.koeln.entity.dataset.Group;
import de.offenedaten.koeln.entity.dataset.Resource;
import de.offenedaten.koeln.entity.dataset.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Die „DkanApi“-Klasse ist die Java-Klasse, welche sich sämtliche Daten der
 * REST-Schnittstelle der Offenen Daten Köln (welche den Namen DKAN-API trägt)
 * abfragt. Dies beinhaltet jedoch nur die Metadaten d.h. die Datensätze, welche
 * sich aus Ressourcen, Tags und Gruppen zusammensetzen. Die eigentlichen Point
 * of Interest-Daten werden von der sogenannten „ResourceFetcher“-Klasse aus den
 * URLs der im Datensatz vorhandenen Ressource-Attribute geholt(gefetcht, von
 * engl. fetch = holen).
 *
 * @author wolf
 */
public class DkanApi {

    private static String FULL_LIST_URL = "https://offenedaten-koeln.de/api/3/action/current_package_list_with_resources";
    private static String CHECK_URL = "https://offenedaten-koeln.de/api/3/action/site_read";

    /**
     * Checks whether the DKAN API is accessable.
     *
     * @return boolean value, true = online, false = offline
     */
    public static boolean checkPortal() {
        try {
            URL u = new URL(CHECK_URL);
            String htmldata = new Scanner(u.openStream()).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(htmldata);
            return obj.getBoolean("result") && obj.getBoolean("success");
        } catch (MalformedURLException ex) {
            Logger.getLogger(DkanApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DkanApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Gibt eine Liste an Datensätzen zurück, welche nach Änderungsdatum
     * sortiert sind
     *
     * @return Liste aller verfügbaren Datensätze.
     */
    public static ArrayList<Dataset> getDatasetListing() {
        try {
            //Retrieving Data
            URL u = new URL(FULL_LIST_URL);
            HttpURLConnection uCon = (HttpURLConnection) u.openConnection();
            int code = uCon.getResponseCode();
            String htmldata = "";
            System.out.println(code);
            if (code == 200) {
                htmldata = new Scanner(u.openStream()).useDelimiter("\\Z").next();
                System.out.println(htmldata);
                if (htmldata.contains("Fatal error")) {
                    return null;
                }
                System.out.println(htmldata);
            } else {
                return null;
            }
            JSONObject mainObj = new JSONObject(htmldata);
            JSONArray mainArray = mainObj.optJSONArray("result");
            mainArray = (JSONArray) mainArray.get(0); // Doppeltes array SINN?!?!?
            ArrayList<Dataset> list = new ArrayList<>();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); // 2016-02-03T14:47:14+01:00
            Date metadata_modified = new Date();
            Date revision_timestamp = new Date();
            Date metadata_created = new Date();
            //Creating Dataset Objects
            for (int i = 0; i < mainArray.length(); i++) {
                //Core Dataset properties
                JSONObject obj = mainArray.getJSONObject(i);
                String id = obj.optString("id", "");
                String name = obj.optString("name", "");
                String title = obj.optString("title", "");
                String notes = obj.optString("notes", "");
                String log_message = obj.optString("log_message", "");
                String creator_user_id = obj.optString("creator_user_id", "");
                String type = obj.optString("type", "");
                boolean state = obj.optString("state", "").toLowerCase().equals("active");
                String url = obj.optString("url", "");
                try {
                    metadata_modified = parser.parse(obj.optString("metadata_modified", ""));
                    revision_timestamp = parser.parse(obj.optString("revision_timestamp", ""));
                    metadata_created = parser.parse(obj.optString("metadata_created", ""));
                } catch (ParseException ex) {
                    Logger.getLogger(DkanApi.class.getName()).log(Level.SEVERE, null, ex);
                }
                Dataset dataset = new Dataset(id, name, title, notes, log_message, creator_user_id, type, url, state, revision_timestamp, metadata_created, metadata_modified);
                dataset.setResourceList(getResources(obj));
                dataset.setGroupList(getGroups(obj));
                dataset.setTagList(getTags(obj));
                // TO DO
                JSONArray extrasArray = obj.optJSONArray("extras");

                list.add(dataset);
            }
            return list;
        } catch (MalformedURLException ex) {
            Logger.getLogger(DkanApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | JSONException | NullPointerException ex) {
            Logger.getLogger(DkanApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Holt resourcen aus einem Datensatz Objekt.
     *
     * @param obj Datensatz-Objekt als JSON Daten(nicht als Java-Klasse
     * Dataset).
     * @return Liste aller resourcen, die zu dem Datensatz gehörten.
     */
    private static ArrayList<Resource> getResources(JSONObject obj) {
        //Dataset's resources
        JSONArray resourcesArray = obj.optJSONArray("resources");
        Resource resource = new Resource();
        ArrayList<Resource> list = new ArrayList<>();
        Date rLast_modified = new Date();
        Date rRevision_timestamp = new Date();
        Date rCreated = new Date();
        for (int k = 0; k < resourcesArray.length(); k++) {
            JSONObject rObj = resourcesArray.getJSONObject(k);
            String rId = rObj.optString("id", "");
            String rRevision_id = rObj.optString("revision_id", "");
            String rDescription = rObj.optString("description", "");
            String rFormat = rObj.optString("format", "");
            String rName = rObj.optString("name", "");
            String rMimetype = rObj.optString("mimetype", "");
            String rResource_group_id = rObj.optString("resource_group_id", "");
            boolean rState = rObj.optString("state", "").toLowerCase().equals("active");
            double rSize = rObj.optDouble("size", 0);
            String rUrl = rObj.optString("url", "");
            //Wie soll mit nicht gesetzen Daten programmintern verfahren werden?
            //rLast_modified = parser.parse(obj.optString("last_modified",""));
            //rRevision_timestamp = parser.parse(obj.optString("revision_timestamp",""));
            //rCreated = parser.parse(obj.optString("created",""));
            resource = new Resource(rId, rRevision_id, rDescription, rFormat, rName, rMimetype, rResource_group_id, rState, rSize, rUrl, rLast_modified, rRevision_timestamp, rCreated);
            list.add(resource);
        }
        return list;
    }

    /**
     * Holt Gruppen aus einem Datensatz Objekt.
     *
     * @param obj Datensatz-Objekt als JSON Daten(nicht als Java-Klasse
     * Dataset).
     * @return Liste aller Gruppen, die zu dem Datensatz gehörten.
     */
    private static ArrayList<Group> getGroups(JSONObject obj) {
        JSONArray groupsArray = obj.optJSONArray("groups");
        ArrayList<Group> list = new ArrayList<>();
        String display_name, description, id, title, name, image_display_url;
        for (int i = 0; i < groupsArray.length(); i++) {
            JSONObject gObj = groupsArray.getJSONObject(i);
            display_name = gObj.optString("display_name", "");
            description = gObj.optString("description", "");
            id = gObj.optString("id", "");
            title = gObj.optString("title", "");
            name = gObj.optString("name", "");
            image_display_url = gObj.optString("image_display_url", "");
            Group group = new Group(display_name, description, id, "", title, name, image_display_url);
            list.add(group);
        }
        return list;
    }

    /**
     * Holt Tags aus einem Datensatz Objekt.
     *
     * @param obj Datensatz-Objekt als JSON Daten(nicht als Java-Klasse
     * Dataset).
     * @return Liste aller Tags, die zu dem Datensatz gehörten.
     */
    private static ArrayList<Tag> getTags(JSONObject obj) {
        JSONArray tagsArray = obj.optJSONArray("tags");
        ArrayList<Tag> list = new ArrayList<>();
        String id, name;
        int vocabulary_id;
        for (int i = 0; i < tagsArray.length(); i++) {
            JSONObject tObj = tagsArray.optJSONObject(i);
            id = tObj.optString("id", "");
            name = tObj.optString("name", "");
            vocabulary_id = tObj.optInt("vocabulary_id", 0);
            Tag tag = new Tag(id, name, vocabulary_id);
            list.add(tag);
        }
        return list;
    }
}

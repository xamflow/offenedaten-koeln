/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.dataset;

import de.offenedaten.koeln.entity.poi.POIDocument;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Bei der Group-Klasse handelt es sich um eine Klasse, welche anzeigt wer die
 * Daten zur Verfügung stellt, z.B. die KVB oder die Stadt Köln. Desweiteren
 * verfügt sie über einen Titel, eine Beschreibung, eine Revisionsnummer, einen
 * Anzeigenamen, eine Id und einen Link zu einem Logo.
 *
 * @author wolf
 */
public class Group extends POIDocument {

    private String display_name, description, id, rev, title, name, image_display_url;

    public Group(String display_name, String description, String id, String rev, String title, String name, String image_display_url) {
        this.display_name = display_name;
        this.description = description;
        this.id = id;
        this.rev = rev;
        this.title = title;
        this.name = name;
        this.image_display_url = image_display_url;
    }

    public Group() {
    }

    @JsonProperty("_rev")
    public void setRev(String rev) {
        this.rev = rev;
    }

    @JsonProperty("_rev")
    public String getRev() {
        return rev;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getDescription() {
        return description;
    }

    @JsonProperty("_id")
    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getImage_display_url() {
        return image_display_url;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("_id")
    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_display_url(String image_display_url) {
        this.image_display_url = image_display_url;
    }

    @Override
    public String toString() {
        return "Group{" + "display_name=" + display_name + ", description=" + description + ", id=" + id + ", title=" + title + ", name=" + name + ", image_display_url=" + image_display_url + '}';
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.dataset;

import java.util.Date;

/**
 * Die Resource-Klasse enthält alle verfügbaren Informationen über die Daten
 * selbst. Dabei handelt es sich unter anderem um das Erstellungsdatum, die URL
 * zur Quelle der Daten, das Format der Daten, den MIME-Type, die Größe usw.
 *
 * @author wolf
 */
public class Resource {

    private String id, revision_id, description, format, name, mimetype, resource_group_id;
    private boolean state;
    private double size;
    private String url;
    private Date last_modified, revision_timestamp, created;

    public Resource(String id, String revision_id, String description, String format, String name, String mimetype, String resource_group_id, boolean state, double size, String url, Date last_modified, Date revision_timestamp, Date created) {
        this.id = id;
        this.revision_id = revision_id;
        this.description = description;
        this.format = format;
        this.name = name;
        this.mimetype = mimetype;
        this.resource_group_id = resource_group_id;
        this.state = state;
        this.size = size;
        this.url = url;
        this.last_modified = last_modified;
        this.revision_timestamp = revision_timestamp;
        this.created = created;
    }

    public Resource() {
    }

    public String getId() {
        return id;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getResource_group_id() {
        return resource_group_id;
    }

    public boolean isState() {
        return state;
    }

    public double getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public Date getRevision_timestamp() {
        return revision_timestamp;
    }

    public Date getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setResource_group_id(String resource_group_id) {
        this.resource_group_id = resource_group_id;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public void setRevision_timestamp(Date revision_timestamp) {
        this.revision_timestamp = revision_timestamp;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ResourceBoundary{" + "id=" + id + ", revision_id=" + revision_id + ", description=" + "<deaktiviert>" + ", format=" + format + ", name=" + name + ", mimetype=" + mimetype + ", resource_group_id=" + resource_group_id + ", state=" + state + ", size=" + size + ", url=" + url + ", last_modified=" + last_modified + ", revision_timestamp=" + revision_timestamp + ", created=" + created + '}';
    }
}

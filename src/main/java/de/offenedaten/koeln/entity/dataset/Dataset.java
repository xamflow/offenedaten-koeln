/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.dataset;

import de.offenedaten.koeln.entity.poi.POIDocument;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dataset ist eine Klasse, die Daten rund um eine Ressource speichert, also
 * nicht die Daten selbst, sondern Informationen wie den Betreuer der Daten, die
 * URL zur Infoseite des Datensatzes, Notizen, aber auch das wichtigste und zwar
 * die Ressourcen.
 *
 * @author wolf
 */
public class Dataset extends POIDocument {

    private String name, title, notes, log_message, creator_user_id, type, url,
            maintainer = "Offene Daten Koeln",
            maintainer_email = "offenedaten@stadt-koeln.de",
            license_title = "https://creativecommons.org/licenses/by/3.0/de/";
    private List<Resource> resourceList;
    private List<Group> groupList;
    private List<Tag> tagList;
    private boolean state;
    private Date revision_timestamp, metadata_created, metadata_modified;

    public Dataset(String id, String name, String title, String notes, String log_message,
            String creator_user_id, String type, String url, boolean state,
            Date revision_timestamp, Date metadata_created, Date metadata_modified) {
        setId(id);
        this.name = name;
        this.title = title;
        this.notes = notes;
        this.log_message = log_message;
        this.creator_user_id = creator_user_id;
        this.type = type;
        this.url = url;
        this.state = state;
        this.revision_timestamp = revision_timestamp;
        this.metadata_created = metadata_created;
        this.metadata_modified = metadata_modified;
        this.resourceList = new ArrayList<>();
        this.tagList = new ArrayList<>();
        this.groupList = new ArrayList<>();
    }

    public Dataset(String id, String name, String title, String notes, String log_message, String creator_user_id, String type, ArrayList<Resource> resourceList, ArrayList<Group> groupList, ArrayList<Tag> tagList, String url, boolean state, Date revision_timestamp, Date metadata_created, Date metadata_modified) {
        setId(id);
        this.name = name;
        this.title = title;
        this.notes = notes;
        this.log_message = log_message;
        this.creator_user_id = creator_user_id;
        this.type = type;
        this.resourceList = resourceList;
        this.groupList = groupList;
        this.tagList = tagList;
        this.url = url;
        this.state = state;
        this.revision_timestamp = revision_timestamp;
        this.metadata_created = metadata_created;
        this.metadata_modified = metadata_modified;
        this.resourceList = new ArrayList<>();
        this.tagList = new ArrayList<>();
        this.groupList = new ArrayList<>();
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setResourceList(ArrayList<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    public Dataset() {
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public String getMaintainer_email() {
        return maintainer_email;
    }

    public String getLicense_title() {
        return license_title;
    }

    public String getNotes() {
        return notes;
    }

    public String getLog_message() {
        return log_message;
    }

    public String getCreator_user_id() {
        return creator_user_id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public boolean isState() {
        return state;
    }

    public Date getRevision_timestamp() {
        return revision_timestamp;
    }

    public Date getMetadata_created() {
        return metadata_created;
    }

    public Date getMetadata_modified() {
        return metadata_modified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public void setMaintainer_email(String maintainer_email) {
        this.maintainer_email = maintainer_email;
    }

    public void setLicense_title(String license_title) {
        this.license_title = license_title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLog_message(String log_message) {
        this.log_message = log_message;
    }

    public void setCreator_user_id(String creator_user_id) {
        this.creator_user_id = creator_user_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setRevision_timestamp(Date revision_timestamp) {
        this.revision_timestamp = revision_timestamp;
    }

    public void setMetadata_created(Date metadata_created) {
        this.metadata_created = metadata_created;
    }

    public void setMetadata_modified(Date metadata_modified) {
        this.metadata_modified = metadata_modified;
    }

    @Override
    public String toString() {
        return "Dataset{" + "name=" + name + ", title=" + title + ", log_message=" + log_message + ", creator_user_id=" + creator_user_id + ", type=" + type + ", url=" + url + ", maintainer=" + maintainer + ", maintainer_email=" + maintainer_email + ", license_title=" + license_title + ", resourceList=" + resourceList + ", groupList=" + groupList + ", tagList=" + tagList + ", state=" + state + ", revision_timestamp=" + revision_timestamp + ", metadata_created=" + metadata_created + ", metadata_modified=" + metadata_modified + '}';
    }

}

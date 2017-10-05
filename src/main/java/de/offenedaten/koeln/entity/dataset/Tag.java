/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.dataset;

/**
 * Tags dienen zur Kategorisierung der Daten, also um welches Themengebiet es
 * sich bei den Daten handelt. So hat z.B. ein Datensatz der Parkscheinautomaten
 * die Tags: „Geo“, „Transport und Verkehr“, „Infrastruktur, Bauen und Wohnen“.
 * Tags bestehen aus einem Namen, einer Id und einer vocabulary_id.
 *
 * @author wolf
 */
public class Tag {

    private String id, name;
    private int vocabulary_id;

    public Tag(String id, String name, int vocabulary_id) {
        this.id = id;
        this.name = name;
        this.vocabulary_id = vocabulary_id;
    }

    public Tag() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVocabulary_id() {
        return vocabulary_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVocabulary_id(int vocabulary_id) {
        this.vocabulary_id = vocabulary_id;
    }

    @Override
    public String toString() {
        return "Tags{" + "id=" + id + ", name=" + name + ", vocabulary_id=" + vocabulary_id + '}';
    }

}

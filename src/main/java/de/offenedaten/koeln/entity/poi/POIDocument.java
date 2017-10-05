/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.entity.poi;

import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Max
 */
public class POIDocument extends CouchDbDocument {

    private double x, y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return super.toString() + " PoiDocument{" + "x=" + x + ", y=" + y + '}';
    }

}

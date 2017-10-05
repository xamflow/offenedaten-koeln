/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offenedaten.koeln.boundary;

/**
 *
 * @author Max
 */
public class POIMenuItem {

    private String name;
    private boolean active;
    private String dbName;
    private int menuNo;

    public POIMenuItem() {
    }

    public POIMenuItem(String name, boolean active, int menuNo, String dbName) {
        this.name = name;
        this.active = active;
        this.menuNo = menuNo;
        this.dbName = dbName;
    }
    
    public int getMenuNo() {
        return menuNo;
    }

    public void setMenuNo(int menuNo) {
        this.menuNo = menuNo;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        String checkbox;
        if (active) {
            checkbox = "[x] ";
        } else {
            checkbox = "[ ] ";
        }
        return checkbox + menuNo + ") " + name;
    }
}

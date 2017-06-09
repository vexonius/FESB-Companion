package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by amarthus on 18-May-17.
 */

public class Dolazak extends RealmObject {


    @PrimaryKey
    public String id;

    public String predmet;
    public String vrsta;
    public String link;
    public int attended;
    public int absent;
    public String required;
    public int semestar;
    public int total;

    public int getTotal() {
        return total;
    }

    public int getAbsent() {
        return absent;
    }

    public String getVrsta() {
        return vrsta;
    }

    public int getAttended() {
        return attended;
    }

    public String getPredmet() {
        return predmet;
    }

    public String getRequired() {
        return required;
    }

    public String getLink() {
        return link;
    }

    public int getSemestar() {
        return semestar;
    }

    public String getId() {
        return id;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSemestar(int semestar) {
        this.semestar = semestar;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

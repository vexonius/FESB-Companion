package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;

/**
 * Created by amarthus on 09-May-17.
 */

public class Materijal extends RealmObject {

    public String url;
    public String vrsta;
    public String imeMtarijala;
    public int icon;
    public int downloadable;

    public void setImeMtarijala(String imeMtarijala) {
        this.imeMtarijala = imeMtarijala;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setDownloadable(int downloadable) {
        this.downloadable = downloadable;
    }

    public String getImeMtarijala() {
        return imeMtarijala;
    }

    public String getUrl() {
        return url;
    }

    public String getVrsta() {
        return vrsta; }

    public int getIcon() {
        return icon;
    }

    public int getDownloadable() {
        return downloadable;
    }
}

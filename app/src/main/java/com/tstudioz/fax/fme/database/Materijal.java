package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;

/**
 * Created by amarthus on 09-May-17.
 */

public class Materijal extends RealmObject {

    public String url;
    public String vrsta;
    public String imeMtarijala;
    public String ikonaUrl;

    public void setImeMtarijala(String imeMtarijala) {
        this.imeMtarijala = imeMtarijala;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public void setIkonaUrl(String ikonaUrl) {
        this.ikonaUrl = ikonaUrl;
    }

    public String getImeMtarijala() {
        return imeMtarijala;
    }

    public String getUrl() {
        return url;
    }

    public String getVrsta() {
        return vrsta;
    }

    public String getIkonaUrl() {
        return ikonaUrl;
    }
}

package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;

/**
 * Created by amarthus on 26-Apr-17.
 */

public class Racun extends RealmObject {

    public String pare;
    public String ime_prezime;
    public String broj_kartice;

    public String getBroj_kartice() {
        return broj_kartice;
    }

    public String getIme_prezime() {
        return ime_prezime;
    }

    public String getPare() {
        return pare;
    }

    public void setBroj_kartice(String broj_kartice) {
        this.broj_kartice = broj_kartice;
    }

    public void setIme_prezime(String ime_prezime) {
        this.ime_prezime = ime_prezime;
    }

    public void setPare(String pare) {
        this.pare = pare;
    }
}


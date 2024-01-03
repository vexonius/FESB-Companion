package com.tstudioz.fax.fme.database;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by amarthus on 09-May-17.
 */

public class KolegijTjedan extends RealmObject{

    public int index;
    public String opis;
    public String tjedan;
    public RealmList<Materijal> materijali;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMaterijali(RealmList<Materijal> materijali) {
        this.materijali = materijali;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setTjedan(String tjedan) {
        this.tjedan = tjedan;
    }


    public RealmList<Materijal> getMaterijali() {
        return materijali;
    }

    public int getIndex() {
        return index;
    }

    public String getOpis() {
        return opis;
    }

    public String getTjedan() {
        return tjedan;
    }

}

package com.tstudioz.fax.fme.database;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by amarthus on 09-May-17.
 */

public class KolegijTjedan extends RealmObject{


    public String opis;
    public String tjedan;
    public RealmList<Materijali> materijali;



    public void setMaterijali(RealmList<Materijali> materijali) {
        this.materijali = materijali;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setTjedan(String tjedan) {
        this.tjedan = tjedan;
    }


    public RealmList<Materijali> getMaterijali() {
        return materijali;
    }

    public String getOpis() {
        return opis;
    }

    public String getTjedan() {
        return tjedan;
    }

}

package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;

/**
 * Created by amarthus on 26-Apr-17.
 */

public class Korisnik extends RealmObject {

    public String username;
    public String lozinka;


    public String getLozinka() {
        return lozinka;
    }

    public String getUsername() {
        return username;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

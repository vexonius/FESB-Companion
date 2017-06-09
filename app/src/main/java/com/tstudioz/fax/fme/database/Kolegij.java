package com.tstudioz.fax.fme.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by amarthus on 06-May-17.
 */

public class Kolegij extends RealmObject {


    public String name;
    public String link;



    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public void setName(String name) {
        this.name = name;
    }
}

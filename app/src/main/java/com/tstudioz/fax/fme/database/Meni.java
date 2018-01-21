package com.tstudioz.fax.fme.database;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by etino7 on 21/01/2018.
 */

public class Meni extends RealmObject {

    private String id;
    private String type;
    private String jelo1;
    private String jelo2;
    private String jelo3;
    private String jelo4;
    private String desert;
    private String cijena;

    public void setId(String id) {
        this.id = id;
    }

    public void setCijena(String cijena) {
        this.cijena = cijena;
    }

    public void setDesert(String desert) {
        this.desert = desert;
    }

    public void setJelo1(String jelo1) {
        this.jelo1 = jelo1;
    }

    public void setJelo2(String jelo2) {
        this.jelo2 = jelo2;
    }

    public void setJelo3(String jelo3) {
        this.jelo3 = jelo3;
    }

    public void setJelo4(String jelo4) {
        this.jelo4 = jelo4;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getCijena() {
        return cijena;
    }

    public String getDesert() {
        return desert;
    }

    public String getJelo1() {
        return jelo1;
    }

    public String getJelo2() {
        return jelo2;
    }

    public String getJelo3() {
        return jelo3;
    }

    public String getJelo4() {
        return jelo4;
    }

    public String getType() {
        return type;
    }

    @Override
    public Realm getRealm() {
        return super.getRealm();
    }

}

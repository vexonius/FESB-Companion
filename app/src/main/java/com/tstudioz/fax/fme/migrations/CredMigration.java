package com.tstudioz.fax.fme.migrations;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;


public class CredMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema shema = realm.getSchema();



        if(oldVersion == 5){
            shema.create("LeanTask")
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("taskTekst", String.class)
                    .addField("dateCreated", Date.class)
                    .addField("dateReminder", Date.class)
                    .addField("reminder", Boolean.class)
                    .addField("isChecked", Boolean.class);

            oldVersion++;
        }


    }

    @Override
    public int hashCode() {
        return 6;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CredMigration);
    }
}

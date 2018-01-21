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

        if(oldVersion == 6){
            shema.create("Meni")
                    .addField("id", String.class)
                    .addField("type", String.class)
                    .addField("jelo1", String.class)
                    .addField("jelo2", String.class)
                    .addField("jelo3", String.class)
                    .addField("jelo4", String.class)
                    .addField("desert", String.class)
                    .addField("cijena", String.class);

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

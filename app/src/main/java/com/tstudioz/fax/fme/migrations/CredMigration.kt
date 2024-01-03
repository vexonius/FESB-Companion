package com.tstudioz.fax.fme.migrations

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import java.util.Date

class CredMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val shema = realm.schema
        if (oldVersion == 5L) {
            shema.create("LeanTask")
                .addField("id", String::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("taskTekst", String::class.java)
                .addField("dateCreated", Date::class.java)
                .addField("dateReminder", Date::class.java)
                .addField("reminder", Boolean::class.java)
                .addField("isChecked", Boolean::class.java)

        }
        if (oldVersion == 6L) {
            shema.create("Meni")
                .addField("id", String::class.java)
                .addField("type", String::class.java)
                .addField("jelo1", String::class.java)
                .addField("jelo2", String::class.java)
                .addField("jelo3", String::class.java)
                .addField("jelo4", String::class.java)
                .addField("desert", String::class.java)
                .addField("cijena", String::class.java)
        }
        if (oldVersion == 7L) {
            shema.get("Predavanja")
                ?.removeField("predavanjeVrsta")
                ?.addField("predavanjeIme", String::class.java)
            shema.get("LeanTask")
                ?.removeField("isChecked")
                ?.addField("checked", Boolean::class.javaPrimitiveType)
        }

        oldVersion++
    }

    override fun hashCode(): Int {
        return 6
    }

    override fun equals(o: Any?): Boolean {
        return o is CredMigration
    }
}

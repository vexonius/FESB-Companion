package com.tstudioz.fax.fme.database;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LeanTask extends RealmObject {

    @PrimaryKey
    public String id;

    public String taskTekst;
    public Date dateCreated;
    public Date dateReminder;
    public Boolean reminder;
    public Boolean isChecked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateReminder(Date dateReminder) {
        this.dateReminder = dateReminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public void setTaskTekst(String taskTekst) {
        this.taskTekst = taskTekst;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public Boolean getReminder() {
        return reminder;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateReminder() {
        return dateReminder;
    }

    public String getTaskTekst() {
        return taskTekst;
    }
}

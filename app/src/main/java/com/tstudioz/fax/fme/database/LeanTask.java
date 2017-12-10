package com.tstudioz.fax.fme.database;

import java.util.Date;

import io.realm.RealmObject;

public class LeanTask extends RealmObject {

    public String taskTekst;
    public Date dateCreated;
    public Date dateReminder;
    public Boolean reminder;

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateReminder(Date dateReminder) {
        this.dateReminder = dateReminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }

    public void setTaskTekst(String taskTekst) {
        this.taskTekst = taskTekst;
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

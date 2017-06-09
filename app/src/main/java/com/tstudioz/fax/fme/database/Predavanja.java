package com.tstudioz.fax.fme.database;

import com.tstudioz.fax.fme.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Predavanja extends RealmObject {

    @PrimaryKey
    private String id;

    private int objectId;
    private String predavanjeVrsta;
    private String predmetPredavanja;
    private String profesor;
    private String rasponVremena;
    private String brojSati;
    private String grupa;
    private String grupaShort;
    private String detaljnoVrijeme;
    private String dvorana;
    private String boja;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getPredavanjeIme() {
        return predavanjeVrsta;
    }

    public void setPredavanjeIme(String predavanjeIme) {
        this.predavanjeVrsta = predavanjeIme;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getGrupaShort() {
        return grupaShort;
    }

    public void setGrupaShort(String grupaShort) {
        this.grupaShort = grupaShort;
    }

    public String getRasponVremena() {
        return rasponVremena;
    }

    public void setRasponVremena(String rasponVremena) {
        this.rasponVremena = rasponVremena;
    }

    public String getDetaljnoVrijeme() {
        return detaljnoVrijeme;
    }

    public void setDetaljnoVrijeme(String detaljnoVrijeme) {
        this.detaljnoVrijeme = detaljnoVrijeme;
    }

    public void setPredavanjeVrsta(String predavanjeVrsta) {
        this.predavanjeVrsta = predavanjeVrsta;
    }

    public void setPredmetPredavanja(String predmetPredavanja) {
        this.predmetPredavanja = predmetPredavanja;
    }

    public String getPredmetPredavanja() {
        return predmetPredavanja;
    }

    public String getDvorana() {
        return dvorana;
    }

    public void setDvorana(String dvorana) {
        this.dvorana = dvorana;
    }

    public void setBrojSati(String brojSati) {
        this.brojSati = brojSati;
    }

    public String getBrojSati() {
        return brojSati;
    }

    public String getBoja() {
        return boja;
    }

    public int setBoja(String boja) {

        int bojaId = R.color.blue_nice;

        switch (boja){
            case("predavanje"):
                bojaId = R.color.blue_nice;
                break;
            case("Auditorne"):
                bojaId = R.color.green_nice;
                break;
            case("Kolokviji"):
                bojaId = R.color.purple_nice;
                break;
            case("Laboratorijske vježbe"):
                bojaId = R.color.red_nice;
                break;
            case("Konstrukcijske vježbe"):
                bojaId = R.color.grey_nice;
                break;
            case("Ispiti"):
                bojaId = R.color.purple_dark;
                break;
        }
        return bojaId;
    }
}


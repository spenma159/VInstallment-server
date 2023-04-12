package com.example.vinstallment;

public class PunishmentItem {
    private int id;
    private String titlePunishmnet, desPunisment;
    private boolean enabled;

    public PunishmentItem(int id, String titlePunishmnet, String desPunisment, boolean enabled) {
        this.id = id;
        this.titlePunishmnet = titlePunishmnet;
        this.desPunisment = desPunisment;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getTitlePunishmnet() {
        return titlePunishmnet;
    }

    public String getDesPunisment() {
        return desPunisment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitlePunishmnet(String titlePunishmnet) {
        this.titlePunishmnet = titlePunishmnet;
    }

    public void setDesPunisment(String desPunisment) {
        this.desPunisment = desPunisment;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

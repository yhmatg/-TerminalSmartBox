package com.android.terminalbox.ui.inventory;

public class FileBean {
    private String name;
    private String epcCode;

    public FileBean(String name, String epcCode) {
        this.name = name;
        this.epcCode = epcCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEpcCode() {
        return epcCode;
    }

    public void setEpcCode(String epcCode) {
        this.epcCode = epcCode;
    }
}

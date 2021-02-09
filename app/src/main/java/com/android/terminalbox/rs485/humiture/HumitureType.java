package com.android.terminalbox.rs485.humiture;

public enum HumitureType {
    //temperature and humidity
    TEMPRATURE("温度",1),
    HUMIDITY("湿度",2);
    private String disp;
    private int index;

    HumitureType(String disp, int index) {
        this.disp = disp;
        this.index = index;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString()
    {
        return this.index+"-"+this.disp;
    }
}

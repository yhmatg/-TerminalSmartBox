package com.android.terminalbox.rs485.ekey;

public enum EkeyStatusChange {
    OPENED("锁柄物理开启",1),
    CLOSED("锁柄物理关闭",2),
    OPENED_ELECTRONICS ("电子开启",3);
    private String disp;
    private int index;

    EkeyStatusChange(String disp, int index) {
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

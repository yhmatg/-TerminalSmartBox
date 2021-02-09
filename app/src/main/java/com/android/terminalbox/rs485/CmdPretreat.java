package com.android.terminalbox.rs485;


public class CmdPretreat extends CmdData{
    private long pretreatTime;

    public CmdPretreat() {
    }

    public CmdPretreat(byte[] cmdData) {
        this.pretreatTime = System.currentTimeMillis();
        this.setCmdBody(cmdData);
    }

    public long getPretreatTime() {
        return pretreatTime;
    }
    public void setPretreatTime(long pretreatTime) {
        this.pretreatTime = pretreatTime;
    }
}

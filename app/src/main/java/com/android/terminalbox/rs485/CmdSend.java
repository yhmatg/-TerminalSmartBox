package com.android.terminalbox.rs485;

public class CmdSend extends CmdData{
    private long sendTime;

    public CmdSend() {
    }

    public CmdSend(byte[] cmdData) {
        this.sendTime = System.currentTimeMillis();
        this.setCmdBody(cmdData);
    }

    public long getSendTime() {
        return sendTime;
    }
    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}

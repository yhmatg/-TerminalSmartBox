package com.android.terminalbox.rs485;

public class CmdData {
    private byte[] cmdBody;
    public byte[] getCmdBody() {
        return cmdBody;
    }

    public void setCmdBody(byte[] cmdBody) {
        this.cmdBody = cmdBody;
    }
}

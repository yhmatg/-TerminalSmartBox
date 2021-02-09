package com.android.terminalbox.rs485;

public class CmdAutoSend extends CmdData{
    private int intervalMultiplesOf100ms;
    public CmdAutoSend(byte[] cmdData) {
        this.intervalMultiplesOf100ms = IntervalType.ITVAL_FAST_500MS;
        this.setCmdBody(cmdData);
    }

    public CmdAutoSend(byte[] cmdData, int intervalMultiplesOf100ms) {
        this.setCmdBody(cmdData);
        this.intervalMultiplesOf100ms = intervalMultiplesOf100ms;
    }

    public int getIntervalMultiplesOf100ms() {
        return intervalMultiplesOf100ms;
    }

    public void setIntervalMultiplesOf100ms(int intervalMultiplesOf100ms) {
        this.intervalMultiplesOf100ms = intervalMultiplesOf100ms;
    }
}

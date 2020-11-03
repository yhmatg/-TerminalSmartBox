package com.android.terminalbox.uhf;

public class UhfTag {
    public byte AntennaID;
    private String epc;
    private String  tid;
    private int rssi;

    public UhfTag() {
    }

    public UhfTag(byte antennaID, String epc, String tid, int rssi) {
        AntennaID = antennaID;
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
    }

    public byte getAntennaID() {
        return AntennaID;
    }

    public void setAntennaID(byte antennaID) {
        AntennaID = antennaID;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "UhfTag{" +
                "AntennaID=" + AntennaID +
                ", epc='" + epc + '\'' +
                ", tid='" + tid + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}

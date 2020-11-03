package com.android.terminalbox.bean;

import com.android.terminalbox.uhf.UhfTag;

import java.io.Serializable;

public class UhfTagStatus implements Serializable {
    private int status;//在库、出库、入库
    private String batchCode;
    private UhfTag uhfTag;
    private String name;

    public UhfTagStatus() {
    }

    public UhfTagStatus(int status, String epc, String name) {
        UhfTag uhfTag=new UhfTag();
        uhfTag.setEpc(epc);
        this.status = status;
        this.uhfTag = uhfTag;
        this.name = name;
    }

    public UhfTagStatus(int status, String batchCode, UhfTag uhfTag, String name) {
        this.status = status;
        this.batchCode = batchCode;
        this.uhfTag = uhfTag;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UhfTag getUhfTag() {
        return uhfTag;
    }

    public void setUhfTag(UhfTag uhfTag) {
        this.uhfTag = uhfTag;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

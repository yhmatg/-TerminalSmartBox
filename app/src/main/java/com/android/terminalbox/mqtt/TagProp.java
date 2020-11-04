package com.android.terminalbox.mqtt;

public class TagProp {
    public String expressNo;
    public String tagEpc;
    public String whName;
    public String workbench;
    public String deviceSn;
    public String result;

    public TagProp() {
        this.whName = "成都华为电商仓";
        this.workbench = "001";
        this.deviceSn = "001";
        this.result = "true";
    }
    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getTagEpc() {
        return tagEpc;
    }

    public void setTagEpc(String tagEpc) {
        this.tagEpc = tagEpc;
    }

    public String getWhName() {
        return whName;
    }

    public void setWhName(String whName) {
        this.whName = whName;
    }

    public String getWorkbench() {
        return workbench;
    }

    public void setWorkbench(String workbench) {
        this.workbench = workbench;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TagProp{" +
                "expressNo='" + expressNo + '\'' +
                ", tagEpc='" + tagEpc + '\'' +
                ", whName='" + whName + '\'' +
                ", workbench='" + workbench + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}

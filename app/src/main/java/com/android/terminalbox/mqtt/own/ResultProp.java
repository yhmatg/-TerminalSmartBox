package com.android.terminalbox.mqtt.own;

import java.util.List;

public class ResultProp {

    /**
     * cap_id : id1
     * relevance_id : 自定义Id推荐uuid
     * prop : {"openType":"remote","openEkey":true,"closeEkey":true,"rfid_out":["epc01,epc02,epc03"],"rfid_in":["epc04","epc05","epc06"]}
     * data_event_time : 1604300824000
     */

    private String cap_id;
    private String relevance_id;
    private Prop prop;
    private long data_event_time;

    public String getCap_id() {
        return cap_id;
    }

    public void setCap_id(String cap_id) {
        this.cap_id = cap_id;
    }

    public String getRelevance_id() {
        return relevance_id;
    }

    public void setRelevance_id(String relevance_id) {
        this.relevance_id = relevance_id;
    }

    public Prop getProp() {
        return prop;
    }

    public void setProp(Prop prop) {
        this.prop = prop;
    }

    public long getData_event_time() {
        return data_event_time;
    }

    public void setData_event_time(long data_event_time) {
        this.data_event_time = data_event_time;
    }

    public static class Prop {
        /**
         * openType : remote
         * openEkey : true
         * closeEkey : true
         * rfid_out : ["epc01,epc02,epc03"]
         * rfid_in : ["epc04","epc05","epc06"]
         */

        private String openType;
        private boolean openEkey;
        private boolean closeEkey;
        private List<String> rfid_out;
        private List<String> rfid_in;

        public String getOpenType() {
            return openType;
        }

        public void setOpenType(String openType) {
            this.openType = openType;
        }

        public boolean isOpenEkey() {
            return openEkey;
        }

        public void setOpenEkey(boolean openEkey) {
            this.openEkey = openEkey;
        }

        public boolean isCloseEkey() {
            return closeEkey;
        }

        public void setCloseEkey(boolean closeEkey) {
            this.closeEkey = closeEkey;
        }

        public List<String> getRfid_out() {
            return rfid_out;
        }

        public void setRfid_out(List<String> rfid_out) {
            this.rfid_out = rfid_out;
        }

        public List<String> getRfid_in() {
            return rfid_in;
        }

        public void setRfid_in(List<String> rfid_in) {
            this.rfid_in = rfid_in;
        }
    }
}

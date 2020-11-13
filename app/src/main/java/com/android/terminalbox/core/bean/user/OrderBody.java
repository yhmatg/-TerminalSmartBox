package com.android.terminalbox.core.bean.user;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OrderBody {

    /**
     * capId : e684ea3f18cc11ebb7260242ac120004
     * instData : {"relevanceId":"自定义Id推荐uuid","ekey":"on"}
     * instName : openKey
     */

    private String capId;
    private InstData instData;
    private String instName;

    public String getCapId() {
        return capId;
    }

    public void setCapId(String capId) {
        this.capId = capId;
    }

    public InstData getInstData() {
        return instData;
    }

    public void setInstData(InstData instData) {
        this.instData = instData;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public static class InstData {
        /**
         * relevanceId : 自定义Id推荐uuid
         * ekey : on
         */

        private String relevanceId;
        private String ekey;
        private int userId;

        public String getRelevanceId() {
            return relevanceId;
        }

        public void setRelevanceId(String relevanceId) {
            this.relevanceId = relevanceId;
        }

        public String getEkey() {
            return ekey;
        }

        public void setEkey(String ekey) {
            this.ekey = ekey;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
package com.android.terminalbox.core.bean.user;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OrderResponse {

    /**
     * actType : 取件
     * devId : 15aa68f3183311ebb7260242ac120004_uniqueCode002
     * gmtCreate : 1604629104173
     * gmtModified : 1604629104173
     * id : 2c90c40e75920dc701759b59062e001b
     * relevanceId : 1111111111
     * userId : 3
     */

    private String actType;
    private String devId;
    private long gmtCreate;
    private long gmtModified;
    private String id;
    private String relevanceId;
    private int userId;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelevanceId() {
        return relevanceId;
    }

    public void setRelevanceId(String relevanceId) {
        this.relevanceId = relevanceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "actType='" + actType + '\'' +
                ", devId='" + devId + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", id='" + id + '\'' +
                ", relevanceId='" + relevanceId + '\'' +
                ", userId=" + userId +
                '}';
    }
}
package com.android.terminalbox.core.bean.user;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class NewOrderBody {

    /**
     * capId : e684ea3f18cc11ebb7260242ac120004
     * instData : {"relevanceId":"自定义Id推荐uuid","ekey":"on"}
     * instName : openKey
     */

    private String actType;
    private String relevanceId;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getRelevanceId() {
        return relevanceId;
    }

    public void setRelevanceId(String relevanceId) {
        this.relevanceId = relevanceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
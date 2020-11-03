package com.android.terminalbox.uhf;

public class EsimUhfParams {
//    private String hostIp;
//    private int allAntNum;
//    private long readMillisSeconds;//30s
    private int[] antIndex;
    public EsimUhfParams(EsimUhfParams.Builder builder) {
//        hostIp = builder.hostIp!=""? builder.hostIp:"127.0.0.1";
//        allAntNum=builder.allAntNum!=0? builder.allAntNum:4;
        if(builder.antIndex==null||builder.antIndex.length==0){
            antIndex=new int[]{1,2,3,4};
        }else{
            antIndex=builder.antIndex;
        }
//        readMillisSeconds=builder.readMillisSeconds!=0? readMillisSeconds:30000;
    }
//    public String getHostIp() {
//        return hostIp;
//    }
//
//    public void setHostIp(String hostIp) {
//        this.hostIp = hostIp;
//    }
//
//    public int getAllAntNum() {
//        return allAntNum;
//    }
//
//    public void setAllAntNum(int allAntNum) {
//        this.allAntNum = allAntNum;
//    }
    public int[] getAntIndex() {
        return antIndex;
    }

    public void setAntIndex(int[] antIndex) {
        this.antIndex = antIndex;
    }
//
//    public long getReadMillisSeconds() {
//        return readMillisSeconds;
//    }
//
//    public void setReadMillisSeconds(long readMillisSeconds) {
//        this.readMillisSeconds = readMillisSeconds;
//    }
    public static final class Builder {
//        private String hostIp;
//        private int allAntNum;
        private long readMillisSeconds;
        private int[] antIndex;

        public Builder() {

        }
//        public Builder hostIp(String val){
//            hostIp=val;
//            return this;
//        }
//        public Builder allAntNum(int val){
//            allAntNum=val;
//            return this;
//        }
//        public Builder readMillisSeconds(int val){
//            readMillisSeconds=val;
//            return this;
//        }
        public Builder antIndex(int... val){
            antIndex=val;
            return this;
        }
        public EsimUhfParams build() {
            return new EsimUhfParams(this);
        }
    }
}
package com.android.terminalbox.rs485.humiture;

import com.android.terminalbox.rs485.Rs485PackageHandleFilter;
import com.android.terminalbox.rs485.utils.CRC16A001;

public class HumitureRs485PackageHandleFilter extends Rs485PackageHandleFilter {
    private HumitureReadListener humitureReadListener;
    private HumitureRs485PackageHandleFilter(Builder builder) {
        this.humitureReadListener=builder.humitureReadListener;
    }
    public static class Builder {
        private HumitureReadListener humitureReadListener;

        public Builder() {
        }

        public Builder(HumitureReadListener humitureReadListener) {
            this.humitureReadListener = humitureReadListener;
        }

        public Builder humitureReadListener(HumitureReadListener humitureReadListener) {
            this.humitureReadListener = humitureReadListener;
            return this;
        }
        public HumitureRs485PackageHandleFilter build() {
            return new HumitureRs485PackageHandleFilter(this);
        }
    }
    @Override
    public boolean checkMatched(byte[] data) {
        //分割3+BCC2= 5字节
        //地址4+数据长度2=6字节
        if(data.length<5){
            return false;
        }
        int len=data.length;
        byte[] crcData=new byte[data.length-2];
        System.arraycopy(data,0,crcData,0,data.length-2);
        byte[] crc= CRC16A001.crc16ModuleBusLow2Hi(crcData);
        if(crc[0]!=data[len-2] || crc[1]!=data[len-1]){
            return false;
        }
        return true;
    }

    @Override
    public void revPackage(byte[] revData) {
        try {
            byte ekeyAddr=revData[0];
            if(revData[1] == HumitureCmd.CMD_READ){
                int value=revData[3]*256+revData[4];
                byte[] sendData=getDefaultMsgCodeBytes();
                if(sendData==null){
                    return;
                }
                if(sendData[2]==0x00 && sendData[3]==0x00){//温度
                    humitureReadListener.onHumitureValue(ekeyAddr,HumitureType.TEMPRATURE,value);
                }else if(sendData[2]==0x00 && sendData[3]==0x01) {//湿度
                    humitureReadListener.onHumitureValue(ekeyAddr,HumitureType.HUMIDITY,value);
                }
            }
        }catch (Exception ex){

        }
    }
}

package com.android.terminalbox.rs485.ekey;

import com.android.terminalbox.rs485.Rs485PackageHandleFilter;
import com.esim.rylai.smartbox.utils.CRC16;

public class EkeyRs485PackageHandleFilter extends Rs485PackageHandleFilter {
    private  EkeyStatusListener ekeyStatusListener;

    private byte preStatusCmd=(byte) 0xFF;//01 开启  00关闭
    private EkeyRs485PackageHandleFilter(Builder builder) {
        this.ekeyStatusListener=builder.ekeyStatusListener;
    }
    public static class Builder {
        private EkeyStatusListener ekeyStatusListener;

        public Builder() {
        }

        public Builder(EkeyStatusListener ekeyStatusListener) {
            this.ekeyStatusListener = ekeyStatusListener;
        }

        public Builder ekeyStatusListener(EkeyStatusListener ekeyStatusListener) {
            this.ekeyStatusListener = ekeyStatusListener;
            return this;
        }
        public EkeyRs485PackageHandleFilter build() {
            return new EkeyRs485PackageHandleFilter(this);
        }
    }
    @Override
    public boolean checkMatched(byte[] data) {
        //分割3+BCC2= 5字节
        //地址4+数据长度2=6字节
        if(data.length<5+6){
            return false;
        }
        int len=data.length;
        if(data[0]!=EkeyCmd.PACK_SEPARATION || data[len-2]!=EkeyCmd.PACK_SEPARATION ||data[len-1]!=EkeyCmd.PACK_SEPARATION ){
            return false;
        }
        byte[] crc= CRC16.getInstance().getBytesCrc(data,1,len-5);
        if(crc[0]!=data[len-4] || crc[1]!=data[len-3]){
            return false;
        }
        return true;
    }

    @Override
    public void revPackage(byte[] pack) {
        try {
            byte ekeyAddr=pack[1];
            if(pack[8] == 0x05){
                if(pack[9]==0x01){//电子开启成功
                    if(ekeyStatusListener!=null){
                        ekeyStatusListener.onEkeyStatusChange(ekeyAddr,EkeyStatusChange.OPENED_ELECTRONICS);
                    }
                }else {}
            }else if(pack[8] == 0x16){
                if(pack[9]==0x01&&preStatusCmd!=0x01){//01—锁柄物理开启
                    ekeyStatusListener.onEkeyStatusChange(ekeyAddr,EkeyStatusChange.OPENED);
                }else if(pack[9]==0x00&&preStatusCmd!=0x00){//00---锁柄物理关闭
                    ekeyStatusListener.onEkeyStatusChange(ekeyAddr,EkeyStatusChange.CLOSED);
                }
                preStatusCmd=pack[9];
            }
        }catch (Exception ex){

        }
    }
}

package com.android.terminalbox.rs485.ekey;

public class EkeyCmd {
    public final static byte PACK_SEPARATION=0x7E;
//    public final static short PACK_END=0x7E7E;

    public final static short CMD_OPEN_KEY=0x0005;//7E0101000000020005C1777E7E
    public final static short CMD_CHECK_STATUS=0x0016;//7E0101000000020016E3257E7E

    public final static short CMD_OPEN_KEY_REC=0x0005;//7E0101000000020005C1777E7E
    public final static short CMD_CHECK_STATUS_REC=0x0016;//7E0101000000020016E3257E7E

    public final static byte MSG_OPEN_SUC=0x01;//7E0101000000020005C1777E7E
    public final static byte MSG_CHECK_STATUS_01=0x01;//锁柄开启
    public final static byte MSG_CHECK_STATUS_00=0x00;//锁柄关闭
}

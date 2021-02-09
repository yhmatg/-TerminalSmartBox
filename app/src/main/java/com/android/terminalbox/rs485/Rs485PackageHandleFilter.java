package com.android.terminalbox.rs485;

public abstract class Rs485PackageHandleFilter {
    private String defaultMsgCode;

    public String getDefaultMsgCode() {
        return defaultMsgCode;
    }
    public byte[] getDefaultMsgCodeBytes() {
        if(defaultMsgCode!=null) {
            return hexStringToBytes(defaultMsgCode);
        }
        return null;
    }
    public void setDefaultMsgCode(String defaultMsgCode) {
        this.defaultMsgCode = defaultMsgCode;
    }

    public abstract boolean checkMatched(byte[] data);
    public abstract void revPackage(byte[] data);
    private byte[] hexStringToBytes(String hex) {
        if (hex == null || "".equals(hex)) {
            return null;
        }
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] chArr = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chArr[pos]) << 4 | toByte(chArr[pos + 1]));
        }
        return result;
    }
    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}

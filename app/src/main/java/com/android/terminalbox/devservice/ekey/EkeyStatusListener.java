package com.android.terminalbox.devservice.ekey;

public interface EkeyStatusListener {
//    void onOpenSuccess(boolean openOk);
//    void onClose();
//    void onFail(String errMsg );//后续整理枚举
    void onEkeyStatus(EkeyStatus ekeyStatus);
}

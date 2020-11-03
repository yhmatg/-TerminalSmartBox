package com.android.terminalbox.devservice.ekey;

public interface EkeyStatusChangeListener {
//    void onOpenSuccess(boolean openOk);
//    void onClose();
//    void onFail(String errMsg );//后续整理枚举
    void onEkeyStatusChange(EkeyStatus ekeyStatus);
}

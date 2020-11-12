package com.android.terminalbox;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.View;

import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.common.ConstFromSrc;
import com.android.terminalbox.ui.face.RecognizeActivity;
import com.android.terminalbox.ui.inventory.InventoryActivity;
import com.android.terminalbox.ui.inventory.InventoryActivity_ViewBinding;
import com.android.terminalbox.ui.rfid.SmartBoxInvActivity;
import com.android.terminalbox.utils.box.ConfigUtil;

import butterknife.OnClick;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_90_ONLY;

public class MainActivity extends BaseActivity {
    private GestureDetectorCompat mDetector;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {
    }

    @OnClick({ R.id.btn_inv, R.id.btn_access,R.id.bt_change_org})
    public void onClick(View view) {
        Bundle bundle=new Bundle();
        switch (view.getId()) {
            case R.id.btn_inv:
                JumpToActivity(InventoryActivity.class);
                break;
            case R.id.btn_access:
                bundle.putString(ConstFromSrc.activityFrom, ConstFromSrc.tagsOut);
                JumpToActivity(SmartBoxInvActivity.class,bundle);
                break;
            case R.id.bt_change_org:
                bundle.putString(ConstFromSrc.activityFrom,ConstFromSrc.tagsIn);
                ConfigUtil.setFtOrient(this, ASF_OP_90_ONLY);
                break;
        }
    }

}

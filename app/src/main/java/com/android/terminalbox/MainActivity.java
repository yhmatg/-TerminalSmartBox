package com.android.terminalbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.common.ConstFromSrc;
import com.android.terminalbox.ui.face.RecognizeActivity;
import com.android.terminalbox.ui.rfid.SmartBoxInvActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

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

    @OnClick({R.id.btn_inv, R.id.btn_fetch, R.id.btn_back})
    public void onClick(View view) {
        Bundle bundle=new Bundle();
        switch (view.getId()) {
            case R.id.btn_inv:
                JumpToActivity(SmartBoxInvActivity.class);
                break;
            case R.id.btn_fetch:
                bundle.putString(ConstFromSrc.activityFrom, ConstFromSrc.tagsOut);
                JumpToActivity(RecognizeActivity.class,bundle);
                break;
            case R.id.btn_back:
                bundle.putString(ConstFromSrc.activityFrom,ConstFromSrc.tagsIn);
                JumpToActivity(RecognizeActivity.class,bundle);
                break;
        }
    }
}

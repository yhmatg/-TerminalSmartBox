package com.android.terminalbox.base.presenter;

import com.android.terminalbox.base.view.AbstractView;

import io.reactivex.disposables.Disposable;


/**
 * Presenter 基类
 *
 * @author yhm
 * @date 2017/11/27
 */

public interface AbstractPresenter<T extends AbstractView> {

    /**
     * 注入View
     *
     * @param view view
     */
    void attachView(T view);

    /**
     * 回收View
     */
    void detachView();

    /**
     * Add rxBing subscribe manager
     *
     * @param disposable Disposable
     */
    void addRxBindingSubscribe(Disposable disposable);


}

package com.wsvita.ui.popupwindow

import android.app.Activity
import android.widget.PopupWindow

abstract class PopupBaseBuilder<P : PopupWindow> {
    internal var haveCancel : Boolean = false;
    internal var haveTitle : Boolean = true;
    internal var haveSubmit : Boolean = false;
    internal var cancelText : String? = null;
    internal var haveMenu : Boolean = false;
    internal var titleText : String? = null;
    internal var submitText : String? = null;
    internal var width:Int = 0;
    internal var height : Int = 0;
    internal var isTranslucent : Boolean = true;
    internal var onSubmit : (()->Unit)? = null;
    internal var onCancel : (()->Unit)? = null;
    internal var onClose : (()->Unit)? = null;

    /**
     * 是否自动处理Activity Window的alpha值
     */
    internal var autoHandleAlpha : Boolean = false;

    internal var windowAlpha : Float = 0.7F;

    internal var activity : Activity;

    constructor(activity: Activity){
        this.activity = activity;
    }

    fun title(title : String): PopupBaseBuilder<P> {
        this.titleText = title;
        if(!title.isEmpty()){
            this.haveTitle = true;
        }
        return this;
    }


    fun width(width:Int): PopupBaseBuilder<P> {
        this.width = width;
        return this;
    }

    fun height(height:Int): PopupBaseBuilder<P> {
        this.height = height;
        return this;
    }

    fun size(width: Int,height: Int): PopupBaseBuilder<P> {
        this.width = width;
        this.height = height;
        return this;
    }

    fun cancelText(cancelText : String): PopupBaseBuilder<P> {
        this.cancelText = cancelText;
        if(cancelText.isNotEmpty()){
            this.haveCancel = true;
        }
        return this;
    }

    fun submitText(submitText : String): PopupBaseBuilder<P> {
        this.submitText = submitText;
        if(submitText.isNotEmpty()){
            this.haveSubmit = true;
        }
        return this;
    }

    fun haveTitle(haveTitle : Boolean): PopupBaseBuilder<P> {
        this.haveTitle = haveTitle;
        return this;
    }

    fun haveMenu(haveMenu : Boolean): PopupBaseBuilder<P> {
        this.haveMenu = haveMenu;
        return this;
    }


    fun haveCancel(haveCancel : Boolean): PopupBaseBuilder<P> {
        this.haveCancel = haveCancel;
        return this;
    }

    fun haveSubmit(haveSubmit : Boolean): PopupBaseBuilder<P> {
        this.haveSubmit = haveSubmit;
        return this;
    }

    fun isTranslucent(isTranslucent : Boolean): PopupBaseBuilder<P> {
        this.isTranslucent = isTranslucent;
        return this;
    }

    fun onSubmit(onSubmit : (()->Unit)): PopupBaseBuilder<P> {
        this.onSubmit = onSubmit;
        return this;
    }

    fun onCancel(onCancel : (()->Unit)): PopupBaseBuilder<P> {
        this.onCancel = onCancel;
        return this;
    }

    fun onClose(onClose : (()->Unit)): PopupBaseBuilder<P> {
        this.onClose = onClose;
        return this;
    }

    fun autoHandleAlpha(windowAlpha : Float): PopupBaseBuilder<P> {
        this.autoHandleAlpha = true;
        this.windowAlpha = windowAlpha;
        return this;
    }

    abstract fun builder() : P;
}

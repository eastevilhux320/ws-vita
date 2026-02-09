package com.wsvita.ui.popupwindow

import android.app.Activity
import android.view.View
import android.webkit.PermissionRequest
import com.wsvita.ui.R
import com.wsvita.ui.databinding.PopupUiMediaPickerBinding

class MediaPickerPopupwindow : BasePopupWindow<PopupUiMediaPickerBinding, MediaPickerPopupwindow.Builder> {

    private var onAlubm : (()->Unit)? = null;
    private var onCamera : (()->Unit)? = null;

    private constructor(builder: Builder) : super(builder) {
        this.onAlubm = builder.onAlubm;
        this.onCamera = builder.onCamera;
    }

    override fun layoutRes(): Int {
        return R.layout.popup_ui_media_picker;
    }

    override fun onInit(activity: Activity) {
        super.onInit(activity)
        dataBinding.popup = this;


    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.tv_ui_media_picker_album->{
                onAlubm?.invoke();
            }
            R.id.tv_ui_media_picker_camera->{
                onCamera?.invoke();
            }
            R.id.tv_ui_media_picker_cancel->{
                dismiss();
            }
        }
    }

    class Builder : PopupBaseBuilder<MediaPickerPopupwindow>{
        internal var onAlubm : (()->Unit)? = null;
        internal var onCamera : (()->Unit)? = null;

        constructor(activity: Activity) : super(activity){

        }

        fun onAlubm(onAlubm : (()->Unit)): Builder {
            this.onAlubm = onAlubm;
            return this;
        }

        fun onCamera(onCamera : (()->Unit)): Builder {
            this.onCamera = onCamera;
            return this;
        }

        override fun builder(): MediaPickerPopupwindow {
            return MediaPickerPopupwindow(this);
        }
    }

}

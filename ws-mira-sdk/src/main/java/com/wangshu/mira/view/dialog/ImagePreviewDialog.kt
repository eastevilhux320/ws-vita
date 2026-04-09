package com.wangshu.mira.view.dialog

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wangshu.mira.R
import com.wangshu.mira.databinding.DialogMiraImagePreviewBinding
import com.wsvita.framework.GlideApp
import com.wsvita.ui.common.BaseBuilderDialog
import com.wsvita.ui.common.DialogBuilder
import ext.ViewExt.dip2px
import ext.ViewExt.getScreenPair

class ImagePreviewDialog : BaseBuilderDialog<DialogMiraImagePreviewBinding,ImagePreviewDialog.Builder>{
    private var imageUrl : String? = null;

    private constructor(builder: Builder) : super(builder,com.wsvita.ui.R.style.AppDialog){
        this.imageUrl = builder.imageUrl;
    }

    override fun getHeight(): Int {
        return context.getScreenPair().second - 120.dip2px();
    }

    override fun getWidth(): Int {
        return context.getScreenPair().first;
    }

    override fun layoutRes(): Int {
        return R.layout.dialog_mira_image_preview;
    }

    override fun initView() {
        super.initView()
        GlideApp.with(dataBinding.ivFullscreen)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(dataBinding.ivFullscreen)

        dataBinding.ivMiraDialogClose.setOnClickListener {
            dismiss();
        }
    }

    /**
     * 动态更新图片地址并重新加载
     * 适用于 Dialog 未 dismiss 时，外部需要切换图片的场景
     */
    fun updateImageUrl(url: String?) {
        if (url.isNullOrEmpty()) return

        // 1. 更新内部维护的变量，保持数据一致性
        this.imageUrl = url

        // 强制触发 Glide 重新加载以适配动态比例
        GlideApp.with(dataBinding.ivFullscreen)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter() // 确保不同比例的图片都能在全屏范围内按比例展示
            .into(dataBinding.ivFullscreen)

        // 4. 执行手动刷新（确保 DataBinding 立即生效）
        dataBinding.executePendingBindings()
    }




    class Builder(context: Context) : DialogBuilder<ImagePreviewDialog>(context) {
        internal var imageUrl: String? = null

        fun setImageUrl(url: String?): Builder {
            this.imageUrl = url
            return this
        }

        override fun builder(): ImagePreviewDialog {
            return ImagePreviewDialog(this);
        }

    }
}

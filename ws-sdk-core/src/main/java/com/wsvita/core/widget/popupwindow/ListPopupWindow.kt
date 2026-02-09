package com.wsvita.core.widget.popupwindow

import android.app.Activity
import com.wsvita.core.R
import com.wsvita.core.adapter.ListPopupAdapter
import com.wsvita.core.databinding.PopupSdkListBinding
import com.wsvita.core.entity.domain.SLItem
import com.wsvita.ui.popupwindow.BasePopupWindow
import com.wsvita.ui.popupwindow.PopupBaseBuilder
import okhttp3.internal.notify

class ListPopupWindow : BasePopupWindow<PopupSdkListBinding,ListPopupWindow.Builder>{
    private lateinit var listAdapter : ListPopupAdapter;
    private var isMultiSelect : Boolean = false;
    private var onItemClick : ((item : SLItem)->Unit)? = null;

    constructor(builder: Builder) : super(builder) {
        this.isMultiSelect = builder.isMultiSelect;
        listAdapter.isMultiSelect(builder.isMultiSelect);
        this.onItemClick = builder.onItemClick;
    }

    override fun layoutRes(): Int {
        return R.layout.popup_sdk_list;
    }

    override fun onInit(activity: Activity) {
        super.onInit(activity)
        dataBinding.titleText = titleText;
        dataBinding.haveSubmit = haveSubmit;
        dataBinding.submitText = submitText;

        listAdapter = ListPopupAdapter(activity,builder.itemList);
        dataBinding.adapter = listAdapter;

        listAdapter.onSLItemClick {
            onItemClick?.invoke(it);
        }
    }

    fun notifyItemList(itemList : MutableList<SLItem>){
        listAdapter.setList(itemList);
        listAdapter.notifyDataSetChanged();
    }

    class Builder(activity: Activity) : PopupBaseBuilder<ListPopupWindow>(activity) {
        internal var itemList : MutableList<SLItem>? = null;
        internal var isMultiSelect : Boolean = false;
        internal var onItemClick : ((item : SLItem)->Unit)? = null;

        fun itemList(itemList : MutableList<SLItem>): Builder {
            this.itemList = itemList;
            return this;
        }

        fun isMultiSelect(isMultiSelect : Boolean): Builder {
            this.isMultiSelect = isMultiSelect;
            return this;
        }

        fun onItemClick(onItemClick : ((item : SLItem)->Unit)): Builder {
            this.onItemClick = onItemClick;
            return this;
        }

        override fun builder(): ListPopupWindow {
            return ListPopupWindow(this);
        }
    }
}

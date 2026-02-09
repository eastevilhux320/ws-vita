package com.wsvita.core.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wsvita.core.R
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.databinding.RvSdkcoreDateTimeBinding
import com.wsvita.core.datatime.DateTimePresenterImpl
import com.wsvita.core.entity.domain.DateTimeEntity
import com.wsvita.framework.utils.SLog
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.createStrokeDrawable
import ext.ViewExt.getScreenPair

class DateTimeAdapter : AppAdapter<DateTimeEntity>{
    private var params : LinearLayout.LayoutParams? = null;
    private var onDateTime : ((dateTime : DateTimeEntity,position : Int)->Unit)? = null;
    /**
     * 上一次选中的下表
     */
    private var lastSelected : Int = -1;
    private var selBg : Drawable? = null;
    private var todayBg : Drawable? = null;

    constructor(context: Context) : super(context){
        val size = context.getScreenPair().first/7;
        params = LinearLayout.LayoutParams(size,size);
        val color = CoreConfigure.instance.getConfig()?.mainThemeColor?: Color.GRAY;
        selBg = context.createStrokeDrawable(color,5f);
        todayBg = Color.WHITE.createComplexRectDrawable(5f,1f,color);
    }

    override fun onAdapterInit() {
        super.onAdapterInit()
    }

    override fun getLayoutId(): Int {
        return R.layout.rv_sdkcore_date_time;
    }

    override fun onBindingView(root: View, item: DateTimeEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
    }

    override fun onBindItem(binding: ViewDataBinding, item: DateTimeEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        item?.let {e->
            if(binding is RvSdkcoreDateTimeBinding){
                binding.dateTimePresenter = dateTimePresenter;
                binding.position = position;

                SLog.d(TAG,"bindItem,position:${position},timeType:${e.timeType},itemSelect:${e.itemSelect}");

                if(e.timeType == 1){
                    binding.tvDay.setTextColor(binding.root.context.getColor(com.wsvita.ui.R.color.base_app_text));
                }else{
                    //设置字体颜色
                    if(e.isUsed){
                        binding.tvDay.setTextColor(binding.root.context.getColor(com.wsvita.ui.R.color.base_app_text));
                    }else{
                        binding.tvDay.setTextColor(binding.root.context.getColor(com.wsvita.ui.R.color.base_text_hint));
                    }
                    //设置背景色
                    if(2 == e.timeType && e.itemSelect && e.isUsed){
                        binding.tvDay.background = selBg;
                    }else{
                        binding.tvDay.setBackgroundResource(R.drawable.bg_shape_datetime_nor);
                    }
                }
            }
        }
    }

    /**
     * 设置选中的下标值
     * create by Eastevil at 2026/1/29 11:08
     * @author Eastevil
     * @param selected
     *      选中的下标值
     * @return
     *      void
     */
    fun setSelected(selected : Int){
        dataList?.let {list->
            //修改上一次的item
            if(lastSelected != -1 && lastSelected < list.size){
                list.get(lastSelected).itemSelect = false;
                //notifyItemChanged(lastSelected);
            }
            //设置选中的item
            if(selected >= 0 && selected < list.size){
                list.get(selected).itemSelect = true;
                //notifyItemChanged(selected);
            }
            notifyDataSetChanged();
        }
        this.lastSelected = selected;
    }

    fun onDateTime(onDateTime : ((dateTime : DateTimeEntity,position : Int)->Unit)){
        this.onDateTime = onDateTime;
    }

    override fun areItemsTheSame(oldItem: DateTimeEntity, newItem: DateTimeEntity): Boolean {
        return oldItem.timestamp == newItem.timestamp;
    }

    override fun areContentsTheSame(oldItem: DateTimeEntity, newItem: DateTimeEntity): Boolean {
        return oldItem.timestamp == newItem.timestamp;
    }

    private val dateTimePresenter = object : DateTimePresenterImpl() {

        override fun onEntityClick(entity: DateTimeEntity, position: Int) {
            super.onEntityClick(entity, position)
            if(entity.isUsed && 2 == entity.timeType){
                //可以被选中
                onDateTime?.invoke(entity,position);
            }
        }
    }

    companion object {
        private const val TAG = "WSVita_Adapter_DateTimeAdapter==>"
    }
}

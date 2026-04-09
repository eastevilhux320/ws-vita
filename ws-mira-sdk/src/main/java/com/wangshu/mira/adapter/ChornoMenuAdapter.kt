package com.wangshu.mira.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.wangshu.mira.R
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.databinding.RecyclerItemMiraChronoMenuBinding
import com.wangshu.mira.entity.ChronoMenuEntity
import com.wsvita.core.common.adapter.AppAdapter
import ext.ViewExt.dip2px

class ChornoMenuAdapter : AppAdapter<ChronoMenuEntity>{

    private var num : Int = 0;

    private var params : ConstraintLayout.LayoutParams? = null;

    private var themeColor : Int = Color.BLACK;
    private var defualtTextColor : Int = Color.BLACK;

    private var lastSelect : Int = -1;

    private var onMenuClick : ((memu : ChronoMenuEntity,position : Int)->Unit)? = null;


    constructor(context: Context, dataList : MutableList<ChronoMenuEntity>?) : super(context, dataList){
        themeColor = MiraConfigure.instance.getConfig()?.mainThemeColor?:context.getColor(R.color.mira_main_theme_color);
        defualtTextColor = context.getColor(com.wsvita.ui.R.color.base_app_text);
        updateFooterState(FooterState.HIDE);
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_mira_chrono_menu;
    }

    override fun notifyDataChangedSafe() {
        super.notifyDataChangedSafe()
    }

    override fun onBindingView(root: View, item: ChronoMenuEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;

        root.setOnClickListener {
            if (item != null) {
                onMenuClick?.invoke(item,position);
            }
        }
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    override fun hasHeader(): Boolean {
        return super.hasHeader()
    }

    fun onMenuClick(onMenuClick : ((memu : ChronoMenuEntity,position : Int)->Unit)){
        this.onMenuClick = onMenuClick;
    }

    override fun onBindItem(binding: ViewDataBinding, item: ChronoMenuEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        item?.let {m->
            if(binding is RecyclerItemMiraChronoMenuBinding){
                binding.chronoMneu = item;
                if(m.itemSelect){
                    //选中
                    binding.tvChronoName.setTextSize(16f);
                    binding.tvChronoName.setTextColor(themeColor)
                    binding.tvChronoMenuLine.visibility = View.VISIBLE;
                    binding.tvChronoMenuLine.setBackgroundColor(themeColor);
                }else{
                    //非选中
                    binding.tvChronoName.setTextSize(13f);
                    binding.tvChronoName.setTextColor(defualtTextColor)
                    binding.tvChronoMenuLine.visibility = View.GONE;
                }
            }
        }
    }

    fun countItemWidth(screenWidth : Int,num : Int){
        this.num = num;
        val width = (screenWidth - (num - 1))/num;
        val height = 40.dip2px();
        params = ConstraintLayout.LayoutParams(width,height);
    }

    fun setSelected(select : Int){
        dataList?.let {
            if(lastSelect != -1 && it.size > lastSelect){
                it.get(lastSelect).itemSelect = false;
                notifyItemChanged(lastSelect)
            }
            it.get(select).itemSelect = true;
            notifyItemChanged(select);
            lastSelect = select;
        }
    }
}

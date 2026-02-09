package com.wsvita.biz.core.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wsvita.core.common.BaseEntity
import com.wsvita.framework.utils.SLog
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
/**
 * 通用的 TagAdapter 基类，适用于基于 MVVM 架构和 DataBinding 的 TagFlowLayout。
 *
 * @param D 继承自 ViewDataBinding，用于绑定 item 的布局
 * @param T 继承自 BaseEntity，表示单个数据项的类型
 *
 * 使用方式：
 * - 继承该类并实现 [getLayoutRes] 和 [onBindView] 方法
 * - 在 Adapter 中进行数据绑定和事件处理
 */
abstract class BaseTagAdapter<D : ViewDataBinding,T : BaseEntity> : TagAdapter<T>{

    companion object{
        private const val TAG = "WSV_Bizcore_BaseTagAdapter=>"
    }

    protected lateinit var dataBinding : D;
    private lateinit var inflater : LayoutInflater;

    private var onItemClick : ((position : Int,entity : T)->Unit)? = null;

    constructor(context: Context,dataList : MutableList<T>?) : super(dataList) {
        inflater = LayoutInflater.from(context);
    }

    override fun getView(parent: FlowLayout?, position: Int, t: T): View {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), parent, false)
        setBean(t);
        setBean(dataBinding,t,position);
        onBindView(dataBinding,t,position);
        return dataBinding.root;
    }

    /**
     * 返回布局资源 ID，用于绑定布局文件
     * @author Eastevil
     * @createTime 2026/1/15 17:13
     * @since
     * @see
     * @return
     *      布局文件的资源 ID，例如 R.layout.item_tag
     */
    abstract fun getLayoutRes() : Int;

    /**
     * 数据绑定回调，用于将数据与视图绑定
     *
     * @author Eastevil
     * @createTime 2026/1/15 17:13
     * @param dataBinding
     *      当前 item 的 DataBinding 实例
     * @param item
     *      当前的数据对象
     * @param position
     *      当前的位置
     * @since
     * @see
     * @return
     *      void
     */
    abstract fun onBindView(dataBinding : D,item : T,position : Int);

    open fun setBean(item : T){
        SLog.d(TAG,"setBean")
    }

    open fun setBean(dataBinding: D,item : T,position : Int){
        SLog.d(TAG,"setBean")
    }

    /**
     * 【反射方法】设置新的数据列表并通知数据变更。
     * * 通过反射修改父类 (TagAdapter) 的私有字段 mTagDatas，
     * 避免因父类未提供公共方法而无法更新数据的问题。
     *
     * ⚠️ 注意：此方法依赖父类 TagAdapter 的私有字段名 "mTagDatas"。
     * 如果第三方库更新并修改字段名，此方法将抛出异常。
     *
     * @param newDataList 新的数据列表
     */
    fun setDataList(newDataList: MutableList<T>) {
        try {
            // 1. 获取 TagAdapter.class
            val superClazz = TagAdapter::class.java

            // 2. 获取私有字段 mTagDatas
            val field = superClazz.getDeclaredField("mTagDatas")

            // 3. 设置字段可访问
            field.isAccessible = true

            // 4. 设置新的数据列表到当前实例的 mTagDatas 字段
            field.set(this, newDataList)

            // 5. 通知数据变更，刷新视图
            //notifyDataChanged()
        } catch (e: java.lang.Exception) {
            // 字段名不匹配（如库升级）
            SLog.e(TAG, "setDataList error: Field 'mTagDatas' not found in TagAdapter.")
        } catch (e: IllegalAccessException) {
            // 访问权限问题
            SLog.e(TAG, "setDataList error: Cannot access or modify 'mTagDatas'.")
        } catch (e: Exception) {
            SLog.e(TAG, "setDataList error: An unknown error occurred.")
        }
    }

    override fun onSelected(position: Int, view: View?) {
        super.onSelected(position, view)
        val binding = view?.let { DataBindingUtil.bind<D>(it) };
        onItemSelected(position,binding);
    }

    override fun unSelected(position: Int, view: View?) {
        super.unSelected(position, view)
        val binding = view?.let { DataBindingUtil.bind<D>(it) };
        onItemUnSelected(position,binding);
    }

    open fun onItemSelected(position: Int,dataBinding: D?){
        SLog.d(TAG,"onItemSelected,position:${position}")
        onItemClick?.invoke(position,getItem(position))
    }

    open fun onItemUnSelected(position: Int,dataBinding: D?){
        SLog.d(TAG,"onItemUnSelected,position:${position}")
    }

    fun onItemClick(onItemClick : ((position : Int,entity : T)->Unit)){
        this.onItemClick = onItemClick;
    }
}

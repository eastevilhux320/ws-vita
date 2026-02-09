package com.wsvita.core.widget.popupwindow

import android.app.Activity
import android.view.View
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import com.wsvita.core.R
import com.wsvita.core.adapter.PlateKeyboardAdapter
import com.wsvita.core.adapter.PlateLetterAdapter
import com.wsvita.core.adapter.PlateLicenseAdapter
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.databinding.PopupSdkPlateLicenseBinding
import com.wsvita.core.entity.domain.PlateLicenseEntity
import com.wsvita.core.ext.SDKExt.mainThread
import com.wsvita.core.network.model.SDKModel
import com.wsvita.core.recycler.GridSpaceItemDecoration
import com.wsvita.framework.utils.VToast
import com.wsvita.ui.popupwindow.BasePopupWindow
import com.wsvita.ui.popupwindow.PopupBaseBuilder
import ext.StringExt.isInvalid
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.dip2px
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class PlateLicensePopupwindow :
    BasePopupWindow<PopupSdkPlateLicenseBinding, PlateLicensePopupwindow.Builder> {

    private var cachePlateLicenseList : MutableList<PlateLicenseEntity>? = null;

    private lateinit var popupJob: CompletableJob;
    private lateinit var popupScope : CoroutineScope;

    private lateinit var provinceName : ObservableField<String>;
    private lateinit var letterCode : ObservableField<String>;

    private var showLoading : (()->Unit)? = null;
    private var dismissLoading : (()->Unit)? = null;

    private var plateAdapter : PlateLicenseAdapter? = null;
    private var letterAdapter : PlateLetterAdapter? = null;

    /**
     * 选中的车牌城市
     */
    private var plateLicense : PlateLicenseEntity? = null;

    /**
     * 车牌号回调
     */
    private var onPlateLicense : ((plateLicense : String)->Unit)? = null;


    private constructor(builder : Builder) : super(builder){
        showLoading = builder.showLoading;
        dismissLoading = builder.dismissLoading;
        onPlateLicense = builder.onPlateLicense;
    }

    override fun onInit(activity: Activity) {
        super.onInit(activity)
        dataBinding.popup = this;
        popupJob = Job();
        popupScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + popupJob);

        provinceName = ObservableField();
        dataBinding.provinceName = provinceName;

        letterCode = ObservableField();
        dataBinding.letterCode = letterCode;

        initKeyboard();

        val plateLayoutManager = GridLayoutManager(activity,8);
        dataBinding.rvPlateLicense.layoutManager = plateLayoutManager;
        plateAdapter = PlateLicenseAdapter(activity,8,40.dip2px());
        dataBinding.rvPlateLicense.adapter = plateAdapter;

        val divider = GridSpaceItemDecoration.build()
            .spacing(3.dip2px(),3.dip2px())
            .spanCount(8);
        dataBinding.rvPlateLicense.addItemDecoration(divider);

        val letterLayoutManager = GridLayoutManager(activity,8);
        dataBinding.rvPlateAlphabetic.layoutManager = letterLayoutManager;
        letterAdapter = PlateLetterAdapter(activity,8,40.dip2px());
        dataBinding.rvPlateAlphabetic.adapter = letterAdapter;
        dataBinding.rvPlateAlphabetic.addItemDecoration(divider);

        val themeColor = CoreConfigure.instance.getConfig()?.mainThemeColor;
        dataBinding.tvPlateLincseOk.background = themeColor?.createComplexRectDrawable(25.dip2px().toFloat());

        plateAdapter?.setOnPlateLicenseClick {
            //车牌城市点击事件监听
            it?.let {
                dataBinding.rvPlateLicense.visibility = View.GONE;
                dataBinding.rvPlateAlphabetic.visibility = View.VISIBLE;
                selectProvince(it);
            }
        }

        letterAdapter?.onItemClick {
            if(plateLicense == null){
                //选中的车牌城市为null，直接重置
                popupScope.launch {
                    load();
                }
            }else{
                letterCode.set(it);
                dataBinding.tvPlateLetterCode.setText(it);
                updateLicenseNum();
            }
        }

        if(builder.needLoadData){
            popupScope.launch {
                load();
            }
        }
    }

    override fun layoutRes(): Int {
        return R.layout.popup_sdk_plate_license;
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.tv_plate_province_name->{
                dataBinding.rvPlateLicense.visibility = View.VISIBLE;
                dataBinding.rvPlateAlphabetic.visibility = View.GONE;
                if(cachePlateLicenseList != null && cachePlateLicenseList?.isNotEmpty() == true){
                    plateAdapter?.setList(cachePlateLicenseList);
                    plateAdapter?.notifyDataSetChanged();
                }else{
                    popupScope.launch {
                        load();
                    }
                }
            }
            R.id.tv_plate_letter_code->{
                plateLicense?.let {
                    dataBinding.rvPlateLicense.visibility = View.GONE;
                    dataBinding.rvPlateAlphabetic.visibility = View.VISIBLE;
                    selectProvince(it);
                }?:let {
                    dataBinding.rvPlateLicense.visibility = View.VISIBLE;
                    dataBinding.rvPlateAlphabetic.visibility = View.GONE;
                    popupScope.launch {
                        load();
                    }
                }
            }
            R.id.tv_plate_lincse_ok->{
                if (plateLicense == null) {
                    VToast.show(R.string.sdkcore_plate_license_select)
                    popupScope.launch { load() }
                    return
                }

                val letterCodeNum = letterCode.get()
                if (letterCodeNum == null || letterCodeNum.isInvalid()) {
                    VToast.show(R.string.sdkcore_plate_license_select_letter)
                    return
                }

                val editTexts = listOf(
                    dataBinding.editPlateLicense1,
                    dataBinding.editPlateLicense2,
                    dataBinding.editPlateLicense3,
                    dataBinding.editPlateLicense4,
                    dataBinding.editPlateLicense5,
                    dataBinding.editPlateLicense6
                )

                // --- 修正逻辑：仅判断前 5 位必填 ---
                // take(5) 表示只取列表前 5 个元素进行非空检查
                if (editTexts.take(5).any { it.text.isNullOrBlank() }) {
                    VToast.show(R.string.sdkcore_plate_license_input)
                    return
                }

                // --- 拼接最终结果 ---
                val sb = StringBuilder()
                sb.append(plateLicense!!.abbreviation)
                    .append(letterCodeNum)
                    .append("·")

                // 循环添加内容（第 6 位为空也会被 append ""，符合预期）
                editTexts.forEach { sb.append(it.text.toString().trim()) }
                val finalPlate = sb.toString()

                onPlateLicense?.invoke(finalPlate);
                dismiss();
            }
        }
    }

    private fun selectProvince(plateLicense : PlateLicenseEntity){
        this.plateLicense = plateLicense;
        provinceName.set(plateLicense.abbreviation);
        updateLicenseNum();
        val alphabeticList = plateLicense.alphabeticList;
        if(alphabeticList != null && alphabeticList.isNotEmpty()){
            //直接展示车牌字母列表
            letterAdapter?.setList(alphabeticList);
            letterAdapter?.notifyDataSetChanged();
        }else{
            //调用接口获取车牌城市的字母列表
            popupScope.launch {
                plateAlphabeticList(plateLicense.id);
            }
        }
    }

    private suspend fun load(){
        if(cachePlateLicenseList != null && cachePlateLicenseList?.isNotEmpty() == true){
            mainThread {
                plateAdapter?.setList(cachePlateLicenseList);
                plateAdapter?.notifyDataSetChanged();
            }
        }else{
            mainThread{
                showLoading?.invoke();
            }
            val result = SDKModel.instance.allPlateList();
            mainThread{
                dismissLoading?.invoke();
            }
            if(result.isSuccess){
                //获取所有的车牌城市成功
                mainThread {
                    updateLicenseNum();
                    plateAdapter?.setList(result.data);
                    plateAdapter?.notifyDataSetChanged();
                }
            }else{
                //加载失败

            }
        }
    }

    private suspend fun plateAlphabeticList(id : Long){
        mainThread{
            showLoading?.invoke();
        }
        val result = SDKModel.instance.plateAlphabeticList(id);
        mainThread{
            dismissLoading?.invoke();
        }
        if(result.isSuccess){
            mainThread {
                letterAdapter?.setList(result.data);
                letterAdapter?.notifyDataSetChanged();
            }
        }else{
            //获取字母列表出错
        }
    }

    /**
     * 实时更新 licenseNum 字段，驱动 UI 显示
     */
    private fun updateLicenseNum() {
        val sb = StringBuilder()

        // 1. 省份与字母
        val province = provinceName.get() ?: ""
        val letter = letterCode.get() ?: ""
        sb.append(province).append(letter)

        // 2. 中间点（仅当有前缀时添加）
        if (sb.isNotEmpty()) sb.append("·")

        // 3. 拼接 6 位输入框内容
        val editTexts = listOf(
            dataBinding.editPlateLicense1,
            dataBinding.editPlateLicense2,
            dataBinding.editPlateLicense3,
            dataBinding.editPlateLicense4,
            dataBinding.editPlateLicense5,
            dataBinding.editPlateLicense6
        )

        editTexts.forEach {
            val text = it.text.toString().trim()
            if (text.isNotEmpty()) {
                sb.append(text)
            } else {

            }
        }
        dataBinding.tvPlateLicenseNum.setText(sb.toString());
    }

    /**
     * 键盘与输入框逻辑初始化
     * 仅负责：软键盘适配、焦点流转、输入跳格、回删处理
     */
    private fun initKeyboard() {
        // 必须设置 isFocusable，EditText 才能获取焦点并弹出系统键盘
        this.isFocusable = true
        // 设置 ADJUST_RESIZE 确保键盘弹出时，布局整体上移，不被键盘遮挡
        this.softInputMode = android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

        // 2. 获取所有的输入框集合
        val editTexts = listOf(
            dataBinding.editPlateLicense1,
            dataBinding.editPlateLicense2,
            dataBinding.editPlateLicense3,
            dataBinding.editPlateLicense4,
            dataBinding.editPlateLicense5,
            dataBinding.editPlateLicense6
        )
        // 定义大写过滤器
        val allCapsFilter = android.text.InputFilter.AllCaps()

        // 3. 遍历处理每个 EditText 的输入交互
        editTexts.forEachIndexed { index, editText ->
            // 1. 添加过滤器（注意不要覆盖原有的 filters，所以用数组添加）
            editText.filters = arrayOf(allCapsFilter, android.text.InputFilter.LengthFilter(1))

            // A. 自动跳格逻辑：输入满 1 位自动跳转到下一个
            editText.addTextChangedListener(object : android.text.TextWatcher {
                override fun afterTextChanged(s: android.text.Editable?) {
                    if (s?.length == 1) {
                        if (index < editTexts.size - 1) {
                            editTexts[index + 1].requestFocus()
                        }
                    }
                    updateLicenseNum();
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // B. 回退删除逻辑：在空格按删除键时跳回上一个
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                    && event.action == android.view.KeyEvent.ACTION_DOWN
                    && editText.text.isEmpty() && index > 0) {

                    val lastEdit = editTexts[index - 1]
                    lastEdit.requestFocus()
                    // 可选：跳回时是否清空上一个格子的内容
                    // lastEdit.setText("")
                    true
                } else {
                    false
                }
            }

            // C. 焦点监听（仅保留焦点状态处理，不干预业务）
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    // 如果需要在这里处理输入框的高亮样式，可以在此实现
                }
            }
        }
    }

    class Builder(activity: Activity) : PopupBaseBuilder<PlateLicensePopupwindow>(activity) {

        internal var showLoading : (()->Unit)? = null;
        internal var dismissLoading : (()->Unit)? = null;

        /**
         * 是否由弹出框自行加载车牌城市信息
         */
        internal var needLoadData : Boolean = true;

        /**
         * 车牌号回调
         */
        internal var onPlateLicense : ((plateLicense : String)->Unit)? = null;

        fun loading(showLoading : (()->Unit),dismissLoading : (()->Unit)): Builder {
            this.showLoading = showLoading;
            this.dismissLoading = dismissLoading;
            return this;
        }

        fun needLoadData(needLoadData : Boolean): Builder {
            this.needLoadData = needLoadData;
            return this;
        }

        fun onPlateLicense(onPlateLicense : ((plateLicense : String)->Unit)): Builder {
            this.onPlateLicense = onPlateLicense
            return this;
        }

        override fun builder(): PlateLicensePopupwindow {
            return PlateLicensePopupwindow(this);
        }
    }
}

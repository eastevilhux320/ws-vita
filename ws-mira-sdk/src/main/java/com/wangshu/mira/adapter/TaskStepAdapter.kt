package com.wangshu.mira.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wangshu.mira.R
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepAudioBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepDocumentBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepImgHBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepImgNoexampleBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepImgVBinding
import com.wangshu.mira.databinding.RecyclerItemMiraTaskstepVideoBinding
import com.wangshu.mira.entity.InputContentEntity
import com.wangshu.mira.entity.StepInputEntity
import com.wangshu.mira.entity.TaskStepEntity
import com.wangshu.mira.entity.TaskStepInputItemEntity
import com.wangshu.mira.entity.enums.StepFileType
import com.wangshu.mira.ext.StringExt.parseHtml
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.framework.GlideApp
import com.wsvita.framework.utils.SLog
import ext.ViewExt.dip2px
import java.io.File

class TaskStepAdapter : AppAdapter<TaskStepEntity> {

    companion object {
        private const val TAG = "Mira_Adapter_TaskStepAdapter=>"
        private const val EXAMPLEDISPLAY_TYPE_H = 1 // 横向：单图撑满
        private const val EXAMPLEDISPLAY_TYPE_V = 2 // 纵向：双图并排
    }

    private var themeColor: Int
    private var lineColor: Int
    private var stepOrderBg: GradientDrawable? = null

    /**
     * 查看示例图的事件监听
     */
    private var onWatchExampleImage : ((url : String?)->Unit)? = null;
    private var onImageSeleted : ((position : Int,step : TaskStepEntity)->Unit)? = null;

    private var screenWidth : Int = 0;

    private var inputMap : HashMap<Long,MutableList<TaskStepInputItemEntity>>;

    constructor(context: Context, dataList: MutableList<TaskStepEntity>?) : super(context, dataList) {
        updateFooterState(FooterState.HIDE);
        themeColor = MiraConfigure.instance.getConfig()?.mainThemeColor
            ?: context.getColor(R.color.mira_main_theme_color)
        lineColor = context.getColor(com.wsvita.ui.R.color.color_base_line)
        stepOrderBg = createDynamicCircle(themeColor, lineColor, 1.dip2px())

        // 横向：(屏幕宽度 - 60dp - 10dp) / 2
        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels

        inputMap = HashMap();
    }

    override fun isUsedAdapterLayout(): Boolean {
        //使用步骤中的布局定义id，不同的步骤，使用的布局资源id不同,在步骤对象中查看
        return false;
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    override fun hasHeader(): Boolean {
        return false;
    }

    override fun hasEmpty(): Boolean {
        return false;
    }

    override fun getFooterLayoutId(): Int {
        return super.getFooterLayoutId()
    }

    override fun onBindItemData(binding: ViewDataBinding, item: TaskStepEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        bindStepOrder(binding,item,position);
        val submitType = item.submitFileType?.let { StepFileType.getByType(it) }?:StepFileType.UNKNOWN;
        if(submitType.isImage()){
            //图片类型处理
            if(binding is RecyclerItemMiraTaskstepImgHBinding){
                //横向布局
                bindExampleH(binding,item,position);
            }else if(binding is RecyclerItemMiraTaskstepImgVBinding){
                //纵向布局
                bindExampleV(binding,item,position);
            }
        }else{
            SLog.d(TAG,"unknow submit type");
        }
        updateFooterState(FooterState.HIDE);
    }

    fun onWatchExampleImage(onWatchExampleImage : ((url : String?)->Unit)){
        this.onWatchExampleImage = onWatchExampleImage;

    }

    fun onImageSeleted(onImageSeleted : ((position : Int,step : TaskStepEntity)->Unit)){
        this.onImageSeleted = onImageSeleted;
    }

    /**
     * 填充指定的下标
     * create by Eastevil at 2026/3/20 15:22
     * @author Eastevil
     * @param
     * @return
     */
    fun fillingSubmitFile(file : File, position: Int){
        val step = dataList?.get(position);
        step?.let {
            val submitType = it.submitFileType?.let { StepFileType.getByType(it) }?:StepFileType.UNKNOWN;
            if(submitType.isImage()){
                //图片文件
                it.stepFile = file;
                it.isFilling = true;
                //更新单个的item
                dataList?.set(position,it);
                notifyDataSetChanged();
            }else if(submitType.isVideo()){

            }else if(submitType.isAudio()){

            }else if(submitType.isDoc()){

            }else{

            }
        }
    }

    fun getInputList(): MutableList<InputContentEntity> {
        val list = mutableListOf<InputContentEntity>();
        inputMap.forEach {
            val stepId = it.key;
            val itemList = it.value;
            itemList.forEach {
                val ic = InputContentEntity();
                ic.stepId = stepId;
                ic.inputId = it.inputData?.id;
                ic.inputContent = it.edit?.text?.toString();
                ic.fieldKey = it.inputData?.fieldKey;
                ic.fieldValue = it.inputData?.fieldKey
                ic.type = it.inputData?.sourceType;
                list.add(ic);
            }
        }
        return list;
    }


    /**
     * 统一分发步骤基础 UI 绑定逻辑。
     * 由于 TaskStep 采用多布局设计（Audio/Video/ImgH 等），
     * 此方法根据不同的 ViewDataBinding 类型，将通用的“步骤序号”、“标题”、“描述”分发到对应的处理函数。
     * * @param binding 当前 Item 的 ViewDataBinding 实例
     * @param step 步骤实体数据
     * @param position 列表索引
     */
    private fun bindStepOrder(binding: ViewDataBinding, step: TaskStepEntity,position: Int) {
        SLog.d(TAG,"bindStepOrder");
        //绑定对应的输入内容，输入内容是所有布局都会有的
        val inputList = step.inputList;

        var inputAdapter : StepInputAdapter? = null;
        var tag = binding.root.tag;

        var setpInputList : MutableList<TaskStepInputItemEntity>? = null;

        if(tag is StepInputAdapter){
            inputAdapter = tag;
            setpInputList = inputAdapter.getInputList();
            setpInputList?.forEach {s->
                inputList?.forEach {inp->
                    if(s.inputData?.stepId == inp.stepId){
                        inp.localInputText = s.localInput;
                    }
                }
            }
            inputAdapter.setList(inputList);
        }else{
            inputAdapter = StepInputAdapter(binding.root.context,inputList);
            setpInputList = inputAdapter.getInputList();
        }


        if(binding is RecyclerItemMiraTaskstepAudioBinding){
            bindStepOrderAudio(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }else if(binding is RecyclerItemMiraTaskstepDocumentBinding){
            bindStepOrderDocument(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }else if(binding is RecyclerItemMiraTaskstepImgHBinding){
            bindStepOrderImgH(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }else if(binding is RecyclerItemMiraTaskstepImgNoexampleBinding){
            bindStepOrderImgNoexample(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }else if(binding is RecyclerItemMiraTaskstepImgVBinding){
            bindStepOrderImgV(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }else if(binding is RecyclerItemMiraTaskstepVideoBinding){
            bindStepOrderVideo(binding,step);
            binding.rvMiraStepInput.adapter = inputAdapter;
        }
        binding.root.tag = inputAdapter;
        inputAdapter.notifyDataSetChanged();

        inputList?.let {
            setpInputList?.let { it1 -> inputMap.put(step.id, it1) };
        }
    }

    /*******************************************步骤处理开始***********************************************************/
    private fun bindStepOrderAudio(binding : RecyclerItemMiraTaskstepAudioBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    private fun bindStepOrderDocument(binding: RecyclerItemMiraTaskstepDocumentBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    private fun bindStepOrderImgH(binding: RecyclerItemMiraTaskstepImgHBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    private fun bindStepOrderImgNoexample(binding: RecyclerItemMiraTaskstepImgNoexampleBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    private fun bindStepOrderImgV(binding: RecyclerItemMiraTaskstepImgVBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    private fun bindStepOrderVideo(binding: RecyclerItemMiraTaskstepVideoBinding,step: TaskStepEntity){
        binding.tvMiraStepName.text = step.stepTitle
        binding.tvMiraStepDescription.text = step.stepDescription.parseHtml()
        binding.tvMiraStepDescription.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        binding.tvMiraStepOrder.background = stepOrderBg
        binding.tvMiraStepOrder.text = step.stepOrder.toString()
        binding.tvMiraStepOrder.setTextColor(Color.WHITE)
        binding.tvMiraStepText.setTextColor(themeColor)
        binding.tvMiraStepGuide.setTextColor(themeColor)
    }
    /*******************************************步骤处理结束***********************************************************/


    /**
     * 绑定横向示例图布局 (Horizontal Layout)
     * 场景：单张图片撑满宽度，或者示例图与上传位左右/上下分布。
     * 逻辑：根据图片的真实宽高比，动态调整 ImageView 的高度，确保 UI 不变形且无黑边。
     *
     * @param binding DataBinding 实例，对应横向布局
     * @param step 步骤实体数据
     * @param position 列表索引
     */
    private fun bindExampleH(binding: RecyclerItemMiraTaskstepImgHBinding, step: TaskStepEntity, position: Int) {
        val targetWidth = (screenWidth - 60.dip2px() - 10.dip2px()) / 2

        binding.ivMiraStepExampleH.setOnClickListener {
            onWatchExampleImage?.invoke(step.exampleImageUrl)
        }
        binding.ivMiraStepImageH.setOnClickListener {
            onImageSeleted?.invoke(position, step)
        }

        // 1. 加载示例图：只更新自己的尺寸
        GlideApp.with(binding.ivMiraStepExampleH)
            .asBitmap()
            .load(step.exampleImageUrl)
            .placeholder(com.wsvita.ui.R.drawable.ui_banner_default_grey)
            .error(com.wsvita.ui.R.drawable.ui_banner_default_grey)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    updateSingleViewSize(binding.ivMiraStepExampleH, targetWidth, resource.width, resource.height)
                    binding.ivMiraStepExampleH.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
                    val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
                    updateSingleViewSize(binding.ivMiraStepExampleH, targetWidth, w, h)
                    super.onLoadFailed(errorDrawable)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.ivMiraStepExampleH.setImageDrawable(placeholder)
                }
            })

        // 2. 加载填充图：只更新自己的尺寸
        GlideApp.with(binding.ivMiraStepImageH)
            .clear(binding.ivMiraStepImageH)

        if(step.stepFile == null){
            val errorDrawable = binding.root.context.getDrawable(R.drawable.icon_mira_example_add)
            val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
            val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
            updateSingleViewSize(binding.ivMiraStepImageH, targetWidth, w, h)
            binding.ivMiraStepImageH.setImageDrawable(errorDrawable)
        }else{
            GlideApp.with(binding.ivMiraStepImageH)
                .asBitmap()
                .load(step.stepFile)
                .placeholder(R.drawable.icon_mira_example_add)
                .error(R.drawable.icon_mira_example_add)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        updateSingleViewSize(binding.ivMiraStepImageH, targetWidth, resource.width, resource.height)
                        binding.ivMiraStepImageH.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
                        val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
                        updateSingleViewSize(binding.ivMiraStepImageH, targetWidth, w, h)
                        super.onLoadFailed(errorDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.ivMiraStepImageH.setImageDrawable(placeholder)
                    }
                })
        }
    }

    /**
     * 绑定纵向示例图布局 (Vertical Layout)
     * 场景：通常用于长图或需要大面积展示的示例图。
     * 逻辑：宽度通常占据屏幕大部分（扣除页边距），高度随图片长短自由伸缩。
     *
     * @param binding DataBinding 实例，对应纵向布局
     * @param step 步骤实体数据
     * @param position 列表索引
     */
    private fun bindExampleV(binding: RecyclerItemMiraTaskstepImgVBinding, step: TaskStepEntity, position: Int) {
        val targetWidth = screenWidth - 60.dip2px()

        binding.ivMiraStepExampleV.setOnClickListener {
            onWatchExampleImage?.invoke(step.exampleImageUrl)
        }
        binding.ivMiraStepImageV.setOnClickListener {
            onImageSeleted?.invoke(position, step)
        }

        // 1. 加载示例图：只更新自己的尺寸
        GlideApp.with(binding.ivMiraStepExampleV)
            .asBitmap()
            .load(step.exampleImageUrl)
            .placeholder(com.wsvita.ui.R.drawable.ui_banner_default_grey)
            .error(com.wsvita.ui.R.drawable.ui_banner_default_grey)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    updateSingleViewSize(binding.ivMiraStepExampleV, targetWidth, resource.width, resource.height)
                    binding.ivMiraStepExampleV.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
                    val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
                    updateSingleViewSize(binding.ivMiraStepExampleV, targetWidth, w, h)
                    super.onLoadFailed(errorDrawable)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.ivMiraStepExampleV.setImageDrawable(placeholder)
                }
            })

        // 2. 加载填充图：只更新自己的尺寸
        GlideApp.with(binding.ivMiraStepImageV)
            .clear(binding.ivMiraStepImageV)
        if(step.stepFile == null){
            val errorDrawable = binding.root.context.getDrawable(R.drawable.icon_mira_example_add)
            val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
            val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
            updateSingleViewSize(binding.ivMiraStepImageV, targetWidth, w, h)
            binding.ivMiraStepImageV.setImageDrawable(errorDrawable)
        }else{
            GlideApp.with(binding.ivMiraStepImageV)
                .asBitmap()
                .load(step.stepFile)
                .placeholder(R.drawable.icon_mira_example_add)
                .error(R.drawable.icon_mira_example_add)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        updateSingleViewSize(binding.ivMiraStepImageV, targetWidth, resource.width, resource.height)
                        binding.ivMiraStepImageV.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        val w = errorDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: 1
                        val h = errorDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: 1
                        updateSingleViewSize(binding.ivMiraStepImageV, targetWidth, w, h)
                        super.onLoadFailed(errorDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.ivMiraStepImageV.setImageDrawable(placeholder)
                    }
                })
        }
    }

    private fun updateSingleViewSize(view: View, targetWidth: Int, originW: Int, originH: Int) {
        if (originW <= 0 || originH <= 0) return
        val ratioHeight = (targetWidth * originH) / originW
        val lp = view.layoutParams
        // 只有尺寸真的变了才重新赋值，避免不必要的 UI 刷新
        if (lp.width != targetWidth || lp.height != ratioHeight) {
            lp.width = targetWidth
            lp.height = ratioHeight
            view.layoutParams = lp
        }
    }


    private fun createDynamicCircle(solidColor: Int, strokeColor: Int, strokeWidthPx: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(solidColor)
            setStroke(strokeWidthPx, strokeColor)
        }
    }
}

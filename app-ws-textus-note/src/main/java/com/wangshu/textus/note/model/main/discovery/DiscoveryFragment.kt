package com.wangshu.note.app.model.main.discovery

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.star.light.common.local.GlideApp
import com.star.starlight.ui.view.commons.RichAdapter
import com.star.starlight.ui.view.commons.RichBuilder
import com.star.starlight.ui.view.entity.RecyclerItemEntity
import com.starlight.dot.framework.utils.changeAlpha
import com.starlight.dot.framework.utils.dip2px
import com.starlight.dot.framework.utils.getScreenSize
import com.starlight.dot.framework.widget.popup.ListPopup
import com.wangshu.note.app.R
import com.wangshu.note.app.BR
import com.wangshu.note.app.adapter.DailyTimeAdapter
import com.wangshu.note.app.common.NoteFragment
import com.wangshu.note.app.databinding.FragmentMainDiscoveryBinding
import com.wangshu.note.app.model.note.WSNoteData
import com.wangshu.note.app.model.plan.PlanData
import com.wangshu.note.app.widget.popup.ScheduleTypePopup

class DiscoveryFragment : NoteFragment<FragmentMainDiscoveryBinding, DiscoveryViewModel>() {
    private lateinit var scheduleTypePopup : ScheduleTypePopup;
    private lateinit var timeAdapter : DailyTimeAdapter;
    private lateinit var healthScienceAdapter : RichAdapter;

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main_discovery;
    }

    override fun getVMClass(): Class<DiscoveryViewModel> {
        return DiscoveryViewModel::class.java;
    }

    override fun initView() {
        super.initView()
        dataBinding.viewModel = viewModel;
        dataBinding.fragment = this;

        scheduleTypePopup = ScheduleTypePopup.Builder(requireContext())
            .onTypeClick {
                viewModel.bindDaily(it.id);
            }
            .size(getScreenSize(requireContext())[0],220.dip2px())
            .builder();
        scheduleTypePopup.setOnDismissListener {
            setAlpha(1f);
        }

        timeAdapter = DailyTimeAdapter(requireContext(),null);
        dataBinding.timeAdapter = timeAdapter;

        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refreshData();
        }

        dataBinding.appBarVip.addOnOffsetChangedListener(onOffsetChangedListener);
        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refreshData();
        }

        healthScienceAdapter = RichBuilder(requireContext())
            .addLayout(R.layout.rv_item_health_science,BR.healthScience)
            .builder();
        dataBinding.healthScienceAdapter = healthScienceAdapter;
        addLineDivider(dataBinding.rvHealthScience,R.drawable.recycler_line_mainbg_5)
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        if(!viewModel.isLogin()){
            toLogin();
            return;
        }
        when(view.id){
            R.id.empty_health_daily_time->{
                //生成健康日常安排
                viewModel.getScheduleTypeList()?.let {
                    setAlpha(0.7f);
                    scheduleTypePopup.notifyTypeList(it);
                    scheduleTypePopup.showViewDown(dataBinding.clMainLayout);
                }
            }
            R.id.cl_note_note->{
                //进入我的日记页面
                //PlanData.publishPlan(requireContext(),false);
                WSNoteData.notesList(requireContext());
            }
            R.id.cl_discovery_top_plan->{
                //进入我的计划列表页面
                PlanData.planList(requireContext());
            }
            R.id.cl_discovery_top_memo->{
                //进入我的备忘录页面
                WSNoteData.memoList(requireContext());
            }
        }
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.userDaily.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
        })

        viewModel.dailyTimeList.observe(this, Observer {
            timeAdapter.dataList = it;
            timeAdapter.notifyDataSetChanged();
        })

        viewModel.healthScienceList.observe(this, Observer {
            healthScienceAdapter.dataList = it as List<RecyclerItemEntity>?;
            healthScienceAdapter.notifyDataSetChanged();
        })
    }

    private val onOffsetChangedListener = object : AppBarLayout.OnOffsetChangedListener{
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            val offSet = Math.abs(verticalOffset);
            val range = appBarLayout?.totalScrollRange;
            if(range != null){
                val alph = Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange();
                dataBinding.toolbarVip.setBackgroundColor(
                    changeAlpha(getColor(R.color.color_main_theme),
                        Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange())
                );
                val viewAlpha = offSet.toFloat()/range.toFloat();
                dataBinding.ivDiscoveryBarIcon.alpha = viewAlpha;
                dataBinding.tvDiscoveryBarTips.alpha = viewAlpha;

                if(alph == 0.0F){
                    dataBinding.toolbarVip.visibility = View.GONE;
                }else{
                    if(dataBinding.toolbarVip.visibility == View.GONE){
                        dataBinding.toolbarVip.visibility = View.VISIBLE;
                    }
                }
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_DiscoveryFragment==>";

        fun newInstance(): DiscoveryFragment {
            val args = Bundle()
            val fragment = DiscoveryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

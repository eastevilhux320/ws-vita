package com.vita.xuantong.demo.model.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.vita.xuantong.demo.commons.KYFragment
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.FragmentMainHomeBinding
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.framework.utils.SLog
import ext.StringExt.parseColor
import ext.ViewExt.createComplexRectDrawable

class HomeFragment : KYFragment<FragmentMainHomeBinding, HomeViewModel>() {
    //private lateinit var homeBanner : HomeBannerAdapter;

    override fun layoutId(): Int {
        return R.layout.fragment_main_home;
    }

    override fun getVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //首页需要状态栏
        val config = ScreenConfig.build(false,getColor(R.color.color_main_theme),false);
        SystemBarManager.instance.applyConfig(requireActivity(), config)

        dataBinding.fragment = this;
        initBanner();
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.homeConfig.observe(this, Observer {
            dataBinding.clMainTitle.setBackgroundColor(Color.parseColor(it.toolBarColor));
            val editBgcolor = it.searchBgcolor?.parseColor() ?: Color.WHITE;
            val searchStrokeColor = it.searchStrokeColor?.parseColor() ?: Color.WHITE;

            dataBinding.editHomeSearch.setBackground(
                requireContext().createComplexRectDrawable(editBgcolor, it.searchStroke, searchStrokeColor, it.searchRadius)
            );
        })

        viewModel.bannerList.observe(this, Observer {
           /* homeBanner.setDatas(it);
            homeBanner.notifyDataSetChanged();*/
        })
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
           /* R.id.cl_find_car->{
                routerContainer(MainConstants.RouterName.HITCH,R.id.f_hitch_riding_list);
            }
            R.id.cl_find_person->{
                routerContainer(MainConstants.RouterName.HITCH,R.id.f_hitch_driving_list);
            }*/
        }
    }

    override fun navigationId(): Int {
        return R.id.nav_f_main_home;
    }

    private fun initBanner(){
        SLog.d(TAG, "initBanner==>")
       /* homeBanner = HomeBannerAdapter();
        dataBinding.bannerGuideContent.setAdapter(homeBanner)
            .addBannerLifecycleObserver(this)
            .setIndicator(CircleIndicator(requireContext()))
            .addPageTransformer(ZoomOutPageTransformer())
            .addPageTransformer(DepthPageTransformer())*/
    }

    companion object{
        private const val TAG = "WSVita_App_Main_HomeFragment==>";

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

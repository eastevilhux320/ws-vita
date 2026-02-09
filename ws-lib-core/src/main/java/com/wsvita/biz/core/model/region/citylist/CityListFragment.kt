package com.wsvita.biz.core.model.region.citylist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.wsvita.biz.core.R
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.adapter.HotCityAdapter
import com.wsvita.biz.core.adapter.RegionCityAdapter
import com.wsvita.biz.core.adapter.RegionDistrictAdapter
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.databinding.FragmentRegionCitylistBinding
import com.wsvita.biz.core.model.region.RegionActivity
import com.wsvita.core.common.adapter.CoreAdapter
import com.wsvita.core.common.adapter.CoreAdapterBuilder
import com.wsvita.core.recycler.GridSpaceItemDecoration
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.FinishParam
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import ext.ViewExt.dip2px
import ext.ViewExt.getScreenPair

class CityListFragment : BizcoreFragment<FragmentRegionCitylistBinding, CityListViewModel>() {
    private lateinit var provinceAdapter : CoreAdapter;
    private lateinit var cityAdapter : RegionCityAdapter;
    private lateinit var distractAdapter : RegionDistrictAdapter;

    private var hotCityAdapter : HotCityAdapter? = null;

    override fun layoutId(): Int {
        return R.layout.fragment_region_citylist;
    }

    override fun getVMClass(): Class<CityListViewModel> {
        return CityListViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.fragment = this;
        setTitleText(getString(R.string.bizcore_title_region_city_list))

        provinceAdapter = CoreAdapterBuilder(requireContext(),R.layout.rv_item_region_province)
            .setVariableId(BR.province)
            .onItemClick { root, position ->
                viewModel.selectProvince(position);
            }
            .build();
        dataBinding.provinceAdapter = provinceAdapter;

        //热门城市
        val cityWidth = (requireContext().getScreenPair().first.toFloat() * 0.68).toInt();
        hotCityAdapter = HotCityAdapter(requireContext(),cityWidth,3);
        dataBinding.hotCityAdapter = hotCityAdapter;
        val hotLayoutManager = GridLayoutManager(requireContext(),3);
        dataBinding.rvBizcoreHotCity.layoutManager = hotLayoutManager;
        val hotDivider = GridSpaceItemDecoration.build()
            .spacing(3.dip2px(),3.dip2px())
            .spanCount(3);
        dataBinding.rvBizcoreHotCity.addItemDecoration(hotDivider);

        //城市
        cityAdapter = RegionCityAdapter(requireContext(),cityWidth,3);
        dataBinding.cityAdapter = cityAdapter;
        val cityLayoutManager = GridLayoutManager(requireContext(),3);
        dataBinding.rvBicoreCity.layoutManager = cityLayoutManager;
        val cityDivider = GridSpaceItemDecoration.build()
            .spacing(3.dip2px(),3.dip2px())
            .spanCount(3);
        dataBinding.rvBicoreCity.addItemDecoration(cityDivider);
        cityAdapter.onCityClick {
            viewModel.selectCity(it);
        }

        //区域
        distractAdapter = RegionDistrictAdapter(requireContext(),cityWidth,3);
        dataBinding.districtAdapter = distractAdapter;
        val districtLayoutManager = GridLayoutManager(requireContext(),3);
        dataBinding.rvBicoreDistrict.layoutManager = districtLayoutManager;
        val districtDivider = GridSpaceItemDecoration.build()
            .spacing(3.dip2px(),3.dip2px())
            .spanCount(3);
        dataBinding.rvBicoreDistrict.addItemDecoration(districtDivider);
        distractAdapter.onDistrictClick {
            //选择区域
            viewModel.selectDistrict(it);
        }

        val showHot = getIntentBoolean(BizConstants.IntentKey.REGION_CITY_HOT_FLAG);
        showHot?.let { viewModel.receiveBoolean(BizConstants.IntentKey.REGION_CITY_HOT_FLAG, it) };

        val showDistrict = getIntentBoolean(BizConstants.IntentKey.REGION_CITY_SHOW_DISTRICT);
        showDistrict?.let {
            viewModel.receiveBoolean(BizConstants.IntentKey.REGION_CITY_SHOW_DISTRICT,it);
        }

        val isFinish = getIntentBoolean(BizConstants.IntentKey.REGION_SELECTED_FINISH);
        viewModel.receiveBoolean(BizConstants.IntentKey.REGION_SELECTED_FINISH,isFinish?:true);
    }

    override fun onConfigChanged(config: BizcoreConfig) {
        super.onConfigChanged(config)
        dataBinding.tvBizcoreProvinceLine.setBackgroundColor(config.mainThemeColor);
        dataBinding.tvBizcoreCityLine.setBackgroundColor(config.mainThemeColor);
        dataBinding.tvBizcoreDistrictLine.setBackgroundColor(config.mainThemeColor)
        dataBinding.tvBizcoreCitySelectedLine.setBackgroundColor(config.mainThemeColor);
        dataBinding.tvBizcoreDistrictSelectedLine.setBackgroundColor(config.mainThemeColor);
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.hotCityList.observe(this, Observer {
            hotCityAdapter?.setList(it);
            hotCityAdapter?.notifyDataSetChanged();
        })

        viewModel.provinceList.observe(this, Observer {
            provinceAdapter.setList(it as MutableList<IRecyclerItem>);
            provinceAdapter.notifyDataSetChanged();
        })

        viewModel.cityList.observe(this, Observer {
            cityAdapter.setList(it);
            cityAdapter.notifyDataSetChanged();
        })

        viewModel.distraceList.observe(this, Observer {
            distractAdapter.setList(it);
            distractAdapter.notifyDataSetChanged();
        })

        viewModel.cityResult.observe(this, Observer {
            if(it.isFinish){
                //携带数据返回
                val ac = getCurrentActivity(RegionActivity::class.java);
                val fParams = FinishParam.create()
                    .putString(BizConstants.IntentKey.RESULT_REGION_PROVINCE,it.province?.toJson())
                    .putString(BizConstants.IntentKey.RESULT_REGION_CITY,it.city?.toJson())
                    .putString(BizConstants.IntentKey.RESULT_REGION_DISTRICT,it.district?.toJson());
                ac?.finishWithResult(fParams);
            }
        })
    }

    override fun navigationId(): Int {
        return R.id.f_bizcore_region_city_list;
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.tv_bizcore_province_line,
            R.id.tv_bizcore_province->{
                viewModel.changeShowType(1);
            }
            R.id.tv_bizcore_city_line,
            R.id.tv_bizcore_city->{
                viewModel.changeShowType(2);
            }
        }
    }

}

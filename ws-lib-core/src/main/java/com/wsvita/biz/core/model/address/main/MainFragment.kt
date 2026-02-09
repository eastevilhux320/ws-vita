package com.wsvita.biz.core.model.address.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.wsvita.biz.core.R
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreActivity
import com.wsvita.biz.core.commons.BizcoreContainerActivity
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.biz.core.databinding.FragmentAddressMainBinding
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.core.common.adapter.CoreAdapter
import com.wsvita.core.common.adapter.CoreAdapterBuilder
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.framework.router.FinishParam
import ext.JsonExt.toJson

class MainFragment : BizcoreFragment<FragmentAddressMainBinding, MainViewModel>() {
    private lateinit var addressAdapter : CoreAdapter;

    override fun navigationId(): Int {
        return R.id.f_bizcore_address_main;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_address_main;
    }

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        addressAdapter = CoreAdapterBuilder(requireContext(),R.layout.rv_item_bizcore_address)
            .setVariableId(BR.address)
            .onBind { binding, item, position ->  }
            .onItemClick { root, position ->
                val addressJson = addressAdapter.getItem(position).toJson();
                val params = FinishParam.create()
                    .put(BizConstants.IntentKey.ADDRESS,addressJson);

                val ac = getCurrentActivity(BizcoreContainerActivity::class.java);
                ac?.finishWithResult(params);
            }
            .build();
        dataBinding.addressAdapter = addressAdapter;

        setMenuIcon(R.drawable.ic_bizcore_add_white);
        showMenu();
        setTitleText(getString(R.string.bizcore_title_address_list))
    }

    override fun onMenuClick(view: View) {
        super.onMenuClick(view)
        navigate(R.id.f_bizcore_address_enrollment);
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.addressList.observe(this, Observer {
            addressAdapter.setList(it as MutableList<IRecyclerItem>)
            addressAdapter.notifyDataSetChanged();
        })
    }

}

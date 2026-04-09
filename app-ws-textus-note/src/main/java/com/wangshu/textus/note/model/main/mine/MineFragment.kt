package com.wangshu.note.app.model.main.mine

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.star.starlight.ui.view.commons.RichAdapter
import com.star.starlight.ui.view.commons.RichBuilder
import com.star.starlight.ui.view.entity.RecyclerItemEntity
import com.starlight.dot.framework.utils.SLog
import com.starlight.dot.framework.utils.changeAlpha
import com.wangshu.account.configure.AccountConfigure
import com.wangshu.note.app.R
import com.wangshu.note.app.BR
import com.wangshu.note.app.common.NoteFragment
import com.wangshu.note.app.databinding.FragmentMainMineBinding
import com.wangshu.note.app.entity.main.MineFunctionEntity
import com.wangshu.note.app.model.finance.FinanceData
import com.wangshu.note.app.model.trade.TradeData
import com.wangshu.note.app.presenter.common.impl.MineFunctionPresenterImpl

class MineFragment : NoteFragment<FragmentMainMineBinding, MineViewModel>() {
    private lateinit var functionAdapter : RichAdapter;

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main_mine;
    }

    override fun getVMClass(): Class<MineViewModel> {
        return MineViewModel::class.java;
    }

    override fun initView() {
        super.initView()
        dataBinding.viewModel = viewModel;
        dataBinding.fragment = this;
        dataBinding.appBarVip.addOnOffsetChangedListener(onOffsetChangedListener);

        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refresh();
        }

        functionAdapter = RichBuilder(requireContext())
            .addLayout(R.layout.rv_item_mine_function,BR.functionEntity)
            .addVariable(BR.functionPresenter,functionPresenter)
            .builder();
        dataBinding.functionAdapter = functionAdapter;

        addLineDivider(dataBinding.rvMineFunction);
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.accountCenter.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
        })

        viewModel.functionList.observe(this, Observer {
            functionAdapter.dataList = it as List<RecyclerItemEntity>?;
            functionAdapter.notifyDataSetChanged();
        })
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        if(!viewModel.isLogin()){
            SLog.d(TAG,"no login,to login")
            toLogin();
            return;
        }
        when(view.id){
            R.id.iv_mine_account_arrow,
            R.id.tv_account_userno,
            R.id.tv_account_nickname,
            R.id.iv_account_header,
            R.id.iv_mine_account_icon->{
                AccountConfigure.instance.userCenter(requireContext(),false);
            }
            R.id.tv_mine_budget_list->{
                //设置预算

            }
            R.id.iv_mine_setting->{
                AccountConfigure.instance.toSetting(requireContext(),false);
            }
        }
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
                dataBinding.ivMineMsg.alpha = viewAlpha;
                dataBinding.ivMineSetting.alpha = viewAlpha;
                dataBinding.tvMineAccountNickname.alpha = viewAlpha;

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

    private val functionPresenter = object : MineFunctionPresenterImpl() {

        override fun onItemSelectListener(entity: MineFunctionEntity?) {
            super.onItemSelectListener(entity)
            if(!viewModel.isLogin()){
                toLogin();
                return;
            }
            when(entity?.id){
                1L->{
                    //提现记录
                    TradeData.withdrawalList(requireContext(),false);
                }
                2L->{
                    //进入收益记录页面
                    FinanceData.toMain(requireContext(),false);
                }
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_MineFragment==>";

        fun newInstance(): MineFragment {
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

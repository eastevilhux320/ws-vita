package com.wsvita.core.datatime

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.wsvita.core.R
import com.wsvita.core.adapter.DateTimeAdapter
import com.wsvita.core.adapter.DateTimeWheelAdapter
import com.wsvita.core.common.AppActivity
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.configure.DateTimeConfig
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.databinding.ActivitySdkcoreDatatimeBinding
import com.wsvita.framework.utils.SLog

class DateTimeActivity : AppActivity<ActivitySdkcoreDatatimeBinding, DateTimeViewModel>() {
    private lateinit var dateTimeAdapter : DateTimeAdapter;
    private lateinit var hourAdapter : DateTimeWheelAdapter;
    private lateinit var minuteAdapter : DateTimeWheelAdapter;
    private lateinit var secondAdapter : DateTimeWheelAdapter;

    override fun layoutId(): Int {
        return R.layout.activity_sdkcore_datatime;
    }

    override fun getVMClass(): Class<DateTimeViewModel> {
        return DateTimeViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.viewModel = viewModel;
        dataBinding.activity = this;

        val themeColor = CoreConfigure.instance.getConfig()?.mainThemeColor?: Color.BLACK;
        dataBinding.titleCoreDatetime.setBackgroundColor(themeColor);
        dataBinding.titleCoreDatetime.setTitleColor(Color.WHITE);

        val layoutManager = GridLayoutManager(this,7);
        dataBinding.rvDatetime.layoutManager = layoutManager;

        dateTimeAdapter = DateTimeAdapter(this);
        dataBinding.dateTimeAdapter = dateTimeAdapter;

        dateTimeAdapter.onDateTime { dateTime, position ->
            viewModel.selectDay(position);
        }

        hourAdapter = DateTimeWheelAdapter();
        minuteAdapter = DateTimeWheelAdapter();
        secondAdapter = DateTimeWheelAdapter();

        dataBinding.whellHour.setItemsVisibleCount(5);
        dataBinding.whellMinute.setItemsVisibleCount(5);
        dataBinding.whellSecond.setItemsVisibleCount(5);

        dataBinding.whellHour.setOnItemSelectedListener {
            viewModel.selectHour(it);
        }
        dataBinding.whellMinute.setOnItemSelectedListener {
            viewModel.selectMinute(it);
        }
        dataBinding.whellSecond.setOnItemSelectedListener {
            viewModel.selectSecond(it);
        }
    }

    override fun initScreenConfig(): ScreenConfig {
        val themeColor = CoreConfigure.instance.getConfig()?.mainThemeColor?: Color.BLACK;
        val screenConfig = ScreenConfig.build(false,themeColor,false);
        return screenConfig;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.dateTimeList.observe(this, Observer {
            dateTimeAdapter.setList(it);
            dateTimeAdapter.notifyDataSetChanged();
        })

        viewModel.hourList.observe(this, Observer {
            hourAdapter.setDataList(it);
            dataBinding.whellHour.adapter = hourAdapter;
        })

        viewModel.minuteList.observe(this, Observer {
            minuteAdapter.setDataList(it);
            dataBinding.whellMinute.adapter = minuteAdapter;
        })

        viewModel.secondList.observe(this, Observer {
            secondAdapter.setDataList(it);
            dataBinding.whellSecond.adapter = secondAdapter;
        })

        viewModel.dateTimeConfig.observe(this, Observer {
            onDateTimeChanged(it);
        })
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.iv_month_last->{
                viewModel.toPrevMonth();
            }
            R.id.iv_month_next->{
                viewModel.toNextMonth();
            }
        }
    }

    private fun onDateTimeChanged(config : DateTimeConfig){
        SLog.d(TAG,"onDateTimeChanged");

    }

    companion object {
        private const val TAG = "WSVita_DateTimeActivity==>"
    }
}

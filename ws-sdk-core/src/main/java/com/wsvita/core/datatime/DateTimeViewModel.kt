package com.wsvita.core.datatime

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.core.R
import com.wsvita.core.common.AppViewModel
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.configure.DateTimeConfig
import com.wsvita.core.entity.domain.DateTimeEntity
import com.wsvita.core.entity.domain.WSTimeEntity
import com.wsvita.framework.utils.SLog
import ext.StringExt.isNotInvalid
import ext.TimeExt.format
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DateTimeViewModel(application: Application) : AppViewModel(application) {
    /**
     * 当前选中的日期天数，默认为0，在[refreshCalendar]方法中进行赋值
     */
    private var daySeletedIndex : Int = -1;

    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var titleFormat: SimpleDateFormat
    private lateinit var monthFormat : SimpleDateFormat
    private val calendarAnchor = Calendar.getInstance()
    private val mReuseDate = Date()
    /**
     * 当前选择的时间
     */
    private val _dateTime = MutableLiveData<Long>();
    val dateTime : LiveData<Long> = _dateTime

    /**
     * 当前时间
     */
    private val _currentTime = MutableLiveData<Long>();
    val currentTime : LiveData<Long>
        get() = _currentTime;

    /**
     * 当前时间展示格式
     */
    val currentTimeText = MutableLiveData<String>();
    val currentMonthText = MutableLiveData<String>();

    private val _dateTimeList = MutableLiveData<MutableList<DateTimeEntity>>()
    val dateTimeList: LiveData<MutableList<DateTimeEntity>> get() = _dateTimeList

    //时分秒的列表
    val hourList = MutableLiveData<MutableList<WSTimeEntity>>();
    val minuteList = MutableLiveData<MutableList<WSTimeEntity>>();
    val secondList = MutableLiveData<MutableList<WSTimeEntity>>();
    /**
     * 时间日期配置管理
     */
    val dateTimeConfig = MutableLiveData<DateTimeConfig>();

    override fun initModel() {
        super.initModel()

        _dateTime.value = systemTime();
        initDateTime();

        startTimeTicker()
    }

    private fun startTimeTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _currentTime.value = now;
                updateCurrentTimeText();
                val nextTick = 1000L - (now % 1000L)
                delay(nextTick)
            }
        }
    }

    private fun initDateTime(){
        var config = getDateTimeConfigInternal();
        dateTimeConfig.value = config;
        // 适配国际化 Pattern
        val timePattern = getApplication<Application>().getString(R.string.ws_time_format_default)
        val titlePattern = getApplication<Application>().getString(R.string.ws_date_format_default)
        val monthPattern = getApplication<Application>().getString(R.string.sdkcore_month_format);

        timeFormat = SimpleDateFormat(timePattern, Locale.getDefault())
        titleFormat = SimpleDateFormat(titlePattern, Locale.getDefault())
        monthFormat = SimpleDateFormat(monthPattern, Locale.getDefault())

        refreshCalendar(config)
        initTimeList();
        updateCurrentTimeText();
    }

    /**
     * 上一月切换
     * Description: 切换至上一个月，并根据配置中的 minDate 和 canSelectBefore 校验边界。
     *
     * create by Eastevil at 2025/12/30 11:45
     */
    fun toPrevMonth() {
        val config = getDateTimeConfigInternal()
        val now = System.currentTimeMillis()

        // 1. 克隆一个临时锚点用于预计算，避免直接修改当前锚点
        val tempCal = calendarAnchor.clone() as Calendar
        tempCal.add(Calendar.MONTH, -1)

        // 获取目标月份的最后一天（例如：如果要跳回1月，判断1月31日是否早于最小限制）
        val lastDayOfPrevMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        tempCal.set(Calendar.DAY_OF_MONTH, lastDayOfPrevMonth)
        val targetTime = tempCal.timeInMillis

        // 2. 校验 canSelectBefore：如果目标月最后一天都比今天早，且不允许选以前，则拦截
        if (!config.canSelectBefore) {
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            if (targetTime < todayStart) {
                // 目标月份已属于过去，禁止跳转
                return
            }
        }

        // 3. 校验 minDate 绝对边界
        if (config.minDate != -1L && targetTime < config.minDate) {
            // 目标月份超出了设定的最小日期限制
            return
        }

        // 执行跳转
        calendarAnchor.add(Calendar.MONTH, -1)
        refreshCalendar(config)
    }

    /**
     * 下一月切换
     * Description: 切换至下一个月，并根据配置中的 maxDate 和 canSelectAfter 校验边界。
     *
     * create by Eastevil at 2025/12/30 11:45
     */
    fun toNextMonth() {
        val config = getDateTimeConfigInternal()
        val now = System.currentTimeMillis()

        val tempCal = calendarAnchor.clone() as Calendar
        tempCal.add(Calendar.MONTH, 1)

        // 获取目标月份的第一天，用于判断是否超过上限
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        val targetTime = tempCal.timeInMillis

        // 1. 校验 canSelectAfter：如果下月第一天就已经超过今天，且不允许选未来，则拦截
        if (!config.canSelectAfter && targetTime > now) {
            return
        }

        // 2. 校验 maxDate 绝对边界
        if (config.maxDate != -1L && targetTime > config.maxDate) {
            return
        }
        calendarAnchor.add(Calendar.MONTH, 1)
        refreshCalendar(config)
    }

    fun selectHour(position: Int) {
        hourList.value?.let {
            val hour = it[position].time
            val config = getDateTimeConfigInternal()
            // 根据 config.hourMode 决定操作哪个字段
            // 假设 24 表示 24小时制，12 表示 12小时制
            val field = if (config.hourMode == 24) Calendar.HOUR_OF_DAY else Calendar.HOUR
            // 记录旧值用于回滚
            val oldHour = calendarAnchor.get(field)
            // 尝试设置新值
            calendarAnchor.set(field, hour)
            if (isTimeValid(calendarAnchor.timeInMillis)) {
                updateDateTime()
            } else {
                // 校验失败，回滚旧值
                calendarAnchor.set(field, oldHour)
                SLog.d(TAG, "Hour selection not allowed, rolled back.")
            }
        }
    }

    fun selectMinute(position: Int) {
        minuteList.value?.let {
            val minute = it[position].time

            // 记录旧值
            val oldMinute = calendarAnchor.get(Calendar.MINUTE)

            // 尝试设置
            calendarAnchor.set(Calendar.MINUTE, minute)

            if (isTimeValid(calendarAnchor.timeInMillis)) {
                updateDateTime()
            } else {
                calendarAnchor.set(Calendar.MINUTE, oldMinute)
                SLog.d(TAG, "Minute selection not allowed, rolled back.")
            }
        }
    }

    fun selectSecond(position: Int) {
        secondList.value?.let {
            val second = it[position].time
            // 记录旧值
            val oldSecond = calendarAnchor.get(Calendar.SECOND)
            // 尝试设置
            calendarAnchor.set(Calendar.SECOND, second)

            if (isTimeValid(calendarAnchor.timeInMillis)) {
                updateDateTime()
            } else {
                calendarAnchor.set(Calendar.SECOND, oldSecond)
                SLog.d(TAG, "Second selection not allowed, rolled back.")
            }
        }
    }

    fun selectDay(position : Int){
        if(position == daySeletedIndex){
            //两次点击相同，直接返回
            return;
        }
        dateTimeList.value?.let {list->
            if(daySeletedIndex >= 0 && daySeletedIndex < list.size){
                //修改上一次的选中状态
                list.get(daySeletedIndex).itemSelect = false;
            }
            if(position >= 0 && position < list.size){
                //修改本次点击
                list.get(position).itemSelect = true;
            }
            //更新数据
            _dateTimeList.value = list;
            //重新覆盖新值
            daySeletedIndex = position;
        }
    }


    /**
     * 精准构建日历矩阵，并根据 DateTimeConfig 约束日期可用性
     *
     * create by Eastevil at 2025/12/30 11:30
     * @author Eastevil
     */
    private fun refreshCalendar(config: DateTimeConfig) {
        val list = mutableListOf<DateTimeEntity>()
        val nowMillis = System.currentTimeMillis()

        // 获取当天的零点时间戳，用于精准对比“今天”
        val todayStart = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // --- A. 星期表头 ---
        val weekDays = listOf(
            getString(com.wsvita.ui.R.string.week_1),
            getString(com.wsvita.ui.R.string.week_2),
            getString(com.wsvita.ui.R.string.week_3),
            getString(com.wsvita.ui.R.string.week_4),
            getString(com.wsvita.ui.R.string.week_5),
            getString(com.wsvita.ui.R.string.week_6),
            getString(com.wsvita.ui.R.string.week_7)
        )
        weekDays.forEach {
            list.add(DateTimeEntity().apply {
                timeType = 1
                isUsed = false
                weekDayText = it
                itemSelect = false
            })
        }

        // --- B. 核心日期逻辑 ---
        val cal = (calendarAnchor.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val viewMonth = cal.get(Calendar.MONTH)

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val headOffset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val totalBeforeTail = headOffset + daysInMonth
        val tailOffset = if (totalBeforeTail % 7 == 0) 0 else 7 - (totalBeforeTail % 7)

        cal.add(Calendar.DAY_OF_MONTH, -headOffset)
        val totalCount = headOffset + daysInMonth + tailOffset

        for (i in 0 until totalCount) {
            val itemTimestamp = cal.timeInMillis
            val d = DateTimeEntity()
            d.timestamp = itemTimestamp
            d.timeType = 2

            // 1. 基础判断：必须是当前月份视图
            val isCurrentMonth = cal.get(Calendar.MONTH) == viewMonth

            // 2. 配置判断：是否在 Min/Max 绝对区间内
            var isInRange = true
            if (config.minDate != -1L && itemTimestamp < config.minDate) {
                isInRange = false
            }
            if (config.maxDate != -1L && itemTimestamp > config.maxDate) {
                isInRange = false
            }

            // 3. 配置判断：能否选今天之前/之后
            var isRelativeValid = true
            // 注意：这里用 itemTimestamp 与 todayStart 对比，确保“今天”始终可选
            if (!config.canSelectBefore && itemTimestamp < todayStart) {
                isRelativeValid = false
            }
            if (!config.canSelectAfter && itemTimestamp > todayStart) {
                isRelativeValid = false
            }
            // 综合判定：只有满足所有业务约束的本月日期，isUsed 才为 true
            d.isUsed = isCurrentMonth && isInRange && isRelativeValid

            // 如果是今天，且符合 config 的范围，通常默认选中或高亮
            if (d.isToday && d.isUsed) {
                SLog.d(TAG,"isToday and used,postion:${i},day:${d.dayText}");
                daySeletedIndex = i;
                d.itemSelect = true;
            }else{
                d.itemSelect = false;
            }
            list.add(d)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        // 更新 UI 状态流
        updateDateTime();
        _dateTimeList.value = list
    }

    /**
     * 内部获取配置方法，确保逻辑复用
     */
    private fun getDateTimeConfigInternal(): DateTimeConfig {
        var config = dateTimeConfig.value ?: CoreConfigure.instance.getDateTimeConfig()
        if (config == null) {
            val appId = CoreConfigure.instance.appId()
            config = DateTimeConfig.Builder(appId).builder()
            dateTimeConfig.value = config!!;
        }
        return config
    }

    private fun initTimeList(){
        val hList = mutableListOf<WSTimeEntity>();
        for (i in 0..23) {
            val t = WSTimeEntity();
            t.type = 1;
            t.time = i;
            hList.add(t);
        }
        hourList.value = hList;

        val mList = mutableListOf<WSTimeEntity>();
        for (i in 0..59) {
            val t = WSTimeEntity();
            t.type = 2;
            t.time = i;
            mList.add(t);
        }
        minuteList.value = mList;

        val sList = mutableListOf<WSTimeEntity>();
        for (i in 0..59) {
            val t = WSTimeEntity();
            t.type = 3;
            t.time = i;
            sList.add(t);
        }
        secondList.value = sList;
    }

    private fun updateCurrentTimeText() {
        val time = _currentTime.value ?: System.currentTimeMillis()
        mReuseDate.time = time // 复用对象，不再 new Date(time)
        var newTimeText : String? = null;
        var newMonthText : String? = null;
        try {
            newTimeText = timeFormat.format(mReuseDate)
            newMonthText = monthFormat.format(mReuseDate);
        } catch (e: Exception) {
            ""
        }
        currentTimeText.value = newTimeText?:"";
        //判断是否需要修改月份,只存在于23:59分打开，经过1分钟后才可能会需要修改。
        val lastMonthText = currentMonthText.value;
        if(newMonthText.isNotInvalid() && !newMonthText.equals(lastMonthText)){
            SLog.d(TAG,"update current month")
            currentMonthText.value = newMonthText?:"";
        }
    }

    private fun updateDateTime(){
        _dateTime.value = calendarAnchor.timeInMillis;
    }

    /**
     * 校验目标时间戳是否符合配置约束
     */
    private fun isTimeValid(targetMillis: Long): Boolean {
        val config = getDateTimeConfigInternal()
        val now = System.currentTimeMillis()

        // 1. 绝对区间校验 (Min/Max)
        if (config.minDate != -1L && targetMillis < config.minDate) {
            return false
        }
        if (config.maxDate != -1L && targetMillis > config.maxDate) {
            return false
        }

        // 2. 相对区间校验 (今天之前/之后)
        // 注意：如果是对“时分秒”的精准拦截，这里直接对比 now 的毫秒值
        if (!config.canSelectBefore && targetMillis < now) {
            return false
        }
        if (!config.canSelectAfter && targetMillis > now) {
            return false
        }

        return true
    }

    companion object {
        private const val TAG = "WSVita_DateTimeViewModel==>"
    }
}

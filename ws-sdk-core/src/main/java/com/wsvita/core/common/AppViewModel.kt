package com.wsvita.core.common

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.core.R
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.core.entity.RequestEvent
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.commons.SDKViewModel
import com.wsvita.framework.entity.AuthError
import com.wsvita.framework.entity.VError
import com.wsvita.framework.utils.SLog
import com.wsvita.network.entity.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ### App 级业务核心 ViewModel 基类
 *
 * **框架定位：**
 * 本类是所有业务组件 ViewModel 的父类，封装了基于 [RequestEvent] 的全生命周期状态分发机制。
 * 核心设计目标是实现“配置驱动交互”，支持表达式级（Suspend）请求调用。
 *
 * **核心特性：**
 * 1. **表达式支持**：支持 `data = request(...)` 风格调用，简化业务层赋值逻辑。
 * 2. **生命周期闭环**：自动在协程内分发 Begin、requestEnd、Error、End 四阶段信号。
 * 3. **UI 竞态修复**：强制在返回结果前发出 [RequestEvent.requestEnd]，确保 UI 交互态优先恢复。
 *
 * @author Eastevil
 * @date 2025/12/30
 */
abstract class AppViewModel(application: Application) : SDKViewModel(application) {

    /**
     * 请求事件流（一次性事件）
     */
    private val _requestEvent = MutableSharedFlow<RequestEvent>()
    val requestEvent: SharedFlow<RequestEvent> = _requestEvent.asSharedFlow()

    private val _authError = MutableLiveData<AuthError?>();
    val authError : LiveData<AuthError?>
        get() = _authError;

    /**
     * 当前业务片段的物理标识 ID。
     *
     * 当 ViewModel 的生命周期绑定在 Fragment 时，该 ID 对应 Fragment 的资源 ID。
     * 默认值设定为 -9527 以区分未初始化状态。
     */
    protected var mFragmentId : Int = -9527;

    /**
     * ### 消耗特定的鉴权错误信号
     *
     * **设计意图：**
     * 在多并发请求环境下，为了确保“请求”与“处理”的精确匹配，通过 [requestId] 来指定向 VM 确认
     * 某次特定的鉴权异常已被 View 层成功截获并处理。
     *
     * **工作流程：**
     * 1. [requestInternal] 执行失败并判定为鉴权异常，生成包含唯一 [requestId] 的 [AuthError]。
     * 2. View 层通过监听 [authError] LiveData 获取到该实体。
     * 3. View 层执行跳转登录或弹窗后，调用此方法并回传 [requestId]。
     * 4. VM 校验当前持有的错误 ID，匹配成功则重置状态，防止粘性事件导致重复处理。
     *
     * @param requestId 触发鉴权异常的原始请求 ID。该 ID 应从 [AuthError] 实体中获取。
     *
     * @author Administrator
     * @date 2026/1/8
     */
    fun consumeAuthError(requestId : Long){
        val currentError = _authError.value
        // 只有当传入的 ID 与当前持有的错误 ID 一致时，才执行清理
        if (currentError != null && currentError.resultId == requestId) {
            SLog.d(TAG, "consumeAuthError -> error consumed, requestId: $requestId")
            _authError.postValue(null)
        } else {
            // ID 不匹配或状态已为空，可能已被其他逻辑消耗或属于过时信号
            SLog.w(TAG, "consumeAuthError -> requestId not match,current: ${currentError?.resultId}, input: $requestId");
        }
    }

    /**
     * 绑定当前 Fragment 的物理标识 ID。
     *
     * 该方法是实现数据隔离的关键入口。在 Fragment 初始化阶段调用，将当前业务单元的 [id]
     * 注入 ViewModel。其核心作用是：
     * 1. 确定数据存取索引：为 [com.wsvita.core.common.AppContainerActivity.getFragmentCacheData] 提供物理查找范围。
     * 2. 预设清理目标：确保在 [onDestroy] 触发时，能够精准定位并回收 [com.wsvita.core.local.manager.ContainerManager] 中关联的缓存。
     *
     * @param fragmentId 对应 Fragment 的资源标识 ID。
     */
    fun setFragmentId(fragmentId : Int){
        this.mFragmentId = fragmentId;
    }

    /**
     * 核心网络请求执行引擎（内部私有实现）。
     * <p>
     * 该方法封装了请求的全生命周期管理，包括：发送 {@link RequestEvent.Begin}、显示/隐藏 Loading、
     * 执行业务 Block、异常捕获、以及最终发送 {@link RequestEvent.End}。
     * </p>
     * <b>注意事项：</b>
     * <ul>
     * <li>本方法是挂起函数，必须在协程作用域内调用。</li>
     * <li>通过 {@code result.isSuccess} 自动分发成功数据或错误流程。</li>
     * <li>在 {@code finally} 块中确保发出 End 信号，以便 UI 层重置状态。</li>
     * </ul>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param config 请求配置对象，包含请求码、UI 展示类型及自定义错误实体。
     * @param showLoading 是否控制全局 Loading 遮罩的显示与隐藏。
     * @param block 具体的网络请求执行体，需返回 {@link Result} 包装类。
     * @return 请求成功返回具体业务对象 T，失败或异常返回 null。
     */
    private suspend fun <T> requestInternal(
        config: ModelRequestConfig,
        showLoading: Boolean,
        block: suspend () -> Result<T>
    ): T? {
        try {
            // 1. Begin
            if (showLoading){
                showLoading(null)
            }
            requestBegin(config);

            // 2. Execute
            val result = block()

            // 3. UI 交互态优先恢复
            if (showLoading){
                hideLoading()
            }
            requestEnd(config,result.code,result.msg);

            // 校验请求是否成功
            if (result.isSuccess) {
                val data = result.data
                // 情况 A：返回成功，但数据体为空
                if (data == null) {
                    SLog.d(TAG, "Business success but data is null.")
                    onEmptyData(config, result)
                    // 根据配置判断：空数据是否作为错误处理
                    if (isDataEmptyAsError(config.requestCode)) {
                        val isErrorIntercepted = onRequestError(config, result)
                        if (isErrorIntercepted) {
                            SLog.d(TAG, "Empty data error intercepted by onRequestError.")
                            return null // 拦截成功，直接返回，不再分发 UI 事件
                        }

                        // 2. 未被拦截，组装标准的空数据错误实体
                        val errorEntity = config.error().apply {
                            code = ModelConstants.ModelCode.SDK_CORE_REQEUST_EMPTY_ERROR_CODE
                            msg = "empty data"
                        }
                        // 分发业务错误事件
                        handleBusinessError(config, errorEntity)
                    } else {
                        // 仅作为普通空数据状态处理
                        handleBusinessEmpty(config, result.code, result.msg)
                    }
                    return null
                }
                // 情况 B：返回成功，且数据有效
                // 触发成功前置拦截器，询问子类是否拦截后续自动化分发
                val isIntercepted = onRequestSuccess(config, result.code, result.msg)
                if (isIntercepted) {
                    SLog.d(TAG, "onRequestSuccess -> intercepted by subclass, skipping default dispatch.")
                    // 如果被拦截，通常意味着子类已在 onRequestSuccess 中处理了 UI 逻辑（如 success()）
                    return data
                }
                // 默认行为：如果子类没有拦截，且你希望在基类中有一个默认的成功分发，可以在此添加
                // success() // 根据你的架构设计决定是否开启
                return data
            } else {
                // 情况 C：请求失败 (Server Error / Network Error)
                // 组装错误实体
                val errorEntity = config.error().apply {
                    code = result.code
                    msg = result.msg ?: msg
                }

                // 1. 优先处理鉴权类错误（如 Token 失效）
                if (AuthError.isAuthError(result.code)) {
                    SLog.e(TAG, "Auth error detected, code: ${result.code}")
                    handleAuthError(config, errorEntity)
                    return null
                }

                // 2. 深度拦截检查：若子类 onRequestError 返回 true，则拦截基类的通用错误反馈（如弹窗/Toast）
                val isErrorIntercepted = onRequestError(config, result)
                if (!isErrorIntercepted) {
                    // 执行标准的业务错误分发流程
                    handleBusinessError(config, errorEntity)
                }

                return null
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            handleException(config, e)
            return null
        } finally {
            // 5. End（兜底）
            emitRequestEvent(RequestEvent.End(config))
        }
    }

    /**
     * 业务请求启动器（配置对象驱动版）。
     * <p>
     * <b>调用方式：</b> 适用于已预先构建好 {@link ModelRequestConfig} 的场景。
     * 该方法会自动在 {@code viewModelScope} 中启动协程。
     * </p>
     * <b>警告：</b> 由于内部使用异步协程启动，方法会立即返回 null，
     * 建议通过 {@code success} 回调（如果存在）或 LiveData 驱动 UI。
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param config 完整的请求参数配置实体。
     * @param showLoading 是否展示全屏阻塞式加载框。
     * @param block 业务请求逻辑（挂起函数）。
     * @return 总是返回 null（异步执行），实际数据请在业务回调中处理。
     */
    private suspend fun <T> request(
        config: ModelRequestConfig,
        showLoading: Boolean = true,
        block: suspend () -> Result<T>
    ): T? = requestInternal(config, showLoading, block)

    /**
     * 业务请求启动器（全参数便捷版）。
     * <p>
     * <b>调用示例：</b>
     * <pre>
     * request(1001, "提交订单", "确定", "取消", ModelRequestConfig.SHOW_TYPE_DIALOG) {
     * repository.submitOrder(...)
     * }
     * </pre>
     * </p>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param requestCode 请求唯一标识码，子组件 View 层根据此 ID 区分业务逻辑。
     * @param requestName 业务名称，通常作为 Dialog 的标题或日志标识。
     * @param submitText 确定按钮文案，若为空则取框架默认配置。
     * @param cancelText 取消按钮文案，若为空则取框架默认配置。
     * @param showType 交互反馈类型（Toast/Dialog/View/None），参见 {@link ModelRequestConfig}。
     * @param showLoading 是否显示 Loading 进度。
     * @param block 网络请求实现体。
     * @return 异步执行，固定返回 null。
     */
    protected suspend fun <T> request(
        requestCode: Int,
        requestName : String? = null,
        submitText : String? = null,
        cancelText : String? = null,
        showType: Int = ModelRequestConfig.SHOW_TYPE_TOAST,
        showLoading: Boolean = true,
        block: suspend () -> Result<T>
    ): T? {
        val config = ModelRequestConfig.Builder()
            .setRequestCode(requestCode)
            .setRequestTitle(requestName)
            .setSubmitText(submitText)
            .setCancelText(cancelText)
            .setShowType(showType)
            .builder()
        return request(config,showLoading,block);
    }

    /**
     * 业务请求启动器（请求码与展示类型版）。
     * <p>
     * 适用于不需要自定义按钮文案，但需要指定 {@code showType} 的标准交互场景。
     * </p>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param requestCode 请求业务码。
     * @param showType UI 反馈模式（Toast/Dialog 等）。
     * @param showLoading 是否展示 Loading。
     * @param block 网络请求逻辑。
     * @return 异步执行，返回 null。
     */
    protected suspend fun <T> request(requestCode : Int, showType: Int = ModelRequestConfig.SHOW_TYPE_TOAST,
                              showLoading: Boolean = true,
                              block: suspend () -> Result<T>): T?{
        val config = ModelRequestConfig.Builder()
            .setRequestCode(requestCode)
            .setShowType(showType)
            .builder()
        return request(config,showLoading,block);
    }

    /**
     * 业务请求启动器（最简请求码版）。
     * <p>
     * 默认使用 {@link ModelRequestConfig#SHOW_TYPE_TOAST} 模式。
     * </p>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param requestCode 请求业务码。
     * @param showLoading 是否开启加载遮罩。
     * @param block 请求执行体。
     * @return 异步执行，返回 null。
     */
    protected suspend fun <T> request(requestCode : Int,
                              showLoading: Boolean = true,
                              block: suspend () -> Result<T>): T?{
        val config = ModelRequestConfig.Builder()
            .setRequestCode(requestCode)
            .setShowType(ModelRequestConfig.SHOW_TYPE_TOAST)
            .builder()
        return request(config,showLoading,block);
    }

    /**
     * 业务请求启动器（匿名请求版）。
     * <p>
     * 自动使用系统默认请求码 {@link ModelConstants.ModelCode#SDK_CORE_REQUEST_DEFAULT}。
     * 适用于不关心请求来源、仅执行简单操作的场景。
     * </p>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param showLoading 是否在请求期间阻塞 UI。
     * @param block 请求执行体。
     * @return 异步执行，返回 null。
     */
    protected suspend fun <T> request(showLoading: Boolean = true,
                              block: suspend () -> Result<T>): T?{
        val config = ModelRequestConfig.Builder()
            .setRequestCode(ModelConstants.ModelCode.SDK_CORE_REQUEST_DEFAULT)
            .setShowType(ModelRequestConfig.SHOW_TYPE_TOAST)
            .builder()
        return request(config,showLoading,block);
    }

    /**
     * 极简业务请求启动器。
     * <p>
     * <b>默认策略：</b>
     * 开启 Loading、错误以 Toast 形式展示、使用默认请求码。
     * 适合 80% 的常规数据读取场景。
     * </p>
     *
     * create by Eastevil at 2025/12/30 16:26
     * @author Eastevil
     * @param block 请求执行体。
     * @return 异步执行，返回 null。
     */
    protected suspend fun <T> request(block: suspend () -> Result<T>): T?{
        val config = ModelRequestConfig.Builder()
            .setRequestCode(ModelConstants.ModelCode.SDK_CORE_REQUEST_DEFAULT)
            .setShowType(ModelRequestConfig.SHOW_TYPE_TOAST)
            .builder()
        return request(config,true,block);
    }

    // --------------------------------------------------
    // 错误 & 钩子（子类可按需重写）
    // --------------------------------------------------

    /**
     * ### 请求启动预处理器
     *
     * **核心职责：**
     * 在异步任务正式进入执行栈并下发 [RequestEvent.Begin] 信号**之前**触发。
     * 为 ViewModel 子类提供一个同步/异步的初始化切入点。
     *
     * **典型应用场景：**
     * 1. **状态校验**：在请求发起前，检查特定业务参数或本地缓存状态。
     * 2. **UI 预设变更**：根据业务逻辑动态修改 [config] 中的属性（如：根据用户权限动态调整 `requestTitle`）。
     * 3. **埋点/日志**：统一记录业务请求的起始时间戳或上报业务启动埋点。
     *
     * **执行序特性：**
     * - **主线程调度**：该方法默认在主线程执行，允许直接修改 UI 相关的 Config 配置。
     * - **阻塞性**：作为一个 `suspend` 方法，若内部执行了耗时操作，将顺延后续请求的执行时间。
     * - **优先权**：执行完此方法后，框架才会通过 `emitRequestEvent` 发送 `Begin` 事件至 UI 层。
     *
     * **注意：**
     * - 默认实现为空。子类重写时通常不需要调用 `super.requestBegin`。
     * - 如果需要在请求前执行某些**会导致请求熔断**的逻辑（如：未实名认证禁止请求），建议在此处抛出业务异常或返回特定状态。
     *
     * @param config [ModelRequestConfig] - 即将发起的请求上下文配置，支持在内部进行二次修改。
     */
    protected open suspend fun requestBegin(config : ModelRequestConfig){
        emitRequestEvent(RequestEvent.Begin(config))
    }

    /**
     * ### 请求逻辑结束处理器
     *
     * **核心职责：**
     * 在业务逻辑执行完毕、数据封装完成，但**尚未**将结果回调给 UI 调用方之前触发。
     * 是 [RequestEvent.requestEnd] 事件的内部触发点。
     *
     * **典型应用场景：**
     * 1. **UI 状态提前恢复**：在 View 层收到具体业务数据并开始渲染前，先行隐藏 Loading 或恢复按钮点击态，提升交互流畅度。
     * 2. **局部重置**：根据 [config.requestCode] 重置特定组件的刷新状态（如停止 SwipeRefreshLayout 的动画）。
     * 3. **后置拦截**：在数据交付前，根据请求结果对 [config] 进行最后的元数据修正。
     *
     * **执行序特性：**
     * - **时机优势**：它位于业务代码块（block）之后，`finally` 块（End 信号）之前。
     * - **异步安全**：作为一个 `suspend` 方法，它在协程作用域内执行，确保执行序的严谨性。
     * - **反馈路由**：执行此方法会通过 `emitRequestEvent` 发送 `requestEnd` 事件，通知 Activity/Fragment 退出活跃执行状态。
     *
     * **注意事项：**
     * - 此处主要用于处理“逻辑上的结束”。对于物理上的清理（如资源释放），建议放在 [End] 阶段。
     * - 如果该请求触发了 [handleBusinessError] 或 [handleBusinessEmpty]，此方法依然会被调用。
     *
     * @param config [ModelRequestConfig] - 请求上下文，用于追溯是哪个业务请求进入了结束阶段。
     */
    protected open suspend fun requestEnd(config: ModelRequestConfig,code : Int,msg : String?){

    }

    /**
     * ### 钩子方法：请求错误深度拦截器
     *
     * **核心职责：**
     * 在错误信息下发至 UI 层之前，提供一个业务拦截点。
     *
     * **典型应用：**
     * 1. **静默处理**：针对特定错误码（如数据未更新）执行静默忽略。
     * 2. **业务跳转**：如遇到 Token 失效、权限不足时，跳转登录页并返回 true 拦截后续弹窗。
     *
     * **注意：**
     * - 此方法运行在 Main 线程（由 [request] 方法调度）。
     * - 若返回 true，后续的 [handleBusinessError] 将不再被执行。
     *
     * @param config [ModelRequestConfig] - 请求上下文。
     * @param result [Result] - 包含原始错误码和消息的响应实体。
     * @return **Boolean** - true: 表示错误已被拦截处理，基类将终止后续 UI 反馈；false: 继续执行标准反馈。
     */
    protected open suspend fun handleBusinessError(config: ModelRequestConfig, error: VError) {
        // 3. 非鉴权错误走标准流程
        emitRequestEvent(RequestEvent.Error(config, error))
    }

    /**
     * ### 钩子方法：请求空数据处理拦截器
     *
     * **核心职责：**
     * 当后端返回成功码但数据体为空（Empty Data）时，控制 UI 状态的分发逻辑。
     *
     * **典型应用：**
     * 1. **局部缺省页显示**：根据 [config.requestCode] 配合 `wsui` 属性，通知特定 UI 组件切换至空状态占位图。
     * 2. **静默占位**：对于非关键接口（如预加载），可重写此方法为空实现，避免触发全局 Empty UI。
     * 3. **多级空状态识别**：利用 [code] 区分“搜索无结果”、“列表已到底”或“权限内无数据”等细分场景。
     *
     * **注意：**
     * - 默认实现会向下游发射 [RequestEvent.Empty] 事件。
     * - 若在子类重写且不调用 `super`，则该请求的空状态将不会传递至 View 层。
     *
     * @param config [ModelRequestConfig] - 请求上下文，包含 `requestCode` 等标识。
     * @param code [Int] - 业务定义的空状态码。
     * @param msg [String?] - 空状态下的提示文案（通常用于缺省页的 Subtitle）。
     */
    protected open suspend fun handleBusinessEmpty(config: ModelRequestConfig,code : Int,msg : String?){
        emitRequestEvent(RequestEvent.Empty(config,code,msg));
    }

    protected open suspend fun handleAuthError(config: ModelRequestConfig, error: VError){
        // 2. 转换为专项 AuthError 并分发，触发 UI 层的全局跳转逻辑
        val authFailure = AuthError();
        authFailure.resultId = error.resultId;
        authFailure.code = error.code;
        authFailure.msg = error.msg;
        authFailure.isSessionExpired = false;
        authFailure.timestamp = error.timestamp;
        withMain {
            _authError.value = authFailure;
        }
    }


    /**
     * ### 系统级/非预期异常处理器
     *
     * **职责说明：**
     * 捕获并处理请求过程中抛出的非业务类异常（如：解析异常、IO 超时、空指针等）。
     *
     * **处理机制：**
     * 1. 基于当前 [config.error()] 模板构建错误对象。
     * 2. 自动提取异常消息，若为空则映射为项目预设的通用服务错误文案。
     * 3. 最终通过 [handleBusinessError] 进入标准错误反馈链路。
     *
     * @param config [ModelRequestConfig] - 本次请求配置。
     * @param e [Exception] - 捕获到的原始异常对象。
     */
    protected open suspend fun handleException(config: ModelRequestConfig, e: Exception) {
        val error = config.error().apply {
            msg = e.message ?: getString(R.string.wsvita_app_default_service_error)
        }
        handleBusinessError(config, error)
    }

    /**
     * ### 钩子方法：业务执行成功但数据为空
     *
     * **触发时机：**
     * 当接口返回状态码标识成功（Result.isSuccess），但数据载体 [Result.data] 为 null 时。
     *
     * **使用场景：**
     * 子类可重写此方法，针对特定 [config.requestCode] 执行缺省页显示指令或日志埋点。
     *
     * @param config [ModelRequestConfig] - 请求上下文。
     * @param result [Result] - 原始请求响应结果。
     */
    protected open fun onEmptyData(config: ModelRequestConfig, result: Result<*>) {
        SLog.d(TAG, "onEmptyData -> requestCode=${config.requestCode}")
    }

    /**
     * 定义当请求成功但具体返回数据为空时，是否将其视为一个错误请求。
     *
     * **流程联动说明：**
     * 1. 如果返回 **true**：当数据为空时，框架会自动构建一个特定的空数据错误对象，
     * 并紧接着调用 [onRequestError] 方法。
     *
     * 2. 如果返回 **false**：当数据为空时，只会调用[onEmptyData]方法。子类如果需要捕获空和请求异常，则一般需要重写
     * [onEmptyData]和[onRequestError]方法
     * 默认返回 true。
     *
     *  **子组件开发规范**：在子组件中，你通常只需要重写 [onRequestError] 即可统一处理
     * * 接口失败和空数据这两种“异常”情况，无需在业务层重复判断 null。
     *
     * create by Eastevil at 2025/12/31 13:26
     * @author Eastevil
     * @param
     * @return true-表示空数据视为错误（将触发后续错误回调），false-表示视为正常逻辑。
     *      默认为true
     */
    protected open fun isDataEmptyAsError(requestCode : Int): Boolean {
        return true
    }

    /**
     * ### 钩子方法：请求错误深度拦截器
     *
     * **核心职责：**
     * 在错误信息下发至 UI 层之前，提供一个业务拦截点。
     *
     * **典型应用：**
     * 1. **静默处理**：针对特定错误码（如数据未更新）执行静默忽略。
     * 2. **业务跳转**：如遇到 Token 失效、权限不足时，跳转登录页并返回 true 拦截后续弹窗。
     *
     * **注意：**
     * - 此方法运行在 Main 线程（由 [request] 方法调度）。
     * - 若返回 true，后续的 [handleBusinessError] 将不再被执行。
     *
     * @param config [ModelRequestConfig] - 请求上下文。
     * @param result [Result] - 包含原始错误码和消息的响应实体。
     * @return **Boolean** - true: 表示错误已被拦截处理，基类将终止后续 UI 反馈；false: 继续执行标准反馈。
     */
    protected open fun onRequestError(config: ModelRequestConfig, result: Result<*>): Boolean {
        return false
    }

    /**
     * ### 钩子方法：请求成功前置拦截器
     *
     * **核心职责：**
     * 在请求成功并即将返回数据给业务调用方之前，提供一个全局或业务级的处理切面。
     *
     * **典型应用：**
     * 1. **数据审计**：记录特定请求码（config.requestCode）的成功日志或埋点。
     * 2. **通用提示**：根据服务端返回的特定 [msg] 弹出非阻断式的成功提示（如“修改成功”）。
     * 3. **配置同步**：在某些关键接口成功后，触发全局配置或缓存的刷新。
     *
     * **注意：**
     * - 此方法仅在 [result.isSuccess] 且数据非空时由 [requestInternal] 调用。
     * - 该回调运行在 Main 线程，不建议执行耗时同步操作。
     * - 区别于 [success] 方法，此处侧重于网络层请求的“原始成功信号”拦截。
     *
     * @param config [ModelRequestConfig] - 请求上下文配置，用于识别请求来源。
     * @param code [Int] - 原始请求成功的状态码（如 200 或业务约定的成功码）。
     * @param msg [String] - 原始请求返回的成功提示信息。
     */
    protected open fun onRequestSuccess(config: ModelRequestConfig,code : Int,msg : String?) : Boolean{
        return false;
    }

    /**
     * ### 请求事件发射器
     *
     * **核心职责：**
     * 将业务流程中的关键节点（如开始、成功、失败、结束）封装为 [RequestEvent]，
     * 通过 [MutableSharedFlow] 异步推送到 View 层。
     *
     * **技术细节：**
     * 1. **线程隔离**：使用 `viewModelScope.launch` 启动协程，确保发射操作即使在非 UI 线程调用时也是安全的。
     * 2. **响应式驱动**：由于使用 [SharedFlow]，View 层（Activity/Fragment）必须在活跃状态下通过 `collect` 监听才能接收到信号。
     * 3. **组件解耦**：这是 ViewModel 唯一向外输出交互指令的通道，实现了业务逻辑与具体 UI 实现（Toast/Dialog）的解耦。
     *
     * @param event [RequestEvent] 即将发送给 UI 层的具体事件指令。
     */
    protected suspend fun emitRequestEvent(event: RequestEvent) {
        _requestEvent.emit(event)
    }

    companion object {
        private const val TAG = "WSVita_AppViewModel==>"
    }
}

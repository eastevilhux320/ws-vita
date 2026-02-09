package com.wsvita.biz.core.model.protocol

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreActivity
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.databinding.ActivityProtocolBinding
import com.wsvita.framework.utils.SLog

class ProtocolActivity : BizcoreActivity<ActivityProtocolBinding, ProtocolViewModel>() {

    override fun layoutId(): Int {
        return R.layout.activity_protocol;
    }

    override fun getVMClass(): Class<ProtocolViewModel> {
        return ProtocolViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.activity = this;

        initWebview();
    }

    override fun onConfigChanged(config: BizcoreConfig) {
        super.onConfigChanged(config)
        dataBinding.titleCoreProtocol.setBackgroundColor(config.mainThemeColor);
        dataBinding.btnBizcoreAllow.setBackgroundColor(config.mainThemeColor);
        dataBinding.btnBizcoreAllow.setTextColor(Color.WHITE);
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.protocolUrl.observe(this, Observer {
            dataBinding.webview.loadUrl(it);
        })
    }

    override fun autoIntentValue(): MutableList<String>? {
        val list = mutableListOf<String>();
        list.add(BizConstants.IntentKey.PROTOCOL_URL_KEY);
        return list;
    }

    override fun onIntentReceivedString(key: String, value: String) {
        super.onIntentReceivedString(key, value)
        //只有一个值，直接设置即可
        viewModel.resetUrl(value);
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.iv_bizcore_agree->{
                viewModel.setAgreeFlag();
            }
            R.id.btn_bizcore_allow->{
                //同意并继续
                finishWithResult(BizConstants.IntentKey.PROTOCOL_RESULT_FLAG,true);
            }
            R.id.btn_bizcore_deny->{
                //拒绝
                finishWithResult(BizConstants.IntentKey.PROTOCOL_RESULT_FLAG,false);
            }
        }
    }

    private fun initWebview(){
        val webSettings: WebSettings = dataBinding.webview.getSettings()
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        //设置自适应屏幕，两者合用
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小

        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。

        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放

        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        //其他细节操作
        //其他细节操作
        webSettings.allowFileAccess = true //设置可以访问文件

        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口

        webSettings.loadsImagesAutomatically = true //支持自动加载图片

        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式

        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        //webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        //String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        //webSettings.setAppCachePath(appCachePath);
        //webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        //String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        //webSettings.setAppCachePath(appCachePath);
        //禁止使用缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.pluginState = WebSettings.PluginState.ON

        dataBinding.webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if(url.toLowerCase().startsWith("http")){
                    view.loadUrl(url)
                }else{
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(url)
                        addCategory(Intent.CATEGORY_DEFAULT)
                    }
                    startActivity(intent);
                }
                return true
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SLog.d(TAG, "request==>,errorCode==" + error.errorCode + ",errorMsg=>" + error.description + "}")
                }
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != ApplicationInfo.FLAG_DEBUGGABLE.let {
                    applicationInfo.flags =
                        applicationInfo.flags and it; applicationInfo.flags
                }) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    companion object{
        private const val TAG = "WS_Biz_Splash_ProtocolActivity=>"
    }
}

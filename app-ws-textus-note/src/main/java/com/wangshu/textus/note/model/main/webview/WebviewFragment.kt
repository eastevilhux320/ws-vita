package com.wangshu.note.app.model.main.webview

import android.os.Bundle
import com.wangshu.note.app.R
import com.wangshu.note.app.common.NoteFragment
import com.wangshu.note.app.databinding.FragmentMainWebviewBinding

class WebviewFragment : NoteFragment<FragmentMainWebviewBinding, WebviewViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main_webview;
    }

    override fun getVMClass(): Class<WebviewViewModel> {
        return WebviewViewModel::class.java;
    }

    companion object{
        private const val TAG = "WS_Note_Main_WebviewFragment==>";

        fun newInstance(): WebviewFragment {
            val args = Bundle()
            val fragment = WebviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

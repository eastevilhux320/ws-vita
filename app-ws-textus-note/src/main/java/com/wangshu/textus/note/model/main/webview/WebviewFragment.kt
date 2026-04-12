package com.wangshu.textus.note.model.main.webview

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainWebviewBinding

class WebviewFragment : NoteFragment<FragmentMainWebviewBinding, WebviewViewModel>() {
    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_webview;
    }

    override fun layoutId(): Int {
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

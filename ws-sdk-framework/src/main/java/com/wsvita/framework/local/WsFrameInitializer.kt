package com.wsvita.framework.local

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log

/**
 * **WsFrameInitializer**
 * Internal Initializer to automatically capture Context.
 * Part of the ws-vita framework infrastructure.
 */
internal class WsFrameInitializer : ContentProvider() {

    companion object {
        private const val TAG = "WSF_WsFrameInitializer=>"
    }

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate: Initializing framework context...")
        (context?.applicationContext as? Application)?.let {
            WsContext.init(it)
            Log.i(TAG, "onCreate: WsContext initialized successfully.")
        } ?: Log.e(TAG, "onCreate: Failed to initialize WsContext - Application is null.")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.v(TAG, "query called for URI: $uri")
        return null
    }

    override fun getType(uri: Uri): String? {
        Log.v(TAG, "getType called for URI: $uri")
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.v(TAG, "insert called for URI: $uri")
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.v(TAG, "delete called for URI: $uri")
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.v(TAG, "update called for URI: $uri")
        return 0
    }
}

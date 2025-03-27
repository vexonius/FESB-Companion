package com.tstudioz.fax.fme.feature.studomat.compose

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(studomatViewModel: StudomatViewModel) {
    AndroidView(
        factory = { context ->
            val webview = WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
            val cookieManager = CookieManager.getInstance()
            val cookie = studomatViewModel.fetchISVUCookie()

            with(cookieManager) {
                setAcceptCookie(true)
                if (!cookie.isNullOrEmpty()) setCookie("https://www.isvu.hr/studomat/hr/", cookie)
                flush()
            }
            webview.loadUrl("https://www.isvu.hr/studomat/hr/ispit/ponudapredmetazaprijavuispita")
            webview
        },
        modifier = Modifier.background(Color.Black).fillMaxSize(),
    )
}
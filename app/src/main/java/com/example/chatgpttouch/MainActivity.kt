
package com.example.chatgpttouch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = false

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.addJavascriptInterface(
            AndroidTouch(),
            "AndroidTouch"
        )

        setContentView(webView)

        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class AndroidTouch {

        @JavascriptInterface
        fun isAvailable(): Boolean {
            return TouchAccessibilityService.instance != null
        }

        @JavascriptInterface
        fun openAccessibilitySettings() {
            runOnUiThread {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        }

        @JavascriptInterface
        fun tap(x: Float, y: Float): Boolean {
            return TouchAccessibilityService.instance?.tap(x, y) ?: false
        }

        @JavascriptInterface
        fun swipe(
            startX: Float,
            startY: Float,
            endX: Float,
            endY: Float
        ): Boolean {
            return TouchAccessibilityService.instance?.swipe(
                startX,
                startY,
                endX,
                endY,
                400
            ) ?: false
        }

        @JavascriptInterface
        fun back(): Boolean {
            return TouchAccessibilityService.instance?.goBack() ?: false
        }

        @JavascriptInterface
        fun home(): Boolean {
            return TouchAccessibilityService.instance?.goHome() ?: false
        }

        @JavascriptInterface
        fun readScreen(): String {
            return TouchAccessibilityService.instance
                ?.readVisibleText()
                ?: "Accessibility service is not enabled."
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.removeJavascriptInterface("AndroidTouch")
        webView.destroy()
        super.onDestroy()
    }
}

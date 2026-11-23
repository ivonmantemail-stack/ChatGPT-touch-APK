package com.example.chatgpttouch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TouchAccessibilityService : AccessibilityService() {

    companion object {
        var instance: TouchAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Events are not stored or sent anywhere.
    }

    override fun onInterrupt() {
        // Nothing to interrupt.
    }

    override fun onDestroy() {
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }

    fun tap(x: Float, y: Float): Boolean {
        if (x < 0 || y < 0) return false

        val path = Path().apply {
            moveTo(x, y)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    100
                )
            )
            .build()

        return dispatchGesture(gesture, null, null)
    }

    fun swipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long
    ): Boolean {
        if (
            startX < 0 || startY < 0 ||
            endX < 0 || endY < 0
        ) return false

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    duration.coerceIn(100, 2000)
                )
            )
            .build()

        return dispatchGesture(gesture, null, null)
    }

    fun goBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun goHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun readVisibleText(): String {
        val root = rootInActiveWindow ?: return "No accessible screen found."
        val result = StringBuilder()

        fun collect(node: AccessibilityNodeInfo?) {
            if (node == null || result.length >= 8000) return

            val isPassword = node.isPassword

            if (!isPassword) {
                val text = node.text?.toString()
                val description = node.contentDescription?.toString()

                if (!text.isNullOrBlank()) {
                    result.append(text).append('\n')
                }

                if (!description.isNullOrBlank()) {
                    result.append(description).append('\n')
                }
            }

            for (i in 0 until node.childCount) {
                collect(node.getChild(i))
            }
        }

        collect(root)
        return result.toString().take(8000)
    }
}


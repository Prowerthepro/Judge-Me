package com.socialscreencontrol.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AppAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Foreground app changes are already monitored by UsageTrackingService.
    }

    override fun onInterrupt() = Unit
}

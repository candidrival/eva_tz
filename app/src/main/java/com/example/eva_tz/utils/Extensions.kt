package com.example.eva_tz.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast


private var doubleBackToExitPressedOnce = false

fun doubleClickExit(activity: Activity) {
    if (doubleBackToExitPressedOnce) {
        activity.finishAndRemoveTask()
        return
    }

    doubleBackToExitPressedOnce = true
    showToast(activity, "Please click BACK again to exit")

    Handler(Looper.getMainLooper()).postDelayed(
        { doubleBackToExitPressedOnce = false },
        2000
    )
}

private var isToastDisplayed = false

fun showToast(context: Context, message: String) {
    if (!isToastDisplayed) {
        isToastDisplayed = true

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            isToastDisplayed = false
        }, 3000)
    }
}
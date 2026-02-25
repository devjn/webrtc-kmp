package com.shepeliev.webrtckmp.capturer

import android.content.Context
import android.media.projection.MediaProjection
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.shepeliev.webrtckmp.DEFAULT_FRAME_RATE
import com.shepeliev.webrtckmp.MediaProjectionIntentHolder
import com.shepeliev.webrtckmp.WebRtc
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.Size
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource

internal class ScreenCapturerController(
    videoSource: VideoSource,
) : VideoCapturerController(videoSource) {

    override fun createVideoCapturer(): VideoCapturer {
        return ScreenCapturerAndroid(
            MediaProjectionIntentHolder.intent,
            object : MediaProjection.Callback() {}
        )
    }

    override fun selectVideoSize(): Size {
        val displayMetrics = DisplayMetrics()
        val context = WebRtc.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics =
                context
                    .getSystemService(WindowManager::class.java)
                    .currentWindowMetrics
                    .bounds
            displayMetrics.widthPixels = windowMetrics.width()
            displayMetrics.heightPixels = windowMetrics.height()
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getRealMetrics(displayMetrics)
        }
        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    override fun selectFps(): Int {
        return DEFAULT_FRAME_RATE
    }
}

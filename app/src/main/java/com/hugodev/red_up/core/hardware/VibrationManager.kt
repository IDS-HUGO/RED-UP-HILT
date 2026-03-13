package com.hugodev.red_up.core.hardware

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VibrationManager @Inject constructor(
    private val vibrator: Vibrator
) {
    /**
     * Hace que el dispositivo vibre por un tiempo determinado en milisegundos.
     */
    fun vibrate(duration: Long = 500) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    /**
     * Hace que el dispositivo vibre con un patrón de "notificación".
     */
    fun vibrateNotification() {
        val pattern = longArrayOf(0, 200, 100, 200) // Espera 0, Vibra 200, Espera 100, Vibra 200
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    /**
     * Feedback háptico ligero (como un click de botón físico).
     * Ideal para interacciones de UI como Likes o clics en botones.
     */
    fun vibrateClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            vibrate(20) // Vibración muy corta para versiones antiguas
        }
    }
}

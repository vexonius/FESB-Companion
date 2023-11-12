package com.tstudioz.fax.fme

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.concurrent.Executor


class AuthManager(val context: Context) {

    val biometricManager: BiometricManager
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    init {
        biometricManager = BiometricManager.from(context)
        checkBiometricHardwareSupport()
    }

    fun checkBiometricHardwareSupport() {

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Timber.d("App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Timber.e("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Timber.e("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Timber.e("The user hasn't associated any biometric credentials with their account.")
        }
    }

    fun authenticate(){

    }
}
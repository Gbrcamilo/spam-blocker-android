package com.seunome.spamblocker

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log

class ScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val settings = SettingsRepository(applicationContext)
        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
        val handle: Uri? = callDetails.handle
        val phoneNumber = handle?.schemeSpecificPart ?: ""

        Log.d(TAG, "onScreenCall: incoming=$isIncoming number=$phoneNumber")

        if (!isIncoming) {
            allow(callDetails)
            return
        }

        if (!settings.isBlockingEnabled()) {
            allow(callDetails)
            return
        }

        when (callDetails.callerNumberVerificationStatus) {
            Connection.VERIFICATION_STATUS_FAILED -> {
                Log.d(TAG, "Network verification failed for $phoneNumber")
            }
            Connection.VERIFICATION_STATUS_PASSED -> {
                Log.d(TAG, "Network verification passed for $phoneNumber")
            }
            else -> {
                Log.d(TAG, "Network verification unavailable for $phoneNumber")
            }
        }

        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSilenceCall(true)
            .setSkipCallLog(settings.shouldSkipCallLog())
            .setSkipNotification(settings.shouldSkipNotification())
            .build()

        respondToCall(callDetails, response)
    }

    private fun allow(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSilenceCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(callDetails, response)
    }

    companion object {
        private const val TAG = "ScreeningService"
    }
}

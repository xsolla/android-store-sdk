package com.xsolla.android.storesdkexample.data.auth

import com.playfab.PlayFabClientAPI
import com.playfab.PlayFabClientModels
import com.playfab.PlayFabSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Auth {

    @JvmStatic
    fun init(playFabTitleId: String) {
        PlayFabSettings.TitleId = playFabTitleId
    }

    @JvmStatic
    fun getToken() = PlayFabSettings.ClientSessionTicket

    @JvmStatic
    fun isTokenExpired() = getToken() == null

    @JvmStatic
    fun register(username: String, email: String, password: String, callback: AuthCallback) = GlobalScope.launch {
        val result = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.RegisterPlayFabUserRequest()
            request.Username = username
            request.Email = email
            request.Password = password
            PlayFabClientAPI.RegisterPlayFabUser(request)
        }
        withContext(Dispatchers.Main) {
            if (result.Error == null) {
                callback.onSuccess()
            } else {
                callback.onFailure(result.Error.errorMessage)
            }
        }
    }

    @JvmStatic
    fun login(username: String, password: String, callback: AuthCallback) = GlobalScope.launch {
        val result = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.LoginWithPlayFabRequest()
            request.Username = username
            request.Password = password
            PlayFabClientAPI.LoginWithPlayFab(request)
        }
        withContext(Dispatchers.Main) {
            if (result.Error == null) {
                PlayFabSettings.ClientSessionTicket = result.Result.SessionTicket
                callback.onSuccess()
            } else {
                callback.onFailure(result.Error.errorMessage)
            }
        }
    }

    @JvmStatic
    fun logout() {
        PlayFabSettings.ClientSessionTicket = null
    }

    @JvmStatic
    fun resetPassword(email: String, callback: AuthCallback) = GlobalScope.launch {
        val result = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.SendAccountRecoveryEmailRequest()
            request.Email = email
            request.TitleId = PlayFabSettings.TitleId
            PlayFabClientAPI.SendAccountRecoveryEmail(request)
        }
        withContext(Dispatchers.Main) {
            if (result.Error == null) {
                callback.onSuccess()
            } else {
                callback.onFailure(result.Error.errorMessage)
            }
        }
    }

    interface AuthCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

}
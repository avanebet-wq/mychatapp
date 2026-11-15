package com.example.mychatapp

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi

object Tg {
    var state by mutableStateOf("loading")
    var error by mutableStateOf("")

    private var client: Client? = null
    private lateinit var appContext: Context

    fun start(context: Context) {
        if (client != null) return
        appContext = context.applicationContext
        client = Client.create({ obj -> onUpdate(obj) }, null, null)
    }

    private fun onUpdate(obj: TdApi.Object) {
        if (obj is TdApi.UpdateAuthorizationState) {
            when (obj.authorizationState) {
                is TdApi.AuthorizationStateWaitTdlibParameters -> sendParams()
                is TdApi.AuthorizationStateWaitPhoneNumber -> state = "phone"
                is TdApi.AuthorizationStateWaitCode -> state = "code"
                is TdApi.AuthorizationStateWaitPassword -> state = "password"
                is TdApi.AuthorizationStateReady -> state = "ready"
                else -> state = "other"
            }
        }
    }

    private fun sendParams() {
        val p = TdApi.SetTdlibParameters()
        p.databaseDirectory = appContext.filesDir.absolutePath + "/tdlib"
        p.databaseEncryptionKey = ByteArray(0)
        p.useFileDatabase = true
        p.useChatInfoDatabase = true
        p.useMessageDatabase = true
        p.useSecretChats = false
        p.apiId = BuildConfig.TG_API_ID
        p.apiHash = BuildConfig.TG_API_HASH
        p.systemLanguageCode = "ru"
        p.deviceModel = Build.MODEL
        p.systemVersion = Build.VERSION.RELEASE
        p.applicationVersion = "0.1"
        send(p)
    }

    private fun send(f: TdApi.Function<*>) {
        error = ""
        client?.send(f) { r ->
            if (r is TdApi.Error) error = r.message
        }
    }

    fun sendPhone(phone: String) = send(TdApi.SetAuthenticationPhoneNumber(phone, null))
    fun sendCode(code: String) = send(TdApi.CheckAuthenticationCode(code))
    fun sendPassword(pass: String) = send(TdApi.CheckAuthenticationPassword(pass))
}

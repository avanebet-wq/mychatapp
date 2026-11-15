package com.example.mychatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tg.start(applicationContext)
        setContent {
            MaterialTheme { Screen() }
        }
    }
}

@Composable
fun Screen() {
    var text by remember(Tg.state) { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (Tg.state) {
            "phone" -> {
                Text("Введите номер телефона")
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    label = { Text("+79991234567") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { Tg.sendPhone(text) }) { Text("Далее") }
            }
            "code" -> {
                Text("Введите код из Telegram")
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    label = { Text("Код") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { Tg.sendCode(text) }) { Text("Войти") }
            }
            "password" -> {
                Text("Введите облачный пароль (2FA)")
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { Tg.sendPassword(text) }) { Text("Войти") }
            }
            "ready" -> Text("Вы вошли в аккаунт!")
            else -> Text("Загрузка...")
        }
        if (Tg.error.isNotEmpty()) {
            Text("Ошибка: " + Tg.error, color = MaterialTheme.colorScheme.error)
        }
    }
}

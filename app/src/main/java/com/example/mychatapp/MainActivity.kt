package com.example.mychatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        Tg.start(applicationContext)
        setContent {
            MaterialTheme { Screen() }
        }
    }
}

class Backdrop(val t: State<Float>, val size: Size)

val LocalBackdrop = compositionLocalOf<Backdrop?> { null }

fun DrawScope.drawAurora(t: Float, w: Float, h: Float) {
    drawRect(
        brush = Brush.linearGradient(
            listOf(Color(0xFF120B2E), Color(0xFF3A2C7A), Color(0xFF1B1E4B)),
            start = Offset(0f, 0f), end = Offset(w, h)
        ),
        size = Size(w, h)
    )

    fun blob(color: Color, cx: Float, cy: Float, r: Float) {
        val c = Offset(cx, cy)
        drawCircle(
            brush = Brush.radialGradient(listOf(color.copy(alpha = 0.8f), Color.Transparent), center = c, radius = r),
            radius = r, center = c
        )
    }

    fun orb(c1: Color, c2: Color, cx: Float, cy: Float, r: Float) {
        drawCircle(
            brush = Brush.linearGradient(
                listOf(c1, c2),
                start = Offset(cx - r, cy - r), end = Offset(cx + r, cy + r)
            ),
            radius = r, center = Offset(cx, cy)
        )
    }

    blob(Color(0xFFFF5F9E), w * (0.15f + 0.5f * t), h * (0.20f + 0.08f * t), w * 0.9f)
    blob(Color(0xFF37D5FF), w * (0.90f - 0.5f * t), h * (0.55f - 0.10f * t), w * 0.95f)
    blob(Color(0xFFFFA24C), w * (0.30f + 0.3f * t), h * (0.95f - 0.12f * t), w * 0.85f)

    orb(Color(0xFFFFD36B), Color(0xFFFF4D8D), w * (0.80f - 0.12f * t), h * (0.30f + 0.04f * t), w * 0.17f)
    orb(Color(0xFF6BFFD8), Color(0xFF3A7BFF), w * (0.20f + 0.12f * t), h * (0.70f - 0.05f * t), w * 0.21f)
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape,
    strong: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val backdrop = LocalBackdrop.current
    var pos by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier
            .clip(shape)
            .onGloballyPositioned { pos = it.positionInRoot() }
    ) {
        if (backdrop != null) {
            Canvas(Modifier.matchParentSize().blur(24.dp)) {
                val cx = pos.x + size.width / 2f
                val cy = pos.y + size.height / 2f
                withTransform({
                    translate(-pos.x, -pos.y)
                    scale(1.1f, 1.1f, Offset(cx, cy))
                }) {
                    drawAurora(backdrop.t.value, backdrop.size.width, backdrop.size.height)
                }
            }
        }
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    if (strong) listOf(Color.White.copy(0.34f), Color.White.copy(0.12f))
                    else listOf(Color.White.copy(0.20f), Color.White.copy(0.06f))
                )
            )
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.radialGradient(
                    listOf(Color.White.copy(0.35f), Color.Transparent),
                    center = Offset(0f, 0f), radius = 520f
                )
            )
        )
        Box(
            Modifier.matchParentSize().border(
                1.2.dp,
                Brush.linearGradient(
                    listOf(Color.White.copy(0.85f), Color.White.copy(0.05f), Color.White.copy(0.45f))
                ),
                shape
            )
        )
        content()
    }
}

@Composable
fun GlassField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    keyboard: KeyboardType,
    hidden: Boolean
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.Black.copy(0.16f))
            .border(1.dp, Color.White.copy(0.28f), shape)
    ) {
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            placeholder = { Text(placeholder, color = Color.White.copy(0.5f)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GlassButton(label: String, onClick: () -> Unit) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        strong = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
    }
}

@Composable
fun AuthContent(s: String) {
    var text by remember { mutableStateOf("") }

    val title: String
    val hint: String
    val button: String
    val placeholder: String
    val keyboard: KeyboardType
    when (s) {
        "phone" -> { title = "Вход"; hint = "Введите номер телефона"; button = "Далее"; placeholder = "+7 999 123 45 67"; keyboard = KeyboardType.Phone }
        "code" -> { title = "Код"; hint = "Мы отправили код в Telegram"; button = "Войти"; placeholder = "Код"; keyboard = KeyboardType.Number }
        "password" -> { title = "Пароль"; hint = "Облачный пароль двухэтапной защиты"; button = "Войти"; placeholder = "Пароль"; keyboard = KeyboardType.Password }
        "ready" -> { title = "Готово"; hint = "Вы вошли в аккаунт"; button = ""; placeholder = ""; keyboard = KeyboardType.Text }
        else -> { title = "MyChat"; hint = "Загрузка…"; button = ""; placeholder = ""; keyboard = KeyboardType.Text }
    }
    val hasInput = s == "phone" || s == "code" || s == "password"

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(36.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(hint, color = Color.White.copy(0.78f), fontSize = 15.sp, textAlign = TextAlign.Center)

                if (hasInput) {
                    Spacer(Modifier.height(24.dp))
                    GlassField(text, { text = it }, placeholder, keyboard, hidden = s == "password")
                    Spacer(Modifier.height(16.dp))
                    GlassButton(button) {
                        when (s) {
                            "phone" -> Tg.sendPhone(text.filter { it.isDigit() || it == '+' })
                            "code" -> Tg.sendCode(text.trim())
                            "password" -> Tg.sendPassword(text)
                        }
                    }
                }

                if (Tg.error.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text(Tg.error, color = Color(0xFFFFB4B4), fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun Screen() {
    val transition = rememberInfiniteTransition(label = "aurora")
    val t = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t"
    )
    var rootSize by remember { mutableStateOf(Size.Zero) }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { rootSize = Size(it.width.toFloat(), it.height.toFloat()) }
    ) {
        val backdrop = if (rootSize.width > 0f) Backdrop(t, rootSize) else null
        CompositionLocalProvider(LocalBackdrop provides backdrop) {
            Canvas(Modifier.fillMaxSize()) {
                drawAurora(t.value, size.width, size.height)
            }
            Crossfade(targetState = Tg.state, label = "auth") { s -> AuthContent(s) }
        }
    }
}

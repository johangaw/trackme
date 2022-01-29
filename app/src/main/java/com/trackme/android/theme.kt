package com.trackme.android

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val colors = lightColors(
    primary = Color(0x0F, 0x44, 0x25),
    primaryVariant = Color(0x52, 0x96, 0x51),
    secondary = Color(0xE3, 0x54, 0x35),
    secondaryVariant = Color(0xFF, 0xCC, 0x47),
    onPrimary = Color.White,
    onSecondary = Color.White,
    background = Color(0xF3, 0xF3, 0xF3),
    onBackground = Color.Black,
    surface = Color(0xF3, 0xF3, 0xF3),
    onSurface = Color.Black,
)


val Colors.primaryVariant2: Color get() = Color(0x76, 0xC0, 0x74, 0xE5)

val fonts = FontFamily(listOf(
    Font(R.font.roboto_black),
    Font(R.font.roboto_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_light, weight = FontWeight.Light),
    Font(R.font.roboto_thin, weight = FontWeight.Thin),
    Font(R.font.roboto_italic, weight = FontWeight.Normal, style = FontStyle.Italic)
))

val typography = Typography(
    defaultFontFamily = fonts,
    h1 = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Bold, fontSize = 36.sp),
    h2 = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    subtitle1 = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    body1 = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Bold, fontSize = 12.sp),
    button = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Bold, fontSize = 36.sp),
)

val Typography.buttonLarge get() = TextStyle(fontFamily = fonts, fontWeight = FontWeight.Normal, fontSize = 56.sp)

@Composable
fun TrackMeTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )
}
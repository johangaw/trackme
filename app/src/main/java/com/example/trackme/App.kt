package com.example.trackme

import android.os.Parcelable
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.common.ui.Navigation
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

@Composable
fun App(onBackPressedDispatcher: OnBackPressedDispatcher) {
    val navigator: Navigation<Destination> =
        rememberSavedInstanceState(saver = Navigation.saver(onBackPressedDispatcher)) {
            Navigation(onBackPressedDispatcher,
                       Destination.Red(0))
        }

    Providers(NavigationAmbient provides navigator) {
        Crossfade(current = navigator.current) {
            when (it) {
                Destination.Green -> GreenScreen()
                is Destination.Red -> RedScreen(it.id)
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_3, showDecoration = true, showBackground = true)
fun AppPreview() {
    App(OnBackPressedDispatcher())
}

@Composable
fun RedScreen(id: Int) {
    val navigator = NavigationAmbient.current
    Column(Modifier.fillMaxSize().background(Color.Red)) {
        Button(onClick = { navigator.push(Destination.Green) }, backgroundColor = Color.Green) {
            Text(text = "To Green")
        }
        Button(onClick = { navigator.pop() }, backgroundColor = Color.Gray) {
            Text(text = "back")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = id.toString(),
                style = MaterialTheme.typography.h1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun GreenScreen() {
    val navigator = NavigationAmbient.current
    Column(Modifier.fillMaxSize().background(Color.Green)) {
        Button(onClick = { navigator.push(Destination.Red(Random.nextInt(0, 100))) },
               backgroundColor = Color.Red) {
            Text(text = "To Red")
        }
        Button(onClick = { navigator.pop() }, backgroundColor = Color.Gray) {
            Text(text = "back")
        }
    }
}

internal val NavigationAmbient = staticAmbientOf<Navigation<Destination>> {
    error("No navigation created")
}

sealed class Destination : Parcelable {

    @Parcelize
    class Red(val id: Int) : Destination()

    @Parcelize
    object Green : Destination()
}



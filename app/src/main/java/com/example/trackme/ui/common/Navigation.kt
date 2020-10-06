package com.example.trackme.ui.common

import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.savedinstancestate.listSaver
import androidx.compose.runtime.toMutableStateList

class Navigation<T> private constructor(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    initialBackStack: List<T>,
) {
    constructor(
        onBackPressedDispatcher: OnBackPressedDispatcher,
        initialDestination: T,
    ) : this(onBackPressedDispatcher, listOf(initialDestination))

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            pop()
        }
    }.also { callback ->
        onBackPressedDispatcher.addCallback(callback)
    }


    private val backStack = initialBackStack.toMutableStateList()

    val current: T get() = backStack.last()

    fun push(destination: T) {
        backStack += destination
        callback.isEnabled = canPop
    }

    fun pop() {
        if (canPop) {
            backStack.removeAt(backStack.lastIndex)
            callback.isEnabled = canPop
        }
    }

    fun popToTop() {
        backStack.removeRange(1, backStack.size)
        callback.isEnabled = canPop
    }

    private val canPop get() = backStack.size > 1

    companion object {
        fun <T : Parcelable> saver(onBackPressedDispatcher: OnBackPressedDispatcher) =
            listSaver<Navigation<T>, T>(
                save = { original -> original.backStack.toList() },
                restore = { list -> Navigation(onBackPressedDispatcher, list) }
            )
    }
}
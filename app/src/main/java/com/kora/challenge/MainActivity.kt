package com.kora.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kora.challenge.data.KoraStore
import com.kora.challenge.navigation.AppNav
import com.kora.challenge.ui.theme.KoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KoraStore.init(applicationContext)
        setContent {
            var dark by rememberSaveable { mutableStateOf(true) } // الموقع افتراضيه داكن
            KoraTheme(dark = dark) {
                AppNav(dark = dark, onToggleTheme = { dark = !dark })
            }
        }
    }
}

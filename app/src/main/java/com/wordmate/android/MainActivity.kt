package com.wordmate.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wordmate.android.navigation.WordMateApp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wordmate.android.core.designsystem.theme.WordMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordMateTheme {
                WordMateApp()
            }
        }
    }
}

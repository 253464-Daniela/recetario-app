package com.daniela.recetario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.daniela.recetario.ui.navigation.RecetarioNavHost
import com.daniela.recetario.ui.theme.RecetarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecetarioTheme {
                RecetarioNavHost()
            }
        }
    }
}
package com.daniela.recetario.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.daniela.recetario.RecetarioApplication

@Composable
fun recetarioApp(): RecetarioApplication =
    LocalContext.current.applicationContext as RecetarioApplication
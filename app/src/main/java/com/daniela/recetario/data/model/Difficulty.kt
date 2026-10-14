package com.daniela.recetario.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty(val level: Int) {
    FACIL(1),
    MEDIA(2),
    DIFICIL(3)
}
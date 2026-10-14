package com.daniela.recetario.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class RecipeCategory {
    ENTRADA,
    PLATO_FUERTE,
    POSTRE,
    BEBIDA,
    SOPA,
    ENSALADA
}
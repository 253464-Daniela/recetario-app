package com.daniela.recetario.ui.screens.detail

/**
 * Eventos que la UI de detalle emite al ViewModel.
 * UDF: los eventos suben, el estado baja.
 */
sealed interface RecipeDetailEvent {

    /** El usuario mueve el slider de porciones. */
    data class OnPortionsChange(val portions: Int) : RecipeDetailEvent

    /** El usuario toca el corazón para marcar/desmarcar como favorita. */
    data object OnToggleFavorite : RecipeDetailEvent
}
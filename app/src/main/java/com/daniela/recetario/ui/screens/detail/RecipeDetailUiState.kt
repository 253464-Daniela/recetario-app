package com.daniela.recetario.ui.screens.detail

import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.Recipe

/**
 * Estado inmutable de la pantalla de detalle.
 *
 * El campo [scaledIngredients] es derivado: se recalcula cada vez que
 * cambia [selectedPortions] o al cargar la receta.
 */
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val selectedPortions: Int = 1,
    val scaledIngredients: List<Ingredient> = emptyList(),
    val isLoading: Boolean = true,
    val notFound: Boolean = false
)

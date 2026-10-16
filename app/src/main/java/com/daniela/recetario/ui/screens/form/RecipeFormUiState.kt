package com.daniela.recetario.ui.screens.form

import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.RecipeCategory

/**
 * Estado inmutable del formulario de creación de recetas.
 *
 * Toda la validación se refleja aquí (campos *_error).
 * El [isValid] es derivado: se recalcula tras cada cambio.
 */
data class RecipeFormUiState(
    // ---------- Campos ----------
    val title: String = "",
    val description: String = "",
    val category: RecipeCategory = RecipeCategory.PLATO_FUERTE,
    val difficulty: Difficulty = Difficulty.FACIL,
    val prepTime: String = "",
    val basePortions: Int = 4,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList(),

    // ---------- Errores de validación ----------
    val titleError: Int? = null,       // resource ID del mensaje
    val prepTimeError: Int? = null,
    val ingredientsError: Int? = null,
    val stepsError: Int? = null,

    // ---------- Estado derivado ----------
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false
)
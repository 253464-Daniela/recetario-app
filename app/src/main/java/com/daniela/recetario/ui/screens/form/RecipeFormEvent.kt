package com.daniela.recetario.ui.screens.form

import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.RecipeCategory

/**
 * Contrato de eventos del formulario.
 * UDF: la UI solo notifica intenciones, el ViewModel decide.
 */
sealed interface RecipeFormEvent {

    data class OnTitleChange(val value: String) : RecipeFormEvent
    data class OnDescriptionChange(val value: String) : RecipeFormEvent
    data class OnCategoryChange(val category: RecipeCategory) : RecipeFormEvent
    data class OnDifficultyChange(val difficulty: Difficulty) : RecipeFormEvent
    data class OnPrepTimeChange(val value: String) : RecipeFormEvent
    data class OnBasePortionsChange(val portions: Int) : RecipeFormEvent

    // Ingredientes
    data object OnAddIngredient : RecipeFormEvent
    data class OnIngredientNameChange(val index: Int, val value: String) : RecipeFormEvent
    data class OnIngredientQuantityChange(val index: Int, val value: String) : RecipeFormEvent
    data class OnIngredientUnitChange(val index: Int, val value: String) : RecipeFormEvent
    data class OnRemoveIngredient(val index: Int) : RecipeFormEvent

    // Pasos
    data object OnAddStep : RecipeFormEvent
    data class OnStepChange(val index: Int, val value: String) : RecipeFormEvent
    data class OnRemoveStep(val index: Int) : RecipeFormEvent

    // Guardar
    data object OnSave : RecipeFormEvent
}
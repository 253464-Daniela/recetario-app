package com.daniela.recetario.ui.screens.list

import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.data.model.SortOption

/**
 * Contrato de eventos que la capa de UI envía al ViewModel.
 * UDF: la UI nunca modifica el estado, solo notifica intenciones.
 */
sealed interface RecipeListEvent {

    data class OnQueryChange(val query: String) : RecipeListEvent

    data class OnCategoryToggle(val category: RecipeCategory) : RecipeListEvent

    data class OnDifficultyToggle(val difficulty: Difficulty) : RecipeListEvent

    data class OnMaxTimeChange(val minutes: Int) : RecipeListEvent

    data class OnSortChange(val option: SortOption) : RecipeListEvent

    data class OnFavoriteToggle(val recipeId: String) : RecipeListEvent

    data class OnRecipeDelete(val recipeId: String) : RecipeListEvent

    data object OnToggleOnlyFavorites : RecipeListEvent

    data object OnClearFilters : RecipeListEvent
}
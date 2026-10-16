package com.daniela.recetario.ui.screens.list

import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.data.model.SortOption

/**
 * Estado inmutable de la pantalla de lista.
 * Todos los campos son de solo lectura; se actualiza con `copy()`.
 */
data class RecipeListUiState(
    // Datos originales
    val recipes: List<Recipe> = emptyList(),

    // Resultado del filtrado reactivo
    val filteredRecipes: List<Recipe> = emptyList(),

    // Filtros actuales
    val query: String = "",
    val selectedCategories: Set<RecipeCategory> = emptySet(),
    val selectedDifficulties: Set<Difficulty> = emptySet(),
    val maxTimeMinutes: Int = 180,
    val onlyFavorites: Boolean = false,
    val sortBy: SortOption = SortOption.RECENT,

    // Estados de UI
    val isLoading: Boolean = true,
    val favoriteCount: Int = 0,

    // Derivados
    val hasActiveFilters: Boolean = false,

    val recentlyDeleted: Recipe? = null,
) {
    companion object {
        const val DEFAULT_MAX_TIME = 180
    }
}
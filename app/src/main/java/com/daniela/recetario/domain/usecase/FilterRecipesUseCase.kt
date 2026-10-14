package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.data.model.SortOption

/**
 * Aplica los filtros de la UI sobre una lista de recetas.
 *
 * Variables interdependientes procesadas:
 *  - [query]            : búsqueda por título o nombre de ingrediente
 *  - [categories]       : filtro por categoría (multi-selección)
 *  - [difficulties]     : filtro por dificultad (multi-selección)
 *  - [maxTimeMinutes]   : tiempo máximo de preparación
 *  - [onlyFavorites]    : solo recetas marcadas como favoritas
 *  - [sortBy]           : criterio de ordenamiento
 *
 * La combinación de todos los criterios se realiza con lógica AND.
 */
class FilterRecipesUseCase {

    operator fun invoke(
        recipes: List<Recipe>,
        query: String = "",
        categories: Set<RecipeCategory> = emptySet(),
        difficulties: Set<Difficulty> = emptySet(),
        maxTimeMinutes: Int = Int.MAX_VALUE,
        onlyFavorites: Boolean = false,
        sortBy: SortOption = SortOption.RECENT
    ): List<Recipe> {
        return recipes
            .asSequence()
            .filter { it.matchesQuery(query) }
            .filter { categories.isEmpty() || it.category in categories }
            .filter { difficulties.isEmpty() || it.difficulty in difficulties }
            .filter { it.prepTimeMinutes <= maxTimeMinutes }
            .filter { !onlyFavorites || it.isFavorite }
            .sortedWith(sortBy.toComparator())
            .toList()
    }

    // ---------- Helpers privados ----------

    private fun Recipe.matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        val normalized = query.trim().lowercase()
        return title.lowercase().contains(normalized) ||
                description.lowercase().contains(normalized) ||
                ingredients.any { it.name.lowercase().contains(normalized) }
    }

    private fun SortOption.toComparator(): Comparator<Recipe> = when (this) {
        SortOption.RECENT       -> compareByDescending { it.createdAt }
        SortOption.ALPHABETICAL -> compareBy { it.title.lowercase() }
        SortOption.PREP_TIME    -> compareBy { it.prepTimeMinutes }
        SortOption.DIFFICULTY   -> compareBy { it.difficulty.level }
    }
}
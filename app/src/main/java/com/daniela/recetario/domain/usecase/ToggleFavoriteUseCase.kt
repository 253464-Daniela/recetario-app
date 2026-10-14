package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.repository.RecipesRepository

/**
 * Alterna el estado de favorito de una receta por su identificador.
 */
class ToggleFavoriteUseCase(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(recipeId: String) {
        repository.toggleFavorite(recipeId)
    }
}
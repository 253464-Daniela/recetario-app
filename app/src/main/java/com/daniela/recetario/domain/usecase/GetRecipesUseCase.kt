package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.repository.RecipesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Obtiene el flujo reactivo con todas las recetas disponibles
 * (semilla + creadas por el usuario) desde el repositorio.
 */
class GetRecipesUseCase(
    private val repository: RecipesRepository
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getRecipes()
}
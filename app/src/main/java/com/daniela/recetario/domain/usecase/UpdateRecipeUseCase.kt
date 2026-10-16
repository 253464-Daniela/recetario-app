package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.repository.RecipesRepository

/** Reemplaza una receta existente por su versión actualizada. */
class UpdateRecipeUseCase(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(recipe: Recipe) {
        repository.updateRecipe(recipe)
    }
}
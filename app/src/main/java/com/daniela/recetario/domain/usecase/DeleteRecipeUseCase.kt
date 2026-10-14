package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.repository.RecipesRepository

/**
 * Elimina una receta creada por el usuario.
 * Las recetas semilla no deben eliminarse (validación en el ViewModel).
 */
class DeleteRecipeUseCase(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(recipeId: String) {
        repository.deleteRecipe(recipeId)
    }
}
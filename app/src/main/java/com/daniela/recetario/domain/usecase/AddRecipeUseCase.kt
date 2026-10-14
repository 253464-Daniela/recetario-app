package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.repository.RecipesRepository
import java.util.UUID

/**
 * Registra una nueva receta en el repositorio.
 * Se genera un ID único y se marca como creada por el usuario.
 */
class AddRecipeUseCase(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(recipe: Recipe) {
        val sanitized = recipe.copy(
            id = recipe.id.ifBlank { UUID.randomUUID().toString() },
            isUserCreated = true,
            createdAt = System.currentTimeMillis()
        )
        repository.addRecipe(sanitized)
    }
}
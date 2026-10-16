package com.daniela.recetario.data.repository

import com.daniela.recetario.data.local.preferences.RecipesPreferencesDataSource
import com.daniela.recetario.data.local.seed.SeedRecipesProvider
import com.daniela.recetario.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RecipesRepositoryImpl(
    private val preferences: RecipesPreferencesDataSource,
    private val seedProvider: SeedRecipesProvider
) : RecipesRepository {

    override fun getRecipes(): Flow<List<Recipe>> =
        preferences.recipesFlow.map { stored ->
            if (stored.isEmpty()) {
                val seed = seedProvider.loadSeed()
                // Persistir la semilla en el primer arranque
                preferences.saveRecipes(seed)
                seed
            } else {
                stored
            }
        }

    override suspend fun addRecipe(recipe: Recipe) {
        val current = preferences.recipesFlow.first()
        preferences.saveRecipes(current + recipe)
    }

    override suspend fun toggleFavorite(id: String) {
        val current = preferences.recipesFlow.first()
        val updated = current.map { recipe ->
            if (recipe.id == id) recipe.copy(isFavorite = !recipe.isFavorite) else recipe
        }
        preferences.saveRecipes(updated)
    }

    override suspend fun deleteRecipe(id: String) {
        val current = preferences.recipesFlow.first()
        preferences.saveRecipes(current.filterNot { it.id == id })
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val current = preferences.recipesFlow.first()
        val updated = current.map { existing ->
            if (existing.id == recipe.id) recipe else existing
        }
        preferences.saveRecipes(updated)
    }
}
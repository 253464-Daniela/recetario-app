package com.daniela.recetario.data.repository

import com.daniela.recetario.data.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipes(): Flow<List<Recipe>>
    suspend fun addRecipe(recipe: Recipe)
    suspend fun toggleFavorite(id: String)
    suspend fun deleteRecipe(id: String)
}
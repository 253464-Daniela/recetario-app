package com.daniela.recetario.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val category: RecipeCategory,
    val difficulty: Difficulty,
    val prepTimeMinutes: Int,
    val basePortions: Int,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val isFavorite: Boolean = false,
    val isUserCreated: Boolean = false,
    val createdAt: Long = 0L
) {
    fun scaledToPortions(newPortions: Int): List<Ingredient> {
        if (newPortions <= 0 || basePortions <= 0) return ingredients
        val factor = newPortions.toDouble() / basePortions
        return ingredients.map { it.scaledBy(factor) }
    }
}
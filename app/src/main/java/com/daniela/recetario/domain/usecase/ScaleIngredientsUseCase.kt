package com.daniela.recetario.domain.usecase

import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.Recipe
import kotlin.math.roundToInt

/**
 * Escala los ingredientes de una receta según un número de porciones objetivo.
 *
 * Fórmula aplicada:
 *     cantidad_nueva = cantidad_base * (porciones_nuevas / porciones_base)
 *
 * El resultado se redondea a 2 decimales para evitar valores como 333.3333.
 */
class ScaleIngredientsUseCase {

    operator fun invoke(recipe: Recipe, targetPortions: Int): List<Ingredient> {
        require(targetPortions > 0) { "Las porciones deben ser mayores a cero" }
        if (recipe.basePortions <= 0) return recipe.ingredients

        val factor = targetPortions.toDouble() / recipe.basePortions
        return recipe.ingredients.map { ingredient ->
            ingredient.copy(
                quantity = (ingredient.quantity * factor * 100).roundToInt() / 100.0
            )
        }
    }
}
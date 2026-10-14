package com.daniela.recetario.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: String
) {
    fun scaledBy(factor: Double): Ingredient =
        copy(quantity = (quantity * factor * 100).toLong() / 100.0)
}
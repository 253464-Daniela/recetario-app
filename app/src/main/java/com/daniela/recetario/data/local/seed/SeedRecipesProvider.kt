package com.daniela.recetario.data.local.seed

import android.content.Context
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Recipe
import kotlinx.serialization.json.Json

class SeedRecipesProvider(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadSeed(): List<Recipe> = runCatching {
        val raw = context.resources.openRawResource(R.raw.recipes_seed)
            .bufferedReader()
            .use { it.readText() }
        json.decodeFromString<List<Recipe>>(raw)
    }.getOrElse { emptyList() }
}
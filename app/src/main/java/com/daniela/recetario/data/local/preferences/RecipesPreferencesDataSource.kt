package com.daniela.recetario.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.daniela.recetario.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recipes_prefs")

class RecipesPreferencesDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val recipesKey = stringPreferencesKey("recipes_json")

    val recipesFlow: Flow<List<Recipe>> = context.dataStore.data.map { prefs ->
        val raw = prefs[recipesKey] ?: return@map emptyList()
        runCatching { json.decodeFromString<List<Recipe>>(raw) }.getOrElse { emptyList() }
    }

    suspend fun saveRecipes(recipes: List<Recipe>) {
        context.dataStore.edit { prefs ->
            prefs[recipesKey] = json.encodeToString(recipes)
        }
    }
}
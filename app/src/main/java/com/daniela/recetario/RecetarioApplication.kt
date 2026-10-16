package com.daniela.recetario

import android.app.Application
import com.daniela.recetario.data.local.preferences.RecipesPreferencesDataSource
import com.daniela.recetario.data.local.seed.SeedRecipesProvider
import com.daniela.recetario.data.repository.RecipesRepository
import com.daniela.recetario.data.repository.RecipesRepositoryImpl
import com.daniela.recetario.domain.usecase.AddRecipeUseCase
import com.daniela.recetario.domain.usecase.DeleteRecipeUseCase
import com.daniela.recetario.domain.usecase.FilterRecipesUseCase
import com.daniela.recetario.domain.usecase.GetRecipesUseCase
import com.daniela.recetario.domain.usecase.ScaleIngredientsUseCase
import com.daniela.recetario.domain.usecase.ToggleFavoriteUseCase
import com.daniela.recetario.domain.usecase.UpdateRecipeUseCase


/**
 * Application class que actúa como contenedor de dependencias manual.
 */
class RecetarioApplication : Application() {

    val recipesRepository: RecipesRepository by lazy {
        RecipesRepositoryImpl(
            preferences = RecipesPreferencesDataSource(applicationContext),
            seedProvider = SeedRecipesProvider(applicationContext)
        )
    }

    val getRecipesUseCase: GetRecipesUseCase by lazy { GetRecipesUseCase(recipesRepository) }
    val addRecipeUseCase: AddRecipeUseCase by lazy { AddRecipeUseCase(recipesRepository) }
    val toggleFavoriteUseCase: ToggleFavoriteUseCase by lazy { ToggleFavoriteUseCase(recipesRepository) }
    val deleteRecipeUseCase: DeleteRecipeUseCase by lazy { DeleteRecipeUseCase(recipesRepository) }
    val filterRecipesUseCase: FilterRecipesUseCase by lazy { FilterRecipesUseCase() }
    val scaleIngredientsUseCase: ScaleIngredientsUseCase by lazy { ScaleIngredientsUseCase() }
    val updateRecipeUseCase: UpdateRecipeUseCase by lazy { UpdateRecipeUseCase(recipesRepository) }
}
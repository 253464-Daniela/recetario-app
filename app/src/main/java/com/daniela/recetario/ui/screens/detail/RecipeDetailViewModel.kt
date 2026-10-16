package com.daniela.recetario.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daniela.recetario.RecetarioApplication
import com.daniela.recetario.domain.usecase.GetRecipesUseCase
import com.daniela.recetario.domain.usecase.ScaleIngredientsUseCase
import com.daniela.recetario.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeId: String,
    private val getRecipesUseCase: GetRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val scaleIngredientsUseCase: ScaleIngredientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        observeRecipe()
    }

    /**
     * Observa el Flow del repositorio de forma reactiva.
     * Cada vez que cambie la receta (por ejemplo, tras editarla),
     * este bloque re-ejecuta y actualiza el estado.
     */
    private fun observeRecipe() {
        viewModelScope.launch {
            getRecipesUseCase().collect { recipes ->
                val recipe = recipes.find { it.id == recipeId }

                if (recipe == null) {
                    _uiState.update {
                        it.copy(isLoading = false, notFound = true, recipe = null)
                    }
                    return@collect
                }

                _uiState.update { current ->
                    val portions = if (current.selectedPortions <= 0) {
                        recipe.basePortions
                    } else {
                        current.selectedPortions
                    }

                    current.copy(
                        recipe = recipe,
                        selectedPortions = portions,
                        scaledIngredients = scaleIngredientsUseCase(recipe, portions),
                        isLoading = false,
                        notFound = false
                    )
                }
            }
        }
    }

    fun onEvent(event: RecipeDetailEvent) {
        when (event) {
            is RecipeDetailEvent.OnPortionsChange -> updatePortions(event.portions)
            RecipeDetailEvent.OnToggleFavorite -> toggleFavorite()
        }
    }

    private fun updatePortions(newPortions: Int) {
        val recipe = _uiState.value.recipe ?: return
        val scaled = scaleIngredientsUseCase(recipe, newPortions)
        _uiState.update {
            it.copy(selectedPortions = newPortions, scaledIngredients = scaled)
        }
    }

    private fun toggleFavorite() {
        val currentRecipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentRecipe.id)
        }
    }

    companion object {
        fun factory(
            app: RecetarioApplication,
            recipeId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecipeDetailViewModel(
                    recipeId = recipeId,
                    getRecipesUseCase = app.getRecipesUseCase,
                    toggleFavoriteUseCase = app.toggleFavoriteUseCase,
                    scaleIngredientsUseCase = app.scaleIngredientsUseCase
                ) as T
            }
        }
    }
}
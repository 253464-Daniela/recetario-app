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
import kotlinx.coroutines.flow.first
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
        loadRecipe()
    }

    /** Carga la receta correspondiente al [recipeId] recibido por constructor. */
    private fun loadRecipe() {
        viewModelScope.launch {
            val recipes = getRecipesUseCase().first()
            val recipe = recipes.find { it.id == recipeId }

            if (recipe == null) {
                _uiState.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }

            // Estado inicial: porciones base de la receta
            _uiState.update {
                it.copy(
                    recipe = recipe,
                    selectedPortions = recipe.basePortions,
                    scaledIngredients = recipe.ingredients,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: RecipeDetailEvent) {
        when (event) {
            is RecipeDetailEvent.OnPortionsChange -> updatePortions(event.portions)
            RecipeDetailEvent.OnToggleFavorite -> toggleFavorite()
        }
    }

    /**
     * Recalcula los ingredientes cuando cambia el slider.
     * Fórmula: cantidad_nueva = cantidad_base * (porciones_nuevas / porciones_base)
     */
    private fun updatePortions(newPortions: Int) {
        val recipe = _uiState.value.recipe ?: return
        val scaled = scaleIngredientsUseCase(recipe, newPortions)

        _uiState.update {
            it.copy(
                selectedPortions = newPortions,
                scaledIngredients = scaled
            )
        }
    }

    private fun toggleFavorite() {
        val currentRecipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentRecipe.id)
            // Refrescamos el estado local optimistamente
            _uiState.update {
                it.copy(
                    recipe = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
                )
            }
        }
    }

    // ---------- Factory manual ----------
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
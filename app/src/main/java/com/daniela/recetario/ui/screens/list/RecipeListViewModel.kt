package com.daniela.recetario.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.domain.usecase.AddRecipeUseCase
import com.daniela.recetario.domain.usecase.DeleteRecipeUseCase
import com.daniela.recetario.domain.usecase.FilterRecipesUseCase
import com.daniela.recetario.domain.usecase.GetRecipesUseCase
import com.daniela.recetario.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val filterRecipesUseCase: FilterRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val addRecipeUseCase: AddRecipeUseCase
) : ViewModel() {

    // Backing property: mutable por dentro, inmutable por fuera
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        observeRecipes()
    }

    /** Se suscribe al Flow del repositorio y reacciona a cada emisión. */
    private fun observeRecipes() {
        viewModelScope.launch {
            getRecipesUseCase()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    // Aquí podrías emitir un snackbar con error.message
                }
                .onEach { recipes ->
                    _uiState.update { current ->
                        current.copy(
                            recipes = recipes,
                            isLoading = false,
                            favoriteCount = recipes.count { it.isFavorite }
                        )
                    }
                    applyFilters() // recalcula filteredRecipes tras cada emisión
                }
                .collect { /* la lógica está en onEach */ }
        }
    }

    /** Punto de entrada único de la UI. */
    fun onEvent(event: RecipeListEvent) {
        when (event) {
            is RecipeListEvent.OnQueryChange -> {
                _uiState.update { it.copy(query = event.query) }
                applyFilters()
            }

            is RecipeListEvent.OnCategoryToggle -> {
                _uiState.update { current ->
                    val updated = if (event.category in current.selectedCategories) {
                        current.selectedCategories - event.category
                    } else {
                        current.selectedCategories + event.category
                    }
                    current.copy(selectedCategories = updated)
                }
                applyFilters()
            }

            is RecipeListEvent.OnDifficultyToggle -> {
                _uiState.update { current ->
                    val updated = if (event.difficulty in current.selectedDifficulties) {
                        current.selectedDifficulties - event.difficulty
                    } else {
                        current.selectedDifficulties + event.difficulty
                    }
                    current.copy(selectedDifficulties = updated)
                }
                applyFilters()
            }

            is RecipeListEvent.OnMaxTimeChange -> {
                _uiState.update { it.copy(maxTimeMinutes = event.minutes) }
                applyFilters()
            }

            is RecipeListEvent.OnSortChange -> {
                _uiState.update { it.copy(sortBy = event.option) }
                applyFilters()
            }

            RecipeListEvent.OnToggleOnlyFavorites -> {
                _uiState.update { it.copy(onlyFavorites = !it.onlyFavorites) }
                applyFilters()
            }

            is RecipeListEvent.OnFavoriteToggle -> toggleFavorite(event.recipeId)

            is RecipeListEvent.OnRecipeDelete -> deleteRecipeWithUndo(event.recipeId)

            RecipeListEvent.OnUndoDelete -> undoDelete()

            RecipeListEvent.OnConfirmDelete -> {
                _uiState.update { it.copy(recentlyDeleted = null) }
            }


            RecipeListEvent.OnClearFilters -> clearFilters()
        }
    }

    private fun applyFilters() {
        val current = _uiState.value
        val filtered = filterRecipesUseCase(
            recipes = current.recipes,
            query = current.query,
            categories = current.selectedCategories,
            difficulties = current.selectedDifficulties,
            maxTimeMinutes = current.maxTimeMinutes,
            onlyFavorites = current.onlyFavorites,
            sortBy = current.sortBy
        )
        _uiState.update {
            it.copy(
                filteredRecipes = filtered,
                hasActiveFilters = it.query.isNotBlank() ||
                        it.selectedCategories.isNotEmpty() ||
                        it.selectedDifficulties.isNotEmpty() ||
                        it.onlyFavorites ||
                        it.maxTimeMinutes != RecipeListUiState.DEFAULT_MAX_TIME
            )
        }
    }

    private fun toggleFavorite(recipeId: String) {
        viewModelScope.launch { toggleFavoriteUseCase(recipeId) }
    }

    private fun deleteRecipeWithUndo(recipeId: String) {
        val recipe = _uiState.value.recipes.find { it.id == recipeId } ?: return
        // Regla de negocio: solo se eliminan recetas creadas por el usuario
        if (!recipe.isUserCreated) return

        viewModelScope.launch {
            deleteRecipeUseCase(recipeId)
            _uiState.update { it.copy(recentlyDeleted = recipe) }
        }
    }

    private fun undoDelete() {
        val recipe = _uiState.value.recentlyDeleted ?: return
        viewModelScope.launch {
            addRecipeUseCase(
                recipe.copy(id = java.util.UUID.randomUUID().toString())
            )
            _uiState.update { it.copy(recentlyDeleted = null) }
        }
    }

    private fun clearFilters() {
        _uiState.update {
            it.copy(
                query = "",
                selectedCategories = emptySet(),
                selectedDifficulties = emptySet(),
                maxTimeMinutes = RecipeListUiState.DEFAULT_MAX_TIME,
                onlyFavorites = false,
                sortBy = com.daniela.recetario.data.model.SortOption.RECENT
            )
        }
        applyFilters()
    }

    // ---------- Factory manual ----------
    companion object {
        fun factory(app: com.daniela.recetario.RecetarioApplication): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipeListViewModel(
                        getRecipesUseCase = app.getRecipesUseCase,
                        filterRecipesUseCase = app.filterRecipesUseCase,
                        toggleFavoriteUseCase = app.toggleFavoriteUseCase,
                        deleteRecipeUseCase = app.deleteRecipeUseCase,
                        addRecipeUseCase = app.addRecipeUseCase
                    ) as T
                }
            }
    }
}
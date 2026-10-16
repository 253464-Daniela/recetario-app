package com.daniela.recetario.ui.screens.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daniela.recetario.R
import com.daniela.recetario.RecetarioApplication
import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.domain.usecase.AddRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class RecipeFormViewModel(
    private val addRecipeUseCase: AddRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    // ============================================================
    // Punto único de entrada de eventos (UDF)
    // ============================================================
    fun onEvent(event: RecipeFormEvent) {
        when (event) {
            is RecipeFormEvent.OnTitleChange -> updateTitle(event.value)
            is RecipeFormEvent.OnDescriptionChange -> updateDescription(event.value)
            is RecipeFormEvent.OnCategoryChange -> updateCategory(event)
            is RecipeFormEvent.OnDifficultyChange -> updateDifficulty(event)
            is RecipeFormEvent.OnPrepTimeChange -> updatePrepTime(event.value)
            is RecipeFormEvent.OnBasePortionsChange -> updateBasePortions(event.portions)

            RecipeFormEvent.OnAddIngredient -> addIngredient()
            is RecipeFormEvent.OnIngredientNameChange -> updateIngredientName(event.index, event.value)
            is RecipeFormEvent.OnIngredientQuantityChange -> updateIngredientQuantity(event.index, event.value)
            is RecipeFormEvent.OnIngredientUnitChange -> updateIngredientUnit(event.index, event.value)
            is RecipeFormEvent.OnRemoveIngredient -> removeIngredient(event.index)

            RecipeFormEvent.OnAddStep -> addStep()
            is RecipeFormEvent.OnStepChange -> updateStep(event.index, event.value)
            is RecipeFormEvent.OnRemoveStep -> removeStep(event.index)

            RecipeFormEvent.OnSave -> saveRecipe()
        }
    }

    // ============================================================
    // Actualizaciones de campos (cada una revalida)
    // ============================================================
    private fun updateTitle(value: String) = _uiState.update {
        it.copy(title = value).revalidated()
    }

    private fun updateDescription(value: String) = _uiState.update {
        it.copy(description = value)
    }

    private fun updateCategory(event: RecipeFormEvent.OnCategoryChange) = _uiState.update {
        it.copy(category = event.category)
    }

    private fun updateDifficulty(event: RecipeFormEvent.OnDifficultyChange) = _uiState.update {
        it.copy(difficulty = event.difficulty)
    }

    private fun updatePrepTime(value: String) = _uiState.update {
        // Solo aceptamos dígitos
        val sanitized = value.filter { ch -> ch.isDigit() }
        it.copy(prepTime = sanitized).revalidated()
    }

    private fun updateBasePortions(portions: Int) = _uiState.update {
        it.copy(basePortions = portions.coerceAtLeast(1))
    }

    // ============================================================
    // Ingredientes (lista dinámica)
    // ============================================================
    private fun addIngredient() = _uiState.update {
        it.copy(ingredients = it.ingredients + Ingredient(name = "", quantity = 0.0, unit = "g"))
            .revalidated()
    }

    private fun updateIngredientName(index: Int, value: String) = _uiState.update { state ->
        state.copy(ingredients = state.ingredients.mapIndexed { i, ing ->
            if (i == index) ing.copy(name = value) else ing
        }).revalidated()
    }

    private fun updateIngredientQuantity(index: Int, value: String) = _uiState.update { state ->
        val sanitized = value.replace(",", ".").filter { it.isDigit() || it == '.' }
        val quantity = sanitized.toDoubleOrNull() ?: 0.0
        state.copy(ingredients = state.ingredients.mapIndexed { i, ing ->
            if (i == index) ing.copy(quantity = quantity) else ing
        }).revalidated()
    }

    private fun updateIngredientUnit(index: Int, value: String) = _uiState.update { state ->
        state.copy(ingredients = state.ingredients.mapIndexed { i, ing ->
            if (i == index) ing.copy(unit = value) else ing
        }).revalidated()
    }

    private fun removeIngredient(index: Int) = _uiState.update { state ->
        state.copy(ingredients = state.ingredients.filterIndexed { i, _ -> i != index })
            .revalidated()
    }

    // ============================================================
    // Pasos (lista dinámica)
    // ============================================================
    private fun addStep() = _uiState.update {
        it.copy(steps = it.steps + "").revalidated()
    }

    private fun updateStep(index: Int, value: String) = _uiState.update { state ->
        state.copy(steps = state.steps.mapIndexed { i, s -> if (i == index) value else s })
            .revalidated()
    }

    private fun removeStep(index: Int) = _uiState.update { state ->
        state.copy(steps = state.steps.filterIndexed { i, _ -> i != index }).revalidated()
    }

    // ============================================================
    // Guardar
    // ============================================================
    private fun saveRecipe() {
        val state = _uiState.value
        if (!state.isValid) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val recipe = Recipe(
                id = UUID.randomUUID().toString(),
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category,
                difficulty = state.difficulty,
                prepTimeMinutes = state.prepTime.toIntOrNull() ?: 0,
                basePortions = state.basePortions,
                ingredients = state.ingredients,
                steps = state.steps.filter { it.isNotBlank() },
                isFavorite = false,
                isUserCreated = true
            )
            addRecipeUseCase(recipe)
            _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
        }
    }

    // ============================================================
    // Validación central (regla de negocio: vive en el ViewModel)
    // ============================================================
    private fun RecipeFormUiState.revalidated(): RecipeFormUiState {
        val titleErr = when {
            title.isBlank() -> R.string.form_error_title_required
            title.length !in 3..60 -> R.string.form_error_title_length
            else -> null
        }
        val timeErr = if ((prepTime.toIntOrNull() ?: 0) <= 0) R.string.form_error_time else null
        val ingredientsErr = if (ingredients.none { it.name.isNotBlank() && it.quantity > 0.0 }) {
            R.string.form_error_ingredients
        } else null
        val stepsErr = if (steps.none { it.isNotBlank() }) R.string.form_error_steps else null

        val valid = titleErr == null && timeErr == null &&
                ingredientsErr == null && stepsErr == null

        return copy(
            titleError = titleErr,
            prepTimeError = timeErr,
            ingredientsError = ingredientsErr,
            stepsError = stepsErr,
            isValid = valid
        )
    }

    // ============================================================
    // Factory manual
    // ============================================================
    companion object {
        fun factory(app: RecetarioApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipeFormViewModel(addRecipeUseCase = app.addRecipeUseCase) as T
                }
            }
    }
}
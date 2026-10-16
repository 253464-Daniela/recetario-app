package com.daniela.recetario.ui.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.ui.components.localizedName
import com.daniela.recetario.ui.theme.RecetarioTheme
import com.daniela.recetario.ui.util.recetarioApp

@Composable
fun RecipeFormRoute(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val app = recetarioApp()
    val viewModel: RecipeFormViewModel = viewModel(
        factory = RecipeFormViewModel.factory(app)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Reacciona al guardado exitoso
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) onSaved()
    }

    RecipeFormScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    uiState: RecipeFormUiState,
    onEvent: (RecipeFormEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.form_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.form_close_cd)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Título ----------
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { onEvent(RecipeFormEvent.OnTitleChange(it)) },
                    label = { Text(stringResource(R.string.form_field_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { res ->
                        { Text(stringResource(res)) }
                    },
                    singleLine = true
                )
            }

            // ---------- Descripción ----------
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { onEvent(RecipeFormEvent.OnDescriptionChange(it)) },
                    label = { Text(stringResource(R.string.form_field_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            // ---------- Categoría ----------
            item {
                SectionLabel(stringResource(R.string.form_section_category))
                CategorySingleChoice(
                    selected = uiState.category,
                    onSelect = { onEvent(RecipeFormEvent.OnCategoryChange(it)) }
                )
            }

            // ---------- Dificultad ----------
            item {
                SectionLabel(stringResource(R.string.form_section_difficulty))
                DifficultySingleChoice(
                    selected = uiState.difficulty,
                    onSelect = { onEvent(RecipeFormEvent.OnDifficultyChange(it)) }
                )
            }

            // ---------- Tiempo y porciones ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.prepTime,
                        onValueChange = { onEvent(RecipeFormEvent.OnPrepTimeChange(it)) },
                        label = { Text(stringResource(R.string.form_field_prep_time)) },
                        modifier = Modifier.weight(1f),
                        isError = uiState.prepTimeError != null,
                        supportingText = uiState.prepTimeError?.let { res ->
                            { Text(stringResource(res)) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.form_field_portions) +
                                    ": ${uiState.basePortions}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Slider(
                            value = uiState.basePortions.toFloat(),
                            onValueChange = {
                                onEvent(RecipeFormEvent.OnBasePortionsChange(it.toInt()))
                            },
                            valueRange = 1f..12f,
                            steps = 10
                        )
                    }
                }
            }

            // ---------- Ingredientes ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(stringResource(R.string.form_section_ingredients))
                    TextButton(onClick = { onEvent(RecipeFormEvent.OnAddIngredient) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.form_add_ingredient))
                    }
                }
                if (uiState.ingredientsError != null) {
                    Text(
                        text = stringResource(uiState.ingredientsError),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            itemsIndexed(
                items = uiState.ingredients,
                key = { index: Int, _: Ingredient -> "ing_$index" }
            ) { index, ingredient ->
                IngredientFormRow(
                    ingredient = ingredient,
                    onNameChange = { onEvent(RecipeFormEvent.OnIngredientNameChange(index, it)) },
                    onQuantityChange = { onEvent(RecipeFormEvent.OnIngredientQuantityChange(index, it)) },
                    onUnitChange = { onEvent(RecipeFormEvent.OnIngredientUnitChange(index, it)) },
                    onRemove = { onEvent(RecipeFormEvent.OnRemoveIngredient(index)) }
                )
            }

            // ---------- Pasos ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(stringResource(R.string.form_section_steps))
                    TextButton(onClick = { onEvent(RecipeFormEvent.OnAddStep) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.form_add_step))
                    }
                }
                if (uiState.stepsError != null) {
                    Text(
                        text = stringResource(uiState.stepsError),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            itemsIndexed(
                items = uiState.steps,
                key = { index: Int, _: String -> "step_$index" }
            ) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = step,
                        onValueChange = { onEvent(RecipeFormEvent.OnStepChange(index, it)) },
                        label = { Text("${index + 1}. ${stringResource(R.string.form_step_hint)}") },
                        modifier = Modifier.weight(1f),
                        minLines = 2
                    )
                    IconButton(onClick = { onEvent(RecipeFormEvent.OnRemoveStep(index)) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.form_delete_step_cd)
                        )
                    }
                }
            }

            // ---------- Botón guardar ----------
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onEvent(RecipeFormEvent.OnSave) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isValid && !uiState.isSaving
                ) {
                    Text(stringResource(R.string.form_save))
                }
            }
        }
    }
}

// ============================================================
// Sub-componentes locales (stateless)
// ============================================================

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun CategorySingleChoice(
    selected: RecipeCategory,
    onSelect: (RecipeCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RecipeCategory.entries.take(3).forEach { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = { Text(category.localizedName()) }
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RecipeCategory.entries.drop(3).forEach { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = { Text(category.localizedName()) }
            )
        }
    }
}

@Composable
private fun DifficultySingleChoice(
    selected: Difficulty,
    onSelect: (Difficulty) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            FilterChip(
                selected = difficulty == selected,
                onClick = { onSelect(difficulty) },
                label = { Text(difficulty.localizedName()) }
            )
        }
    }
}

@Composable
private fun IngredientFormRow(
    ingredient: Ingredient,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.form_ingredient_name_hint)) },
            modifier = Modifier.weight(2f),
            singleLine = true
        )
        OutlinedTextField(
            value = if (ingredient.quantity == 0.0) "" else ingredient.quantity.toString(),
            onValueChange = onQuantityChange,
            label = { Text(stringResource(R.string.form_ingredient_quantity_hint)) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = onUnitChange,
            label = { Text(stringResource(R.string.form_ingredient_unit_hint)) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.form_delete_ingredient_cd)
            )
        }
    }
}

// ============================================================
// Preview
// ============================================================
@Preview(showBackground = true)
@Composable
private fun RecipeFormScreenPreview() {
    RecetarioTheme {
        RecipeFormScreen(
            uiState = RecipeFormUiState(
                title = "Tacos al pastor",
                description = "Clásico mexicano",
                prepTime = "45",
                basePortions = 4,
                ingredients = listOf(
                    Ingredient("Carne", 500.0, "g"),
                    Ingredient("Tortillas", 12.0, "pza")
                ),
                steps = listOf("Marinar la carne", "Cocinar a fuego alto"),
                isValid = true
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
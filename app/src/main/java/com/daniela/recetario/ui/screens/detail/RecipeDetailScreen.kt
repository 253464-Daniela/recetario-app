package com.daniela.recetario.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.ui.components.DifficultyBadge
import com.daniela.recetario.ui.components.localizedName
import com.daniela.recetario.ui.theme.FavoriteRed
import com.daniela.recetario.ui.theme.RecetarioTheme
import com.daniela.recetario.ui.util.recetarioApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailRoute(
    recipeId: String,
    onBack: () -> Unit
) {
    val app = recetarioApp()
    val viewModel: RecipeDetailViewModel = viewModel(
        key = "detail_$recipeId",
        factory = RecipeDetailViewModel.factory(app, recipeId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecipeDetailScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    uiState: RecipeDetailUiState,
    onEvent: (RecipeDetailEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.recipe?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back_cd)
                        )
                    }
                },
                actions = {
                    val recipe = uiState.recipe
                    if (recipe != null) {
                        IconButton(onClick = { onEvent(RecipeDetailEvent.OnToggleFavorite) }) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (recipe.isFavorite) FavoriteRed
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> LoadingBox(Modifier.padding(innerPadding))

            uiState.notFound || uiState.recipe == null -> NotFoundBox(Modifier.padding(innerPadding))

            else -> RecipeDetailContent(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun RecipeDetailContent(
    uiState: RecipeDetailUiState,
    onEvent: (RecipeDetailEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val recipe = uiState.recipe ?: return

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---------- Header con metadata ----------
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.category.localizedName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    DifficultyBadge(difficulty = recipe.difficulty)
                    Text(
                        text = stringResource(R.string.list_prep_time, recipe.prepTimeMinutes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ---------- Descripción ----------
        if (recipe.description.isNotBlank()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionTitle(text = stringResource(R.string.detail_description))
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        item { HorizontalDivider() }

        // ---------- Slider de porciones ----------
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.detail_portions, uiState.selectedPortions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = uiState.selectedPortions.toFloat(),
                    onValueChange = {
                        onEvent(RecipeDetailEvent.OnPortionsChange(it.toInt()))
                    },
                    valueRange = 1f..12f,
                    steps = 10
                )
            }
        }

        // ---------- Ingredientes escalados ----------
        item {
            SectionTitle(text = stringResource(R.string.detail_ingredients))
        }

        itemsIndexed(
            items = uiState.scaledIngredients,
            key = { index: Int, ingredient: Ingredient -> "${recipe.id}_${ingredient.name}" }
        ) { _, ingredient ->
            IngredientRow(ingredient = ingredient)
        }

        item { Spacer(Modifier.height(8.dp)) }
        item { HorizontalDivider() }

        // ---------- Pasos ----------
        item {
            SectionTitle(text = stringResource(R.string.detail_steps))
        }

        itemsIndexed(recipe.steps) { index, step ->
            StepRow(index = index + 1, text = step)
        }
    }
}

// ============================================================
// Sub-componentes stateless locales
// ============================================================

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "• ${ingredient.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${ingredient.quantity} ${ingredient.unit}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StepRow(index: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = index.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

@Composable
private fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NotFoundBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Receta no encontrada")
    }
}

// ============================================================
// PREVIEW
// ============================================================
@Preview(showBackground = true)
@Composable
private fun RecipeDetailScreenPreview() {
    val recipe = Recipe(
        id = "1",
        title = "Tacos al pastor",
        description = "Clásico mexicano con piña y adobo marinado por 12 horas.",
        category = RecipeCategory.PLATO_FUERTE,
        difficulty = Difficulty.MEDIA,
        prepTimeMinutes = 45,
        basePortions = 4,
        ingredients = listOf(
            Ingredient("Carne de cerdo", 500.0, "g"),
            Ingredient("Tortillas", 12.0, "pza"),
            Ingredient("Piña", 1.0, "taza")
        ),
        steps = listOf(
            "Marinar la carne con adobo durante 12 horas.",
            "Cocinar a fuego alto en el trompo o sartén.",
            "Servir sobre tortillas calientes."
        ),
        isFavorite = true
    )
    RecetarioTheme {
        RecipeDetailScreen(
            uiState = RecipeDetailUiState(
                recipe = recipe,
                selectedPortions = 6,
                scaledIngredients = listOf(
                    Ingredient("Carne de cerdo", 750.0, "g"),
                    Ingredient("Tortillas", 18.0, "pza"),
                    Ingredient("Piña", 1.5, "taza")
                ),
                isLoading = false
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
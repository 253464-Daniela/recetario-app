package com.daniela.recetario.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.data.model.Ingredient
import com.daniela.recetario.data.model.Recipe
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.data.model.SortOption
import com.daniela.recetario.ui.components.CategoryChipsRow
import com.daniela.recetario.ui.components.DifficultyChipsRow
import com.daniela.recetario.ui.components.EmptyState
import com.daniela.recetario.ui.components.LoadingState
import com.daniela.recetario.ui.components.RecipeCard
import com.daniela.recetario.ui.components.RecipeSearchBar
import com.daniela.recetario.ui.theme.RecetarioTheme
import com.daniela.recetario.ui.util.recetarioApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListRoute(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToForm: () -> Unit
) {
    val app = recetarioApp()
    val viewModel: RecipeListViewModel = viewModel(
        factory = RecipeListViewModel.factory(app)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecipeListScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToForm = onNavigateToForm
    )
}

/**
 * Pantalla principal. 100% stateless con respecto a la lógica:
 * recibe [uiState] y notifica con [onEvent]. No conoce al ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    uiState: RecipeListUiState,
    onEvent: (RecipeListEvent) -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToForm: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.list_title)) },
                actions = {
                    BadgedBox(
                        badge = {
                            if (uiState.favoriteCount > 0) {
                                Badge { Text(text = uiState.favoriteCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.list_favorites_badge, uiState.favoriteCount)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.list_fab_new))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            RecipeSearchBar(
                query = uiState.query,
                onQueryChange = { onEvent(RecipeListEvent.OnQueryChange(it)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            CategoryChipsRow(
                categories = RecipeCategory.entries,
                selected = uiState.selectedCategories,
                onToggle = { onEvent(RecipeListEvent.OnCategoryToggle(it)) }
            )

            DifficultyChipsRow(
                selected = uiState.selectedDifficulties,
                onToggle = { onEvent(RecipeListEvent.OnDifficultyToggle(it)) },
                modifier = Modifier.padding(top = 4.dp)
            )

            // Slider de tiempo máximo
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = stringResource(R.string.list_max_time, uiState.maxTimeMinutes),
                    style = MaterialTheme.typography.labelSmall
                )
                Slider(
                    value = uiState.maxTimeMinutes.toFloat(),
                    onValueChange = { onEvent(RecipeListEvent.OnMaxTimeChange(it.toInt())) },
                    valueRange = 5f..180f,
                    steps = 34
                )
            }

            // Fila de "Solo favoritos"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.list_only_favorites),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = uiState.onlyFavorites,
                    onCheckedChange = { onEvent(RecipeListEvent.OnToggleOnlyFavorites) }
                )
            }

            // Contenido principal
            when {
                uiState.isLoading -> LoadingState()

                uiState.filteredRecipes.isEmpty() -> EmptyState(
                    onClearFilters = { onEvent(RecipeListEvent.OnClearFilters) }
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items = uiState.filteredRecipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToDetail(recipe.id) },
                            onFavoriteClick = { onEvent(RecipeListEvent.OnFavoriteToggle(recipe.id)) }
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// PREVIEWS (con datos falsos, sin ViewModel)
// ============================================================
@Preview(showBackground = true)
@Composable
private fun RecipeListScreenPreview() {
    val fakeRecipes = listOf(
        Recipe(
            id = "1", title = "Tacos al pastor",
            description = "Clásico mexicano con piña y adobo",
            category = RecipeCategory.PLATO_FUERTE,
            difficulty = Difficulty.MEDIA, prepTimeMinutes = 45,
            basePortions = 4,
            ingredients = listOf(Ingredient("Carne", 500.0, "g")),
            steps = listOf("Marinar"), isFavorite = true
        ),
        Recipe(
            id = "2", title = "Ensalada César",
            description = "Fresca con aderezo cremoso",
            category = RecipeCategory.ENSALADA,
            difficulty = Difficulty.FACIL, prepTimeMinutes = 15,
            basePortions = 2,
            ingredients = listOf(Ingredient("Lechuga", 1.0, "pza")),
            steps = listOf("Mezclar")
        )
    )
    RecetarioTheme {
        RecipeListScreen(
            uiState = RecipeListUiState(
                recipes = fakeRecipes,
                filteredRecipes = fakeRecipes,
                isLoading = false,
                favoriteCount = 1
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeListScreenEmptyPreview() {
    RecetarioTheme {
        RecipeListScreen(
            uiState = RecipeListUiState(isLoading = false, filteredRecipes = emptyList()),
            onEvent = {}
        )
    }
}
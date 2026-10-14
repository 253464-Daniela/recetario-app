package com.daniela.recetario.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.ui.theme.RecetarioTheme

/**
 * Fila horizontal scrolleable de chips de categoría.
 * 100% stateless: recibe el estado y notifica eventos.
 */
@Composable
fun CategoryChipsRow(
    categories: List<RecipeCategory>,
    selected: Set<RecipeCategory>,
    onToggle: (RecipeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = categories, key = { it.name }) { category ->
            CategoryChip(
                category = category,
                selected = category in selected,
                onClick = { onToggle(category) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipsRowPreview() {
    RecetarioTheme {
        CategoryChipsRow(
            categories = RecipeCategory.entries,
            selected = setOf(RecipeCategory.POSTRE),
            onToggle = {}
        )
    }
}
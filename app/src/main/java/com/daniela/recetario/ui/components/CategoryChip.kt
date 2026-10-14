package com.daniela.recetario.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.daniela.recetario.R
import com.daniela.recetario.data.model.RecipeCategory
import com.daniela.recetario.ui.theme.RecetarioTheme

/**
 * Chip individual de categoría.
 * Stateless: recibe [selected] y notifica con [onClick].
 */
@Composable
fun CategoryChip(
    category: RecipeCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = category.localizedName()) },
        modifier = modifier
    )
}

@Composable
internal fun RecipeCategory.localizedName(): String = stringResource(
    when (this) {
        RecipeCategory.ENTRADA -> R.string.category_entrada
        RecipeCategory.PLATO_FUERTE -> R.string.category_plato_fuerte
        RecipeCategory.POSTRE -> R.string.category_postre
        RecipeCategory.BEBIDA -> R.string.category_bebida
        RecipeCategory.SOPA -> R.string.category_sopa
        RecipeCategory.ENSALADA -> R.string.category_ensalada
    }
)

@Preview(showBackground = true)
@Composable
private fun CategoryChipPreview() {
    RecetarioTheme {
        CategoryChip(
            category = RecipeCategory.POSTRE,
            selected = true,
            onClick = {}
        )
    }
}
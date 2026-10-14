package com.daniela.recetario.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.ui.theme.RecetarioTheme

@Composable
fun DifficultyChipsRow(
    selected: Set<Difficulty>,
    onToggle: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = Difficulty.entries, key = { it.name }) { difficulty ->
            FilterChip(
                selected = difficulty in selected,
                onClick = { onToggle(difficulty) },
                label = { Text(text = difficulty.localizedName()) }
            )
        }
    }
}

@Composable
internal fun Difficulty.localizedName(): String = stringResource(
    when (this) {
        Difficulty.FACIL -> R.string.difficulty_facil
        Difficulty.MEDIA -> R.string.difficulty_media
        Difficulty.DIFICIL -> R.string.difficulty_dificil
    }
)

@Preview(showBackground = true)
@Composable
private fun DifficultyChipsRowPreview() {
    RecetarioTheme {
        DifficultyChipsRow(selected = setOf(Difficulty.FACIL), onToggle = {})
    }
}
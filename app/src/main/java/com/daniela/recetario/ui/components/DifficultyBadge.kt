package com.daniela.recetario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniela.recetario.R
import com.daniela.recetario.data.model.Difficulty
import com.daniela.recetario.ui.theme.DifficultyEasy
import com.daniela.recetario.ui.theme.DifficultyHard
import com.daniela.recetario.ui.theme.DifficultyMedium
import com.daniela.recetario.ui.theme.RecetarioTheme

/**
 * Chip de dificultad con color según el nivel.
 * Componente 100% stateless.
 */
@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
) {
    val (color, label) = when (difficulty) {
        Difficulty.FACIL -> DifficultyEasy to stringResource(R.string.difficulty_facil)
        Difficulty.MEDIA -> DifficultyMedium to stringResource(R.string.difficulty_media)
        Difficulty.DIFICIL -> DifficultyHard to stringResource(R.string.difficulty_dificil)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(contentPadding)
    )
}

@Preview(showBackground = true)
@Composable
private fun DifficultyBadgePreview() {
    RecetarioTheme {
        DifficultyBadge(difficulty = Difficulty.MEDIA)
    }
}
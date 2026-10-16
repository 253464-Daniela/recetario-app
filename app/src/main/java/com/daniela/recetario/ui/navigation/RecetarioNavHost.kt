package com.daniela.recetario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daniela.recetario.ui.screens.detail.RecipeDetailRoute
import com.daniela.recetario.ui.screens.form.RecipeFormRoute
import com.daniela.recetario.ui.screens.list.RecipeListRoute

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{recipeId}"
    const val FORM = "form"

    fun detail(recipeId: String) = "detail/$recipeId"
}

@Composable
fun RecetarioNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(Routes.LIST) {
            RecipeListRoute(
                onNavigateToDetail = { recipeId ->
                    navController.navigate(Routes.detail(recipeId))
                },
                onNavigateToForm = {
                    navController.navigate(Routes.FORM)
                }
            )
        }

        composable(Routes.DETAIL) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId").orEmpty()
            RecipeDetailRoute(
                recipeId = recipeId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FORM) {
            RecipeFormRoute(
                onBack = { navController.popBackStack() },
                onSaved = {
                    // Vuelve a la lista tras guardar
                    navController.popBackStack()
                }
            )
        }
    }
}
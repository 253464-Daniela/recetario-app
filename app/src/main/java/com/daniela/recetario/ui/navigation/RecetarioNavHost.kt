package com.daniela.recetario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daniela.recetario.ui.screens.detail.RecipeDetailRoute
import com.daniela.recetario.ui.screens.form.RecipeFormRoute
import com.daniela.recetario.ui.screens.list.RecipeListRoute

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{recipeId}"
    const val FORM = "form?recipeId={recipeId}"

    fun detail(recipeId: String) = "detail/$recipeId"
    fun formNew() = "form"                           // sin id = crear
    fun formEdit(recipeId: String) = "form?recipeId=$recipeId"
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
                onNavigateToDetail = { id -> navController.navigate(Routes.detail(id)) },
                onNavigateToForm = { navController.navigate(Routes.formNew()) }
            )
        }

        composable(Routes.DETAIL) { entry ->
            val recipeId = entry.arguments?.getString("recipeId").orEmpty()
            RecipeDetailRoute(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.formEdit(id)) }
            )
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val recipeId = entry.arguments?.getString("recipeId")
            RecipeFormRoute(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
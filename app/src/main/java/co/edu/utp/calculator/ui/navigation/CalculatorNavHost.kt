package co.edu.utp.calculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.utp.calculator.ui.calculator.CalculatorRoute
import co.edu.utp.calculator.ui.calculator.CalculatorViewModel
import co.edu.utp.calculator.ui.history.HistoryRoute

private object Destinations {
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
}

@Composable
fun CalculatorNavHost(
    navController: NavHostController = rememberNavController(),
    calculatorViewModel: CalculatorViewModel = viewModel(),
) {
    NavHost(navController = navController, startDestination = Destinations.CALCULATOR) {
        composable(Destinations.CALCULATOR) {
            CalculatorRoute(
                viewModel = calculatorViewModel,
                onNavigateToHistory = {
                    navController.navigate(Destinations.HISTORY) { launchSingleTop = true }
                },
            )
        }
        composable(Destinations.HISTORY) {
            HistoryRoute(
                viewModel = calculatorViewModel,
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }
}

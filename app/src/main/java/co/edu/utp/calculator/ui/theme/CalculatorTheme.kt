package co.edu.utp.calculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import co.edu.utp.calculator.R

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    val background = colorResource(R.color.calculator_background)
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = colorResource(R.color.operator_key_background),
            background = background,
            surface = background,
            onBackground = colorResource(R.color.text_primary),
            onSurface = colorResource(R.color.text_primary),
        ),
        content = content,
    )
}

package co.edu.utp.calculator.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.utp.calculator.R
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.ui.calculator.components.CalculatorDisplay
import co.edu.utp.calculator.ui.calculator.components.CalculatorKeypad
import co.edu.utp.calculator.ui.calculator.components.CalculatorKeypadLayout
import co.edu.utp.calculator.ui.calculator.components.KeySpacing
import co.edu.utp.calculator.ui.theme.CalculatorTheme

private const val KEYPAD_HEIGHT_FRACTION = 0.7f

@Composable
fun CalculatorRoute(
    viewModel: CalculatorViewModel,
    onNavigateToHistory: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorScreen(
        uiState = uiState,
        onInput = viewModel::onInput,
        onNavigateToHistory = onNavigateToHistory,
    )
}

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onInput: (CalculatorInput) -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.calculator_background))
            .safeDrawingPadding()
            .padding(16.dp),
    ) {
        val buttonSize = calculateButtonSize(maxWidth, maxHeight)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HistoryShortcut(
                onClick = onNavigateToHistory,
                modifier = Modifier.align(Alignment.Start),
            )
            CalculatorDisplay(
                display = uiState.display,
                pendingLeftOperand = uiState.pendingLeftOperand,
                pendingOperation = uiState.pendingOperation,
                error = uiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Spacer(modifier = Modifier.height(KeySpacing))
            CalculatorKeypad(
                buttonSize = buttonSize,
                highlightedOperation = uiState.highlightedOperation,
                onInput = onInput,
            )
        }
    }
}

@Composable
private fun HistoryShortcut(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text = stringResource(R.string.history_title),
            color = colorResource(R.color.operator_key_background),
        )
    }
}

private fun calculateButtonSize(maxWidth: Dp, maxHeight: Dp): Dp {
    val columns = CalculatorKeypadLayout.COLUMN_COUNT
    val rows = CalculatorKeypadLayout.rows.size
    val widthBased = (maxWidth - KeySpacing * (columns - 1)) / columns
    val heightBased = (maxHeight * KEYPAD_HEIGHT_FRACTION - KeySpacing * (rows - 1)) / rows
    return minOf(widthBased, heightBased)
}

@Preview(showBackground = true)
@Composable
private fun CalculatorScreenPreview() {
    CalculatorTheme {
        CalculatorScreen(
            uiState = CalculatorUiState(display = "1234.5"),
            onInput = {},
            onNavigateToHistory = {},
        )
    }
}

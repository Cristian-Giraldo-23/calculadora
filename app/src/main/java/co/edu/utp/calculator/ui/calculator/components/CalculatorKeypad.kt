package co.edu.utp.calculator.ui.calculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.domain.model.CalculatorOperation

val KeySpacing = 12.dp
private const val LABEL_TO_BUTTON_RATIO = 0.4f

@Composable
fun CalculatorKeypad(
    buttonSize: Dp,
    highlightedOperation: CalculatorOperation?,
    onInput: (CalculatorInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelSize = (buttonSize.value * LABEL_TO_BUTTON_RATIO).sp
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(KeySpacing)) {
        CalculatorKeypadLayout.rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(KeySpacing)) {
                row.forEach { key ->
                    val width = if (key.isWide) buttonSize * 2 + KeySpacing else buttonSize
                    CalculatorButton(
                        key = key,
                        isHighlighted = isHighlighted(key, highlightedOperation),
                        labelSize = labelSize,
                        onClick = { onInput(key.input) },
                        modifier = Modifier.size(width = width, height = buttonSize),
                    )
                }
            }
        }
    }
}

private fun isHighlighted(key: CalculatorKey, highlightedOperation: CalculatorOperation?): Boolean {
    val keyOperation = (key.input as? CalculatorInput.Operation)?.operation
    return highlightedOperation != null && keyOperation == highlightedOperation
}

package co.edu.utp.calculator.ui.calculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import co.edu.utp.calculator.R
import co.edu.utp.calculator.domain.model.CalculatorError
import co.edu.utp.calculator.domain.model.CalculatorOperation
import co.edu.utp.calculator.ui.common.messageRes
import co.edu.utp.calculator.ui.common.symbolRes

@Composable
fun CalculatorDisplay(
    display: String,
    pendingLeftOperand: String?,
    pendingOperation: CalculatorOperation?,
    error: CalculatorError?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        PendingExpression(pendingLeftOperand, pendingOperation)
        if (error != null) {
            ErrorMessage(error)
        } else {
            MainValue(display)
        }
    }
}

@Composable
private fun PendingExpression(leftOperand: String?, operation: CalculatorOperation?) {
    val text = if (leftOperand != null && operation != null) {
        stringResource(R.string.expression_pending, leftOperand, stringResource(operation.symbolRes()))
    } else {
        ""
    }
    Text(
        text = text,
        color = colorResource(R.color.text_secondary),
        fontSize = 28.sp,
        maxLines = 1,
    )
}

@Composable
private fun MainValue(value: String) {
    Text(
        text = value,
        color = colorResource(R.color.text_primary),
        fontSize = valueFontSize(value.length),
        fontWeight = FontWeight.Light,
        maxLines = 1,
    )
}

@Composable
private fun ErrorMessage(error: CalculatorError) {
    Text(
        text = stringResource(error.messageRes()),
        color = colorResource(R.color.text_primary),
        fontSize = 32.sp,
        textAlign = TextAlign.End,
        maxLines = 2,
    )
}

private fun valueFontSize(length: Int): TextUnit = when {
    length <= 6 -> 80.sp
    length <= 9 -> 60.sp
    length <= 12 -> 46.sp
    else -> 36.sp
}

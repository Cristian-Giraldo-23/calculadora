package co.edu.utp.calculator.ui.calculator.components

import androidx.annotation.StringRes
import co.edu.utp.calculator.R
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.domain.model.CalculatorOperation
import co.edu.utp.calculator.ui.common.symbolRes

enum class KeyStyle { NUMBER, FUNCTION, OPERATOR }

data class CalculatorKey(
    @StringRes val labelRes: Int,
    val input: CalculatorInput,
    val style: KeyStyle,
    val isWide: Boolean = false,
)

object CalculatorKeypadLayout {

    const val COLUMN_COUNT = 4

    private val digitLabels = listOf(
        R.string.key_digit_0, R.string.key_digit_1, R.string.key_digit_2, R.string.key_digit_3,
        R.string.key_digit_4, R.string.key_digit_5, R.string.key_digit_6, R.string.key_digit_7,
        R.string.key_digit_8, R.string.key_digit_9,
    )

    val rows: List<List<CalculatorKey>> = listOf(
        listOf(
            CalculatorKey(R.string.key_clear, CalculatorInput.Clear, KeyStyle.FUNCTION),
            CalculatorKey(R.string.key_toggle_sign, CalculatorInput.ToggleSign, KeyStyle.FUNCTION),
            CalculatorKey(R.string.key_percent, CalculatorInput.Percent, KeyStyle.FUNCTION),
            operatorKey(CalculatorOperation.DIVIDE),
        ),
        listOf(digitKey(7), digitKey(8), digitKey(9), operatorKey(CalculatorOperation.MULTIPLY)),
        listOf(digitKey(4), digitKey(5), digitKey(6), operatorKey(CalculatorOperation.SUBTRACT)),
        listOf(digitKey(1), digitKey(2), digitKey(3), operatorKey(CalculatorOperation.ADD)),
        listOf(
            digitKey(0, isWide = true),
            CalculatorKey(R.string.key_decimal_point, CalculatorInput.DecimalPoint, KeyStyle.NUMBER),
            CalculatorKey(R.string.key_equals, CalculatorInput.Equals, KeyStyle.OPERATOR),
        ),
    )

    private fun digitKey(digit: Int, isWide: Boolean = false) = CalculatorKey(
        labelRes = digitLabels[digit],
        input = CalculatorInput.Digit(digit),
        style = KeyStyle.NUMBER,
        isWide = isWide,
    )

    private fun operatorKey(operation: CalculatorOperation) = CalculatorKey(
        labelRes = operation.symbolRes(),
        input = CalculatorInput.Operation(operation),
        style = KeyStyle.OPERATOR,
    )
}

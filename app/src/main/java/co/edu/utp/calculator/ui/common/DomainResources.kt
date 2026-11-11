package co.edu.utp.calculator.ui.common

import androidx.annotation.StringRes
import co.edu.utp.calculator.R
import co.edu.utp.calculator.domain.model.CalculatorError
import co.edu.utp.calculator.domain.model.CalculatorOperation

@StringRes
fun CalculatorOperation.symbolRes(): Int = when (this) {
    CalculatorOperation.ADD -> R.string.operation_add
    CalculatorOperation.SUBTRACT -> R.string.operation_subtract
    CalculatorOperation.MULTIPLY -> R.string.operation_multiply
    CalculatorOperation.DIVIDE -> R.string.operation_divide
}

@StringRes
fun CalculatorError.messageRes(): Int = when (this) {
    CalculatorError.DivisionByZero -> R.string.error_division_by_zero
    CalculatorError.Overflow -> R.string.error_overflow
}

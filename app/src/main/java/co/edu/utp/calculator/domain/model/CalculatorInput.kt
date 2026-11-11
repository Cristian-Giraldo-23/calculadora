package co.edu.utp.calculator.domain.model

sealed interface CalculatorInput {
    data class Digit(val value: Int) : CalculatorInput
    data object DecimalPoint : CalculatorInput
    data class Operation(val operation: CalculatorOperation) : CalculatorInput
    data object Equals : CalculatorInput
    data object Clear : CalculatorInput
    data object ToggleSign : CalculatorInput
    data object Percent : CalculatorInput
}

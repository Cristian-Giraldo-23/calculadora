package co.edu.utp.calculator.domain.model

sealed interface CalculatorError {
    data object DivisionByZero : CalculatorError
    data object Overflow : CalculatorError
}

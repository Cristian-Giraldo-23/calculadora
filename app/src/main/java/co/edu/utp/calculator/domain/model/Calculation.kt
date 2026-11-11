package co.edu.utp.calculator.domain.model

data class Calculation(
    val leftOperand: String,
    val operation: CalculatorOperation,
    val rightOperand: String,
    val result: String,
)

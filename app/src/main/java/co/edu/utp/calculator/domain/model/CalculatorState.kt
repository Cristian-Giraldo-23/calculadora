package co.edu.utp.calculator.domain.model

data class CalculatorState(
    val display: String = "0",
    val storedOperand: String? = null,
    val pendingOperation: CalculatorOperation? = null,
    val isOperandEntered: Boolean = false,
    val shouldReplaceDisplay: Boolean = true,
    val lastOperation: CalculatorOperation? = null,
    val lastOperand: String? = null,
    val error: CalculatorError? = null,
)

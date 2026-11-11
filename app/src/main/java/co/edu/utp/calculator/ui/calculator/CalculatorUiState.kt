package co.edu.utp.calculator.ui.calculator

import co.edu.utp.calculator.domain.model.Calculation
import co.edu.utp.calculator.domain.model.CalculatorError
import co.edu.utp.calculator.domain.model.CalculatorOperation

data class CalculatorUiState(
    val display: String,
    val pendingLeftOperand: String? = null,
    val pendingOperation: CalculatorOperation? = null,
    val highlightedOperation: CalculatorOperation? = null,
    val error: CalculatorError? = null,
    val history: List<Calculation> = emptyList(),
)

package co.edu.utp.calculator.domain.model

data class EngineResult(
    val state: CalculatorState,
    val completedCalculation: Calculation? = null,
)

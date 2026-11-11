package co.edu.utp.calculator.ui.calculator

import androidx.lifecycle.ViewModel
import co.edu.utp.calculator.domain.logic.CalculatorEngine
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.domain.model.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel(
    private val engine: CalculatorEngine = CalculatorEngine(),
) : ViewModel() {

    private var calculatorState = CalculatorState()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onInput(input: CalculatorInput) {
        val result = engine.process(calculatorState, input)
        calculatorState = result.state

        _uiState.update { current ->
            current.copy(
                display = result.state.display,
                pendingLeftOperand = result.state.storedOperand,
                pendingOperation = result.state.pendingOperation,
                highlightedOperation = result.state.pendingOperation
                    ?.takeUnless { result.state.isOperandEntered },
                error = result.state.error,
                history = result.completedCalculation
                    ?.let { current.history + it }
                    ?: current.history,
            )
        }
    }
}

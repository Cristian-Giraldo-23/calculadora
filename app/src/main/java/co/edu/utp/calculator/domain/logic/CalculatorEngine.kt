package co.edu.utp.calculator.domain.logic

import co.edu.utp.calculator.domain.model.Calculation
import co.edu.utp.calculator.domain.model.CalculatorError
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.domain.model.CalculatorOperation
import co.edu.utp.calculator.domain.model.CalculatorState
import co.edu.utp.calculator.domain.model.EngineResult
import java.math.BigDecimal
import java.math.MathContext

class CalculatorEngine {

    fun process(state: CalculatorState, input: CalculatorInput): EngineResult {
        if (input is CalculatorInput.Clear) return EngineResult(CalculatorState())
        if (state.error != null) return recoverFromError(state, input)

        return when (input) {
            is CalculatorInput.Digit -> EngineResult(appendDigit(state, input.value))
            CalculatorInput.DecimalPoint -> EngineResult(appendDecimalPoint(state))
            CalculatorInput.ToggleSign -> EngineResult(toggleSign(state))
            CalculatorInput.Percent -> EngineResult(applyPercent(state))
            is CalculatorInput.Operation -> selectOperation(state, input.operation)
            CalculatorInput.Equals -> evaluate(state)
            CalculatorInput.Clear -> EngineResult(CalculatorState())
        }
    }

    private fun recoverFromError(state: CalculatorState, input: CalculatorInput): EngineResult {
        val startsNewNumber = input is CalculatorInput.Digit || input is CalculatorInput.DecimalPoint
        return if (startsNewNumber) process(CalculatorState(), input) else EngineResult(state)
    }

    private fun appendDigit(state: CalculatorState, digit: Int): CalculatorState {
        val current = if (state.shouldReplaceDisplay) ZERO else state.display
        if (current.count { it.isDigit() } >= MAX_INPUT_DIGITS) return state

        val updated = when (current) {
            ZERO -> digit.toString()
            NEGATIVE_ZERO -> "$MINUS$digit"
            else -> current + digit
        }
        return state.copy(display = updated, shouldReplaceDisplay = false, isOperandEntered = true)
    }

    private fun appendDecimalPoint(state: CalculatorState): CalculatorState {
        val current = if (state.shouldReplaceDisplay) ZERO else state.display
        if (current.contains(DECIMAL_SEPARATOR)) return state

        return state.copy(
            display = current + DECIMAL_SEPARATOR,
            shouldReplaceDisplay = false,
            isOperandEntered = true,
        )
    }

    private fun toggleSign(state: CalculatorState): CalculatorState {
        val isWaitingForOperand = state.pendingOperation != null && !state.isOperandEntered
        if (state.shouldReplaceDisplay && (isWaitingForOperand || state.display == ZERO)) {
            return state.copy(display = NEGATIVE_ZERO, shouldReplaceDisplay = false, isOperandEntered = true)
        }

        val toggled = if (state.display.startsWith(MINUS)) state.display.drop(1) else MINUS + state.display
        return state.copy(display = toggled, isOperandEntered = true)
    }

    private fun applyPercent(state: CalculatorState): CalculatorState {
        val value = state.display.toBigDecimal()
        val relativeBase = state.storedOperand
            ?.takeIf { state.pendingOperation in RELATIVE_PERCENT_OPERATIONS }
            ?.toBigDecimal()
        val percentage = (relativeBase?.multiply(value) ?: value).divide(PERCENT_DIVISOR, MATH_CONTEXT)

        if (percentage.abs() > MAX_MAGNITUDE) return CalculatorState(error = CalculatorError.Overflow)
        return state.copy(
            display = formatNumber(percentage),
            shouldReplaceDisplay = true,
            isOperandEntered = true,
        )
    }

    private fun selectOperation(state: CalculatorState, operation: CalculatorOperation): EngineResult {
        val pendingOperation = state.pendingOperation
        val leftOperand = state.storedOperand

        if (pendingOperation == null || leftOperand == null || !state.isOperandEntered) {
            val normalizedDisplay = normalize(state.display)
            return EngineResult(
                state.copy(
                    display = normalizedDisplay,
                    storedOperand = normalizedDisplay,
                    pendingOperation = operation,
                    shouldReplaceDisplay = true,
                    isOperandEntered = false,
                    lastOperation = null,
                    lastOperand = null,
                ),
            )
        }

        return when (val outcome = calculate(leftOperand, pendingOperation, state.display)) {
            is ArithmeticOutcome.Failure -> EngineResult(CalculatorState(error = outcome.error))
            is ArithmeticOutcome.Success -> EngineResult(
                state = CalculatorState(
                    display = outcome.calculation.result,
                    storedOperand = outcome.calculation.result,
                    pendingOperation = operation,
                ),
                completedCalculation = outcome.calculation,
            )
        }
    }

    private fun evaluate(state: CalculatorState): EngineResult {
        val pendingOperation = state.pendingOperation
        val leftOperand = state.storedOperand
        if (pendingOperation != null && leftOperand != null) {
            return completeCalculation(leftOperand, pendingOperation, state.display)
        }

        val lastOperation = state.lastOperation
        val lastOperand = state.lastOperand
        if (lastOperation != null && lastOperand != null) {
            return completeCalculation(state.display, lastOperation, lastOperand)
        }

        return EngineResult(state.copy(display = normalize(state.display), shouldReplaceDisplay = true))
    }

    private fun completeCalculation(
        leftOperand: String,
        operation: CalculatorOperation,
        rightOperand: String,
    ): EngineResult = when (val outcome = calculate(leftOperand, operation, rightOperand)) {
        is ArithmeticOutcome.Failure -> EngineResult(CalculatorState(error = outcome.error))
        is ArithmeticOutcome.Success -> EngineResult(
            state = CalculatorState(
                display = outcome.calculation.result,
                lastOperation = operation,
                lastOperand = outcome.calculation.rightOperand,
            ),
            completedCalculation = outcome.calculation,
        )
    }

    private fun calculate(
        leftOperand: String,
        operation: CalculatorOperation,
        rightOperand: String,
    ): ArithmeticOutcome {
        val left = leftOperand.toBigDecimal()
        val right = rightOperand.toBigDecimal()

        if (operation == CalculatorOperation.DIVIDE && right.signum() == 0) {
            return ArithmeticOutcome.Failure(CalculatorError.DivisionByZero)
        }

        val result = when (operation) {
            CalculatorOperation.ADD -> left.add(right, MATH_CONTEXT)
            CalculatorOperation.SUBTRACT -> left.subtract(right, MATH_CONTEXT)
            CalculatorOperation.MULTIPLY -> left.multiply(right, MATH_CONTEXT)
            CalculatorOperation.DIVIDE -> left.divide(right, MATH_CONTEXT)
        }

        if (result.abs() > MAX_MAGNITUDE) return ArithmeticOutcome.Failure(CalculatorError.Overflow)

        return ArithmeticOutcome.Success(
            Calculation(
                leftOperand = formatNumber(left),
                operation = operation,
                rightOperand = formatNumber(right),
                result = formatNumber(result),
            ),
        )
    }

    private fun normalize(text: String): String = formatNumber(text.toBigDecimal())

    private fun formatNumber(value: BigDecimal): String {
        if (value.signum() == 0) return ZERO

        for (precision in MATH_CONTEXT.precision downTo 1) {
            val candidate = value.round(MathContext(precision)).stripTrailingZeros().toPlainString()
            if (candidate.length <= MAX_DISPLAY_LENGTH) return candidate
        }
        return value.round(MathContext(SCIENTIFIC_PRECISION)).stripTrailingZeros().toString()
    }

    private sealed interface ArithmeticOutcome {
        data class Success(val calculation: Calculation) : ArithmeticOutcome
        data class Failure(val error: CalculatorError) : ArithmeticOutcome
    }

    private companion object {
        const val ZERO = "0"
        const val NEGATIVE_ZERO = "-0"
        const val MINUS = "-"
        const val DECIMAL_SEPARATOR = '.'
        const val MAX_INPUT_DIGITS = 15
        const val MAX_DISPLAY_LENGTH = 16
        const val SCIENTIFIC_PRECISION = 10
        val MATH_CONTEXT: MathContext = MathContext.DECIMAL64
        val MAX_MAGNITUDE = BigDecimal("1E+100")
        val PERCENT_DIVISOR = BigDecimal(100)
        val RELATIVE_PERCENT_OPERATIONS = setOf(CalculatorOperation.ADD, CalculatorOperation.SUBTRACT)
    }
}

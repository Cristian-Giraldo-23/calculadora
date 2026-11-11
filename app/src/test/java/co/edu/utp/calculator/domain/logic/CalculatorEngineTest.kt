package co.edu.utp.calculator.domain.logic

import co.edu.utp.calculator.domain.model.Calculation
import co.edu.utp.calculator.domain.model.CalculatorError
import co.edu.utp.calculator.domain.model.CalculatorInput
import co.edu.utp.calculator.domain.model.CalculatorInput.Clear
import co.edu.utp.calculator.domain.model.CalculatorInput.DecimalPoint
import co.edu.utp.calculator.domain.model.CalculatorInput.Digit
import co.edu.utp.calculator.domain.model.CalculatorInput.Equals
import co.edu.utp.calculator.domain.model.CalculatorInput.Operation
import co.edu.utp.calculator.domain.model.CalculatorInput.Percent
import co.edu.utp.calculator.domain.model.CalculatorInput.ToggleSign
import co.edu.utp.calculator.domain.model.CalculatorOperation.ADD
import co.edu.utp.calculator.domain.model.CalculatorOperation.DIVIDE
import co.edu.utp.calculator.domain.model.CalculatorOperation.MULTIPLY
import co.edu.utp.calculator.domain.model.CalculatorState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEngineTest {

    private val engine = CalculatorEngine()

    private data class Session(val state: CalculatorState, val history: List<Calculation>)

    private fun run(vararg inputs: CalculatorInput): Session =
        inputs.fold(Session(CalculatorState(), emptyList())) { session, input ->
            val result = engine.process(session.state, input)
            Session(result.state, session.history + listOfNotNull(result.completedCalculation))
        }

    @Test
    fun addsTwoNumbersAndRecordsHistory() {
        val session = run(Digit(5), Operation(ADD), Digit(3), Equals)
        assertEquals("8", session.state.display)
        assertEquals(listOf(Calculation("5", ADD, "3", "8")), session.history)
    }

    @Test
    fun chainsOperationsFromLeftToRight() {
        val session = run(Digit(2), Operation(ADD), Digit(3), Operation(MULTIPLY), Digit(4), Equals)
        assertEquals("20", session.state.display)
    }

    @Test
    fun repeatedEqualsRepeatsLastOperation() {
        val session = run(Digit(5), Operation(ADD), Digit(3), Equals, Equals, Equals)
        assertEquals("14", session.state.display)
    }

    @Test
    fun divisionByZeroProducesControlledError() {
        val session = run(Digit(5), Operation(DIVIDE), Digit(0), Equals)
        assertEquals(CalculatorError.DivisionByZero, session.state.error)
        assertEquals(emptyList<Calculation>(), session.history)
    }

    @Test
    fun typingAfterErrorStartsNewNumber() {
        val session = run(Digit(5), Operation(DIVIDE), Digit(0), Equals, Digit(7))
        assertNull(session.state.error)
        assertEquals("7", session.state.display)
    }

    @Test
    fun handlesDecimalsWithoutFloatingPointErrors() {
        val session = run(Digit(0), DecimalPoint, Digit(1), Operation(ADD), Digit(0), DecimalPoint, Digit(2), Equals)
        assertEquals("0.3", session.state.display)
    }

    @Test
    fun ignoresSecondDecimalPoint() {
        val session = run(Digit(1), DecimalPoint, Digit(2), DecimalPoint, Digit(3))
        assertEquals("1.23", session.state.display)
    }

    @Test
    fun percentIsRelativeToStoredOperandForAddition() {
        val session = run(Digit(5), Digit(0), Operation(ADD), Digit(1), Digit(0), Percent, Equals)
        assertEquals("55", session.state.display)
    }

    @Test
    fun toggleSignBeforeTypingProducesNegativeNumber() {
        val session = run(ToggleSign, Digit(5))
        assertEquals("-5", session.state.display)
    }

    @Test
    fun clearResetsCalculatorState() {
        val session = run(Digit(9), Operation(ADD), Digit(1), Clear)
        assertEquals(CalculatorState(), session.state)
    }
}

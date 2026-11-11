package co.edu.utp.calculator.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.utp.calculator.R
import co.edu.utp.calculator.domain.model.Calculation
import co.edu.utp.calculator.domain.model.CalculatorOperation
import co.edu.utp.calculator.ui.calculator.CalculatorViewModel
import co.edu.utp.calculator.ui.common.symbolRes
import co.edu.utp.calculator.ui.theme.CalculatorTheme

@Composable
fun HistoryRoute(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(history = uiState.history, onNavigateBack = onNavigateBack)
}

@Composable
fun HistoryScreen(
    history: List<Calculation>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.calculator_background))
            .safeDrawingPadding()
            .padding(16.dp),
    ) {
        TextButton(onClick = onNavigateBack) {
            Text(
                text = stringResource(R.string.back_to_calculator),
                color = colorResource(R.color.operator_key_background),
            )
        }
        Text(
            text = stringResource(R.string.history_title),
            color = colorResource(R.color.text_primary),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        if (history.isEmpty()) {
            HistoryEmptyState(modifier = Modifier.weight(1f))
        } else {
            HistoryList(history = history, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.history_empty),
            color = colorResource(R.color.text_secondary),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HistoryList(history: List<Calculation>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LaunchedEffect(history.size) {
        listState.scrollToItem(history.lastIndex)
    }
    LazyColumn(state = listState, modifier = modifier.fillMaxWidth()) {
        items(history) { calculation ->
            HistoryItem(calculation)
            HorizontalDivider(color = colorResource(R.color.divider))
        }
    }
}

@Composable
private fun HistoryItem(calculation: Calculation) {
    Text(
        text = stringResource(
            R.string.history_entry,
            calculation.leftOperand,
            stringResource(calculation.operation.symbolRes()),
            calculation.rightOperand,
            calculation.result,
        ),
        color = colorResource(R.color.text_primary),
        fontSize = 24.sp,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    CalculatorTheme {
        HistoryScreen(
            history = listOf(Calculation("5", CalculatorOperation.ADD, "3", "8")),
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryEmptyPreview() {
    CalculatorTheme {
        HistoryScreen(history = emptyList(), onNavigateBack = {})
    }
}

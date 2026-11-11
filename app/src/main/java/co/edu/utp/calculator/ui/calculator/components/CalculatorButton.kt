package co.edu.utp.calculator.ui.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.TextUnit
import co.edu.utp.calculator.R

@Composable
fun CalculatorButton(
    key: CalculatorKey,
    isHighlighted: Boolean,
    labelSize: TextUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = keyColors(key.style, isHighlighted)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.background)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(key.labelRes),
            color = colors.content,
            fontSize = labelSize,
        )
    }
}

private data class KeyColors(val background: Color, val content: Color)

@Composable
private fun keyColors(style: KeyStyle, isHighlighted: Boolean): KeyColors = when {
    style == KeyStyle.OPERATOR && isHighlighted -> KeyColors(
        background = colorResource(R.color.operator_key_selected_background),
        content = colorResource(R.color.operator_key_background),
    )
    style == KeyStyle.OPERATOR -> KeyColors(
        background = colorResource(R.color.operator_key_background),
        content = colorResource(R.color.key_text_light),
    )
    style == KeyStyle.FUNCTION -> KeyColors(
        background = colorResource(R.color.function_key_background),
        content = colorResource(R.color.key_text_dark),
    )
    else -> KeyColors(
        background = colorResource(R.color.number_key_background),
        content = colorResource(R.color.key_text_light),
    )
}

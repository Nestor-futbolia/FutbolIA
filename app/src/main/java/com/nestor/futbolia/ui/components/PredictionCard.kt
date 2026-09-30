package com.nestor.futbolia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.PredictionDetail
import java.util.Locale

@Composable
fun PredictionCard(
    prediction: PredictionDetail
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "🤖 PREDICCIÓN FÚTBOL IA",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = "NESTOR",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                Modifier.height(16.dp)
            )

            ProbabilityRow(
                label = "LOCAL",
                value = prediction.percentages.home
            )

            ProbabilityRow(
                label = "EMPATE",
                value = prediction.percentages.draw
            )

            ProbabilityRow(
                label = "VISITANTE",
                value = prediction.percentages.away
            )

            Spacer(
                Modifier.height(16.dp)
            )

            Divider()

            Spacer(
                Modifier.height(12.dp)
            )

            Text(
                text =
                    "Predicción principal: ${
                        predictionLabel(
                            prediction.prediction
                        )
                    }",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "Confianza: ${
                        formatPercentage(
                            prediction.predictionPercentage
                        )
                    }",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    "Modelo: ${prediction.modelVersion}",
                style =
                    MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun ProbabilityRow(
    label: String,
    value: Double
) {

    val safeValue =
        value.coerceIn(
            0.0,
            100.0
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = label
            )

            Text(
                text =
                    formatPercentage(
                        safeValue
                    )
            )
        }

        LinearProgressIndicator(

            progress =
                (
                    safeValue / 100.0
                )
                    .coerceIn(
                        0.0,
                        1.0
                    )
                    .toFloat(),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp
                    )
        )
    }
}

private fun formatPercentage(
    value: Double
): String {

    return String.format(
        Locale.US,
        "%.2f%%",
        value
    )
}

private fun predictionLabel(
    prediction: String
): String {

    return when (
        prediction.uppercase()
    ) {

        "HOME",
        "LOCAL" ->
            "LOCAL"

        "DRAW",
        "EMPATE" ->
            "EMPATE"

        "AWAY",
        "VISITANTE" ->
            "VISITANTE"

        else ->
            prediction
    }
}

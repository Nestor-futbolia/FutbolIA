package com.nestor.futbolia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.PredictionDetail

@Composable
fun PredictionCard(
    prediction: PredictionDetail
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    "🤖 PREDICCIÓN FÚTBOL IA",

                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "NESTOR",

                color =
                    MaterialTheme.colorScheme.primary,

                style =
                    MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            ProbabilityRow(
                "LOCAL",
                prediction.percentages.home
            )

            ProbabilityRow(
                "EMPATE",
                prediction.percentages.draw
            )

            ProbabilityRow(
                "VISITANTE",
                prediction.percentages.away
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "Predicción principal: " +
                        prediction.prediction,

                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "Confianza: " +
                        formatPercentage(
                            prediction.predictionPercentage
                        )
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Modelo: " +
                        prediction.modelVersion,

                style =
                    MaterialTheme.typography.labelSmall,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProbabilityRow(
    label: String,
    value: Double
) {

    Column(
        modifier =
            Modifier
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

            Text(label)

            Text(
                formatPercentage(value)
            )
        }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        LinearProgressIndicator(

            progress = {
                (value / 100.0)
                    .coerceIn(0.0, 1.0)
                    .toFloat()
            },

            modifier =
                Modifier.fillMaxWidth()
        )
    }
}

private fun formatPercentage(
    value: Double
): String {

    return String.format(
        java.util.Locale.US,
        "%.1f%%",
        value
    )
}

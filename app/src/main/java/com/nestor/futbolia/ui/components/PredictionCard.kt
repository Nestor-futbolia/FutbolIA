package com.nestor.futbolia.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
                .padding(12.dp)
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    "🤖 PREDICCIÓN FÚTBOL IA",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    "NESTOR",
                style =
                    MaterialTheme
                        .typography
                        .labelLarge
            )

            Spacer(
                Modifier.height(16.dp)
            )

            ProbabilityRow(
                label = "LOCAL",
                value =
                    prediction
                        .percentages
                        .home
            )

            ProbabilityRow(
                label = "EMPATE",
                value =
                    prediction
                        .percentages
                        .draw
            )

            ProbabilityRow(
                label = "VISITANTE",
                value =
                    prediction
                        .percentages
                        .away
            )

            Spacer(
                Modifier.height(16.dp)
            )

            HorizontalDivider()

            Spacer(
                Modifier.height(12.dp)
            )

            Text(
                text =
                    "Predicción principal: " +
                        prediction.prediction,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(
                text =
                    "Confianza: " +
                        "${prediction.predictionPercentage}%",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    "Modelo: " +
                        prediction.modelVersion,
                style =
                    MaterialTheme
                        .typography
                        .labelSmall
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
                "${value}%"
            )
        }

        LinearProgressIndicator(
            progress = {
                (value / 100.0)
                    .coerceIn(
                        0.0,
                        1.0
                    )
                    .toFloat()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp
                    )
        )
    }
}

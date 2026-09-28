package com.nestor.futbolia.ui.screens.predictions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.PredictionDetail
import com.nestor.futbolia.ui.components.PredictionCard

@Composable
fun AiPredictionsScreen(
    predictions: List<PredictionDetail>
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Text(
            "🤖 PREDICCIONES IA",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            Modifier.height(12.dp)
        )

        predictions.forEach {

            PredictionCard(
                prediction = it
            )
        }
    }
}

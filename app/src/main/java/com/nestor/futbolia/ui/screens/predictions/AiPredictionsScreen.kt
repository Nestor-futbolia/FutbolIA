package com.nestor.futbolia.ui.screens.predictions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.PredictionDetail
import com.nestor.futbolia.ui.components.PredictionCard

@Composable
fun AiPredictionsScreen(
    predictions:
        List<PredictionDetail>
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(
                8.dp
            )
    ) {

        Text(
            "🤖 PREDICCIONES IA",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        predictions.forEach { prediction ->

            PredictionCard(
                prediction = prediction
            )
        }
    }
}

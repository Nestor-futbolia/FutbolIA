package com.nestor.futbolia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.MatchUi
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MatchCard(
    match: MatchUi,
    onClick: () -> Unit
) {

    val fixture =
        match.fixture

    val prediction =
        match.prediction

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 5.dp
                )
                .clickable {
                    onClick()
                },

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        fixture?.leagueName
                            ?: "FÚTBOL",

                    style =
                        MaterialTheme.typography.labelMedium,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                StatusChip(
                    fixture?.status
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            fixture?.homeTeam?.name
                                ?: "Local",

                        style =
                            MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            formatTime(
                                fixture?.startingAt
                            ),

                        style =
                            MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "VS",

                        style =
                            MaterialTheme.typography.titleMedium,

                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier =
                        Modifier.weight(1f),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            fixture?.awayTeam?.name
                                ?: "Visitante",

                        style =
                            MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (prediction != null) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "🤖 NESTOR  " +
                            prediction.prediction +
                            " · " +
                            formatTimePercentage(
                                prediction.predictionPercentage
                            ),

                    style =
                        MaterialTheme.typography.labelMedium,

                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: String?
) {

    val normalized =
        status
            ?.uppercase()
            ?: "NS"

    val label =
        when (normalized) {

            "NS" ->
                "PRÓXIMO"

            "1H",
            "2H",
            "LIVE",
            "ET",
            "HT",
            "P" ->
                "EN VIVO"

            "FT",
            "AET",
            "PEN" ->
                "FINALIZADO"

            else ->
                normalized
        }

    AssistChip(

        onClick = {},

        label = {
            Text(label)
        }
    )
}

private fun formatTime(
    value: String?
): String {

    if (value.isNullOrBlank()) {
        return "--:--"
    }

    return try {

        val parser =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                Locale.US
            )

        val formatter =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )

        val date =
            parser.parse(value)

        formatter.format(
            date ?: return "--:--"
        )

    } catch (_: Exception) {

        "--:--"
    }
}

private fun formatTimePercentage(
    value: Double
): String {

    return String.format(
        Locale.US,
        "%.1f%%",
        value
    )
}

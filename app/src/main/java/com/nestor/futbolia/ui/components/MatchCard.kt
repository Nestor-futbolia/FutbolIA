package com.nestor.futbolia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.MatchUi
import java.text.SimpleDateFormat
import java.util.*

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
                    vertical = 6.dp
                )
                .clickable {
                    onClick()
                },
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
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
                        MaterialTheme
                            .typography
                            .labelMedium
                )

                StatusLabel(
                    fixture?.status
                )
            }

            Spacer(
                Modifier.height(12.dp)
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
                            fixture
                                ?.homeTeam
                                ?.name
                                ?: "Local",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            fixtureTime(
                                fixture?.startingAt
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall
                    )

                    Text(
                        text = "VS",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
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
                            fixture
                                ?.awayTeam
                                ?.name
                                ?: "Visitante",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )
                }
            }

            Spacer(
                Modifier.height(12.dp)
            )

            if (prediction != null) {

                HorizontalDivider()

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text =
                        "🤖 NESTOR: " +
                            prediction.prediction +
                            " · " +
                            "${prediction.predictionPercentage}%",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge
                )
            }
        }
    }
}

@Composable
private fun StatusLabel(
    status: String?
) {

    val normalized =
        status
            ?.uppercase()
            ?: "NS"

    val text =
        when (normalized) {
            "NS" -> "PRÓXIMO"
            "1H",
            "2H",
            "LIVE",
            "ET",
            "HT",
            "P" -> "EN VIVO"
            "FT",
            "AET",
            "PEN" -> "FINALIZADO"
            else -> normalized
        }

    AssistChip(
        onClick = {},
        label = {
            Text(text)
        }
    )
}

private fun fixtureTime(
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

        if (date != null) {
            formatter.format(date)
        } else {
            "--:--"
        }

    } catch (_: Exception) {
        "--:--"
    }
}

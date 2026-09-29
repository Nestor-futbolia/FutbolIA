package com.nestor.futbolia.ui.screens.leagues

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LeaguesScreen() {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(
                8.dp
            )
    ) {

        Text(
            "LIGAS",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Text(
            "COMPETICIONES"
        )

        LeagueItem(
            "Premier League",
            "Inglaterra"
        )

        LeagueItem(
            "LaLiga",
            "España"
        )

        LeagueItem(
            "Serie A",
            "Italia"
        )

        LeagueItem(
            "Bundesliga",
            "Alemania"
        )

        LeagueItem(
            "Ligue 1",
            "Francia"
        )

        LeagueItem(
            "Champions League",
            "Europa"
        )
    }
}

@Composable
private fun LeagueItem(
    name: String,
    country: String
) {

    Card {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            Text(
                name,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(
                country,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

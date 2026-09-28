package com.nestor.futbolia.ui.screens.leagues

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LeaguesScreen() {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Text(
            "LIGAS",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Competiciones"
        )

        Spacer(
            Modifier.height(12.dp)
        )

        LeagueItem("Premier League")
        LeagueItem("La Liga")
        LeagueItem("Serie A")
        LeagueItem("Bundesliga")
        LeagueItem("Ligue 1")
        LeagueItem("Champions League")
    }
}

@Composable
private fun LeagueItem(
    name: String
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                )
    ) {

        Text(
            text = name,
            modifier =
                Modifier.padding(
                    16.dp
                )
        )
    }
}

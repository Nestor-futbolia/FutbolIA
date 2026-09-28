package com.nestor.futbolia.ui.screens.myanalysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.ui.MainDestination
import com.nestor.futbolia.ui.components.BottomNavigationBar

@Composable
fun MyAnalysesScreen(
    repository: FutbolRepository,
    onOpenMatch: (Int) -> Unit,
    onDestinationChanged: (MainDestination) -> Unit
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        "MIS ANÁLISIS"
                    )
                }
            )
        },
        bottomBar = {

            BottomNavigationBar(
                selected =
                    MainDestination.MIS_ANALISIS,
                onSelected =
                    onDestinationChanged
            )
        }
    ) { padding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
        ) {

            Text(
                "Mis análisis",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                Modifier.height(12.dp)
            )

            Text(
                "Aquí aparecerán los partidos que "
                        + "hayas abierto y analizado con NESTOR."
            )

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "MI COMBINACIÓN",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                "La combinación se construirá "
                        + "a partir de tus selecciones de partidos."
            )
        }
    }
}

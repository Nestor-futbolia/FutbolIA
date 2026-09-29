@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.nestor.futbolia.ui.screens.myanalysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.ui.MainDestination
import com.nestor.futbolia.ui.components.BottomNavigationBar

@Composable
fun MyAnalysesScreen(
    onDestinationChanged:
        (MainDestination) -> Unit
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
                    .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )

        ) {

            Text(

                "MIS ANÁLISIS",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Text(

                "Aquí aparecerán los partidos que hayas abierto y analizado con NESTOR."
            )

            Text(

                "MI COMBINACIÓN",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(

                "Tus selecciones de partidos aparecerán aquí cuando activemos la persistencia local."
            )
        }
    }
}

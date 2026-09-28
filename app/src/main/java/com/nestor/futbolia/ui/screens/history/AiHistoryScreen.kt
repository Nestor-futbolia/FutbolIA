package com.nestor.futbolia.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.AiPerformance
import com.nestor.futbolia.data.AiStatus
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.ui.MainDestination
import com.nestor.futbolia.ui.components.BottomNavigationBar
import kotlinx.coroutines.launch

@Composable
fun AiHistoryScreen(
    repository: FutbolRepository,
    onDestinationChanged: (MainDestination) -> Unit
) {

    var status by remember {
        mutableStateOf<AiStatus?>(null)
    }

    var performance by remember {
        mutableStateOf<AiPerformance?>(null)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    val scope =
        rememberCoroutineScope()

    LaunchedEffect(Unit) {

        try {

            status =
                repository.loadAiStatus()

            performance =
                repository.loadPerformance()

        } finally {

            loading = false
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        "📈 HISTORIAL IA"
                    )
                }
            )
        },
        bottomBar = {

            BottomNavigationBar(
                selected =
                    MainDestination.HISTORIAL_IA,
                onSelected =
                    onDestinationChanged
            )
        }
    ) { padding ->

        if (loading) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
            ) {

                CircularProgressIndicator()
            }

            return@Scaffold
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
        ) {

            Text(
                "NESTOR",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                Modifier.height(16.dp)
            )

            InfoCard(
                title = "MODELO ACTIVO",
                value =
                    status
                        ?.modelVersion
                        ?: "N/D"
            )

            InfoCard(
                title = "PARTIDOS DE ENTRENAMIENTO",
                value =
                    status
                        ?.trainingMatches
                        ?.toString()
                        ?: "N/D"
            )

            InfoCard(
                title = "PARTIDOS EVALUADOS",
                value =
                    performance
                        ?.evaluatedMatches
                        ?.toString()
                        ?: "0"
            )

            InfoCard(
                title = "ACCURACY",
                value =
                    performance
                        ?.accuracyPercent
                        ?.let {
                            "$it%"
                        }
                        ?: "N/D"
            )

            InfoCard(
                title = "LOG LOSS",
                value =
                    performance
                        ?.logLoss
                        ?.let {
                            "%.4f".format(it)
                        }
                        ?: "N/D"
            )

            InfoCard(
                title = "BRIER SCORE",
                value =
                    performance
                        ?.brierScore
                        ?.let {
                            "%.4f".format(it)
                        }
                        ?: "N/D"
            )

            Spacer(
                Modifier.height(16.dp)
            )

            Button(
                onClick = {

                    scope.launch {

                        status =
                            repository
                                .loadAiStatus()

                        performance =
                            repository
                                .loadPerformance()
                    }
                }
            ) {

                Text(
                    "ACTUALIZAR HISTORIAL"
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                title,
                style =
                    MaterialTheme
                        .typography
                        .labelLarge
            )

            Text(
                value,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }
    }
}

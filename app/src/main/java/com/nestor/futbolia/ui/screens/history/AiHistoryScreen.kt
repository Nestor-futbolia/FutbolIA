@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.nestor.futbolia.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    onDestinationChanged:
        (MainDestination) -> Unit
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

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val scope =
        rememberCoroutineScope()

    suspend fun load() {

        loading = true
        error = null

        try {

            status =
                repository.loadAiStatus()

            performance =
                repository.loadPerformance()

        } catch (e: Exception) {

            error =
                e.message
                    ?: "No se pudo cargar el historial"

        } finally {

            loading = false
        }
    }

    LaunchedEffect(Unit) {
        load()
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

        when {

            loading -> {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)

                ) {

                    CircularProgressIndicator()
                }
            }

            error != null -> {

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

                        error!!,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    Button(

                        onClick = {

                            scope.launch {
                                load()
                            }
                        }

                    ) {

                        Text(
                            "REINTENTAR"
                        )
                    }
                }
            }

            else -> {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )

                ) {

                    Text(

                        "NESTOR",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall
                    )

                    InfoCard(

                        "MODELO ACTIVO",

                        status?.modelVersion
                            ?: "N/D"
                    )

                    InfoCard(

                        "PARTIDOS EVALUADOS",

                        performance
                            ?.evaluatedMatches
                            ?.toString()
                            ?: "0"
                    )

                    InfoCard(

                        "CORRECTOS",

                        performance
                            ?.correctMatches
                            ?.toString()
                            ?: "0"
                    )

                    InfoCard(

                        "ACCURACY",

                        formatValue(
                            performance?.accuracy
                        )
                    )

                    InfoCard(

                        "LOG LOSS",

                        formatValue(
                            performance?.logLoss
                        )
                    )

                    InfoCard(

                        "BRIER SCORE",

                        formatValue(
                            performance?.brierScore
                        )
                    )

                    Button(

                        onClick = {

                            scope.launch {
                                load()
                            }
                        }

                    ) {

                        Text(
                            "ACTUALIZAR"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String
) {

    Card {

        androidx.compose.foundation.layout.Row(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {

            Text(title)

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

private fun formatValue(
    value: Double?
): String {

    return value?.let {

        String.format(
            java.util.Locale.US,
            "%.4f",
            it
        )

    } ?: "N/D"
}

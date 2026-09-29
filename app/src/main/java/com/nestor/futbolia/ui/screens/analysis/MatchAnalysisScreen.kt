package com.nestor.futbolia.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.data.MatchUi
import com.nestor.futbolia.ui.components.PredictionCard
import kotlinx.coroutines.launch

private enum class AnalysisTab {

    RESUMEN,
    ESTADISTICAS,
    IA,
    HISTORIAL
}

@Composable
fun MatchAnalysisScreen(
    matchId: Int,
    repository: FutbolRepository,
    onBack: () -> Unit
) {

    var match by remember {
        mutableStateOf<MatchUi?>(null)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var selectedTab by remember {
        mutableStateOf(
            AnalysisTab.RESUMEN
        )
    }

    val scope =
        rememberCoroutineScope()

    suspend fun load() {

        loading = true
        error = null

        try {

            match =
                repository.loadMatch(
                    matchId
                )

        } catch (e: Exception) {

            error =
                e.message
                    ?: "Error al cargar el partido"

        } finally {

            loading = false
        }
    }

    LaunchedEffect(matchId) {
        load()
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "ANÁLISIS DEL PARTIDO"
                    )
                },

                navigationIcon = {

                    Button(
                        onClick = onBack
                    ) {

                        Text("‹")
                    }
                }
            )
        }

    ) { padding ->

        when {

            loading -> {

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.padding(
                                24.dp
                            )
                    )
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

                val current =
                    match

                if (current != null) {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding)
                    ) {

                        MatchHeader(
                            match = current
                        )

                        TabRow(

                            selectedTabIndex =
                                selectedTab.ordinal
                        ) {

                            AnalysisTab.values()
                                .forEach { tab ->

                                    Tab(

                                        selected =
                                            selectedTab ==
                                                tab,

                                        onClick = {
                                            selectedTab =
                                                tab
                                        },

                                        text = {

                                            Text(
                                                when (tab) {

                                                    AnalysisTab.RESUMEN ->
                                                        "RESUMEN"

                                                    AnalysisTab.ESTADISTICAS ->
                                                        "ESTADÍSTICAS"

                                                    AnalysisTab.IA ->
                                                        "IA"

                                                    AnalysisTab.HISTORIAL ->
                                                        "HISTORIAL"
                                                }
                                            )
                                        }
                                    )
                                }
                        }

                        when (selectedTab) {

                            AnalysisTab.RESUMEN -> {

                                SummaryTab(
                                    match = current
                                )
                            }

                            AnalysisTab.ESTADISTICAS -> {

                                StatisticsTab(
                                    match = current
                                )
                            }

                            AnalysisTab.IA -> {

                                current.prediction?.let {

                                    PredictionCard(
                                        prediction = it
                                    )
                                }
                            }

                            AnalysisTab.HISTORIAL -> {

                                HistoryTab(
                                    match = current
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchHeader(
    match: MatchUi
) {

    val fixture =
        match.fixture

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
    ) {

        Text(
            fixture?.leagueName
                ?: "FÚTBOL",

            color =
                MaterialTheme
                    .colorScheme
                    .primary
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                fixture?.homeTeam?.name
                    ?: "Local",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(
                "VS",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(
                fixture?.awayTeam?.name
                    ?: "Visitante",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            "Estado: " +
                (fixture?.status ?: "N/D"),

            style =
                MaterialTheme
                    .typography
                    .labelMedium
        )

        HorizontalDivider(
            modifier =
                Modifier.padding(
                    top = 14.dp
                )
        )
    }
}

@Composable
private fun SummaryTab(
    match: MatchUi
) {

    Column(
        modifier =
            Modifier.padding(16.dp)
    ) {

        Text(
            "RESUMEN",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        match.prediction?.let {

            PredictionCard(
                prediction = it
            )
        }
    }
}

@Composable
private fun StatisticsTab(
    match: MatchUi
) {

    Column(
        modifier =
            Modifier.padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {

        Text(
            "ESTADÍSTICAS",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Text(
            "ID del partido: " +
                (match.fixture?.id ?: "N/D")
        )

        Text(
            "Estado: " +
                (match.fixture?.status ?: "N/D")
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            "Las estadísticas detalladas de tiros, posesión, córners y tarjetas solo se mostrarán cuando estén expuestas por el backend."
        )
    }
}

@Composable
private fun HistoryTab(
    match: MatchUi
) {

    Column(
        modifier =
            Modifier.padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {

        Text(
            "HISTORIAL",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Text(
            "Modelo utilizado:"
        )

        Text(
            match.prediction?.modelVersion
                ?: "N/D"
        )

        Text(
            "La evaluación histórica global se encuentra en HISTORIAL IA."
        )
    }
}

package com.nestor.futbolia.ui.screens.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.data.MatchUi
import com.nestor.futbolia.ui.components.PredictionCard
import kotlinx.coroutines.launch

enum class AnalysisTab {
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

    var tab by remember {
        mutableStateOf(
            AnalysisTab.RESUMEN
        )
    }

    val scope =
        rememberCoroutineScope()

    LaunchedEffect(matchId) {

        loading = true
        error = null

        try {

            match =
                repository.loadMatch(
                    matchId
                )

        } catch (exception: Exception) {

            error =
                exception.message
                    ?: "No se pudo cargar el partido"

        } finally {

            loading = false
        }
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

                    TextButton(
                        onClick = onBack
                    ) {
                        Text("‹")
                    }
                }
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

        if (error != null) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
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

                            try {

                                loading = true

                                match =
                                    repository
                                        .loadMatch(
                                            matchId
                                        )

                                error = null

                            } catch (
                                exception: Exception
                            ) {

                                error =
                                    exception.message
                            } finally {

                                loading = false
                            }
                        }
                    }
                ) {
                    Text("REINTENTAR")
                }
            }

            return@Scaffold
        }

        val current =
            match ?: return@Scaffold

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
                    tab.ordinal
            ) {

                AnalysisTab.entries.forEach { item ->

                    Tab(
                        selected =
                            tab == item,
                        onClick = {
                            tab = item
                        },
                        text = {
                            Text(
                                when (item) {
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

            when (tab) {

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
            style =
                MaterialTheme
                    .typography
                    .labelMedium
        )

        Spacer(
            Modifier.height(12.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                fixture
                    ?.homeTeam
                    ?.name
                    ?: "Local",
                modifier =
                    Modifier.weight(1f),
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
                fixture
                    ?.awayTeam
                    ?.name
                    ?: "Visitante",
                modifier =
                    Modifier.weight(1f),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }
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
            Modifier.height(16.dp)
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
            Modifier.padding(16.dp)
    ) {

        Text(
            "ESTADÍSTICAS",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Partido: ${match.fixture?.id ?: "N/D"}"
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            "Estado: ${match.fixture?.status ?: "N/D"}"
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            "Las estadísticas detalladas se mostrarán "
                    + "cuando estén expuestas por el backend."
        )
    }
}

@Composable
private fun HistoryTab(
    match: MatchUi
) {

    Column(
        modifier =
            Modifier.padding(16.dp)
    ) {

        Text(
            "HISTORIAL",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Modelo utilizado:"
        )

        Text(
            match.prediction
                ?.modelVersion
                ?: "N/D"
        )

        Spacer(
            Modifier.height(12.dp)
        )

        Text(
            "La evaluación histórica del modelo "
                    + "se consulta desde HISTORIAL IA."
        )
    }
}

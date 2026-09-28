package com.nestor.futbolia.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.data.MatchUi
import com.nestor.futbolia.ui.MainDestination
import com.nestor.futbolia.ui.components.BottomNavigationBar
import com.nestor.futbolia.ui.components.MatchCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    repository: FutbolRepository,
    onOpenMatch: (Int) -> Unit,
    onOpenPredictions: () -> Unit,
    onDestinationChanged: (MainDestination) -> Unit
) {

    var matches by remember {
        mutableStateOf<List<MatchUi>>(
            emptyList()
        )
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

            matches =
                repository.loadHome(
                    limit = 10
                )

        } catch (exception: Exception) {

            error =
                exception.message
                    ?: "No se pudieron cargar los partidos"

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
                        "FÚTBOL NESTOR IA"
                    )
                }
            )
        },
        bottomBar = {

            BottomNavigationBar(
                selected =
                    MainDestination.LIGAS,
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
        ) {

            Text(
                text =
                    "Partidos y predicciones",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                modifier =
                    Modifier.padding(
                        16.dp
                    )
            )

            OutlinedButton(
                onClick = {
                    scope.launch {
                        load()
                    }
                },
                modifier =
                    Modifier
                        .padding(
                            horizontal = 16.dp
                        )
            ) {
                Text("ACTUALIZAR")
            }

            when {

                loading -> {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {

                    Column(
                        modifier =
                            Modifier.padding(
                                16.dp
                            )
                    ) {

                        Text(
                            text =
                                error!!,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )

                        Spacer(
                            Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    load()
                                }
                            }
                        ) {
                            Text("REINTENTAR")
                        }
                    }
                }

                matches.isEmpty() -> {

                    Text(
                        text =
                            "No hay partidos próximos disponibles.",
                        modifier =
                            Modifier.padding(
                                16.dp
                            )
                    )
                }

                else -> {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        item {

                            Text(
                                text =
                                    "PRÓXIMOS PARTIDOS",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,
                                modifier =
                                    Modifier.padding(
                                        16.dp
                                    )
                            )
                        }

                        items(
                            matches
                        ) { match ->

                            MatchCard(
                                match = match,
                                onClick = {

                                    val id =
                                        match
                                            .prediction
                                            ?.matchId
                                            ?: match
                                                .fixture
                                                ?.id

                                    if (id != null) {
                                        onOpenMatch(id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

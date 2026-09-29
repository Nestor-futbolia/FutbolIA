package com.nestor.futbolia.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
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
    onDestinationChanged:
        (MainDestination) -> Unit
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

        } catch (e: Exception) {

            error =
                e.message
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

                    Column {

                        Text(
                            "FÚTBOL NESTOR IA"
                        )

                        Text(
                            "Resultados · IA · Análisis",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
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
                    "PARTIDOS",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,

                modifier =
                    Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
            )

            Button(

                onClick = {
                    scope.launch {
                        load()
                    }
                },

                modifier =
                    Modifier.padding(
                        horizontal = 16.dp
                    )
            ) {

                Text(
                    "ACTUALIZAR"
                )
            }

            when {

                loading -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

                error != null -> {

                    Column(
                        modifier =
                            Modifier.padding(
                                16.dp
                            ),

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

                matches.isEmpty() -> {

                    Text(
                        text =
                            "No hay predicciones próximas disponibles.",

                        modifier =
                            Modifier.padding(
                                16.dp
                            )
                    )
                }

                else -> {

                    LazyColumn {

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

                        items(matches) { match ->

                            MatchCard(

                                match = match,

                                onClick = {

                                    val id =
                                        match.prediction
                                            ?.matchId
                                            ?: match.fixture?.id

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

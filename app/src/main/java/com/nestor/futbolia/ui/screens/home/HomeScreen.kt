@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.nestor.futbolia.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
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
    onDestinationChanged: (MainDestination) -> Unit
) {

    var matches by remember {
        mutableStateOf<List<MatchUi>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    suspend fun load() {

        loading = true
        error = null

        try {
            matches = repository.loadHome(
                limit = 10
            )

        } catch (e: Exception) {

            error = e.message
                ?: "No se pudieron cargar los partidos"

        } finally {

            loading = false
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    Scaffold(

        containerColor =
            MaterialTheme.colorScheme.background,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "FÚTBOL NESTOR IA",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Resultados · IA · Análisis",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },

        bottomBar = {

            BottomNavigationBar(
                selected = MainDestination.LIGAS,
                onSelected = onDestinationChanged
            )
        }

    ) { padding ->

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)

        ) {

            item {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp
                            ),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        )

                ) {

                    Column(

                        modifier =
                            Modifier.padding(18.dp)

                    ) {

                        Text(
                            text = "NESTOR IA",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            fontWeight = FontWeight.Bold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Analiza los partidos utilizando los datos disponibles del sistema.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Surface(

                            shape =
                                RoundedCornerShape(14.dp),

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .surface
                                    .copy(
                                        alpha = 0.75f
                                    )

                        ) {

                            Row(

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 14.dp,
                                            vertical = 10.dp
                                        ),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween,

                                verticalAlignment =
                                    Alignment.CenterVertically

                            ) {

                                Text(
                                    text = "PARTIDOS CARGADOS",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelMedium,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )

                                Text(
                                    text =
                                        matches.size.toString(),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 4.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.SpaceBetween

                ) {

                    Column {

                        Text(
                            text = "PARTIDOS",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text = "Próximos partidos",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Button(

                        onClick = {

                            scope.launch {
                                load()
                            }
                        }

                    ) {

                        Text(
                            text = "ACTUALIZAR"
                        )
                    }
                }
            }

            when {

                loading -> {

                    item {

                        Box(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),

                            contentAlignment =
                                Alignment.Center

                        ) {

                            Column(

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)

                            ) {

                                CircularProgressIndicator()

                                Text(
                                    text = "Cargando partidos...",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                error != null -> {

                    item {

                        Card(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp
                                    ),

                            shape =
                                RoundedCornerShape(18.dp)

                        ) {

                            Column(

                                modifier =
                                    Modifier.padding(18.dp),

                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)

                            ) {

                                Text(
                                    text =
                                        "No se pudieron cargar los partidos",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text = error!!,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .error,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )

                                Button(

                                    onClick = {

                                        scope.launch {
                                            load()
                                        }
                                    }

                                ) {

                                    Text(
                                        text = "REINTENTAR"
                                    )
                                }
                            }
                        }
                    }
                }

                matches.isEmpty() -> {

                    item {

                        Card(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp
                                    ),

                            shape =
                                RoundedCornerShape(18.dp)

                        ) {

                            Column(

                                modifier =
                                    Modifier.padding(20.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)

                            ) {

                                Text(
                                    text =
                                        "No hay partidos disponibles",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "NESTOR no recibió partidos próximos desde el backend.",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                else -> {

                    item {

                        Text(
                            text =
                                "PRÓXIMOS PARTIDOS",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.Bold,

                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 2.dp
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

                    item {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }
                }
            }
        }
    }
}

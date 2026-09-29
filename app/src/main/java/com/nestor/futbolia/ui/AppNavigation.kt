package com.nestor.futbolia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nestor.futbolia.data.FutbolRepository
import com.nestor.futbolia.ui.screens.analysis.MatchAnalysisScreen
import com.nestor.futbolia.ui.screens.history.AiHistoryScreen
import com.nestor.futbolia.ui.screens.home.HomeScreen
import com.nestor.futbolia.ui.screens.myanalysis.MyAnalysesScreen

enum class MainDestination {

    LIGAS,

    MIS_ANALISIS,

    HISTORIAL_IA
}

@Composable
fun AppNavigation() {

    val repository =
        remember {
            FutbolRepository()
        }

    var destination by remember {

        mutableStateOf(
            MainDestination.LIGAS
        )
    }

    var selectedMatchId by remember {

        mutableStateOf<Int?>(null)
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        if (selectedMatchId != null) {

            MatchAnalysisScreen(

                matchId =
                    selectedMatchId!!,

                repository =
                    repository,

                onBack = {
                    selectedMatchId =
                        null
                }
            )

        } else {

            when (destination) {

                MainDestination.LIGAS -> {

                    HomeScreen(

                        repository =
                            repository,

                        onOpenMatch = {
                            selectedMatchId =
                                it
                        },

                        onDestinationChanged = {
                            destination =
                                it
                        }
                    )
                }

                MainDestination.MIS_ANALISIS -> {

                    MyAnalysesScreen(

                        onDestinationChanged = {
                            destination =
                                it
                        }
                    )
                }

                MainDestination.HISTORIAL_IA -> {

                    AiHistoryScreen(

                        repository =
                            repository,

                        onDestinationChanged = {
                            destination =
                                it
                        }
                    )
                }
            }
        }
    }
}

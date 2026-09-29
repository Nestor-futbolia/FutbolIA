package com.nestor.futbolia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nestor.futbolia.ui.MainDestination

@Composable
fun BottomNavigationBar(
    selected: MainDestination,
    onSelected: (MainDestination) -> Unit
) {

    NavigationBar {

        NavigationBarItem(

            selected =
                selected ==
                    MainDestination.LIGAS,

            onClick = {
                onSelected(
                    MainDestination.LIGAS
                )
            },

            icon = {
                Icon(
                    Icons.Default.SportsSoccer,
                    contentDescription = "Inicio"
                )
            },

            label = {
                Text("Inicio")
            }
        )

        NavigationBarItem(

            selected =
                selected ==
                    MainDestination.MIS_ANALISIS,

            onClick = {
                onSelected(
                    MainDestination.MIS_ANALISIS
                )
            },

            icon = {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "Mis análisis"
                )
            },

            label = {
                Text("Mis análisis")
            }
        )

        NavigationBarItem(

            selected =
                selected ==
                    MainDestination.HISTORIAL_IA,

            onClick = {
                onSelected(
                    MainDestination.HISTORIAL_IA
                )
            },

            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Historial IA"
                )
            },

            label = {
                Text("Historial IA")
            }
        )
    }
}

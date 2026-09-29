package com.nestor.futbolia.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.nestor.futbolia.ui.theme.FutbolTheme

@Composable
fun FutbolApp() {

    FutbolTheme {

        Surface {

            AppNavigation()
        }
    }
}

package com.futbolia.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "⚽ Fútbol IA 2.0"
            textSize = 28f
            setTextColor(Color.rgb(20, 70, 120))
            gravity = Gravity.CENTER
        }

        val info = TextView(this).apply {
            text = "Motor local de demostración multimercado\n\nIntroduce los equipos y pulsa ANALIZAR."
            textSize = 16f
        }

        val home = EditText(this).apply {
            hint = "Equipo local"
        }

        val away = EditText(this).apply {
            hint = "Equipo visitante"
        }

        val button = Button(this).apply {
            text = "ANALIZAR PARTIDO"
        }

        val result = TextView(this).apply {
            textSize = 17f
            setPadding(0, 24, 0, 0)
        }

        button.setOnClickListener {
            if (home.text.isBlank() || away.text.isBlank()) {
                result.text = "Introduce ambos equipos."
            } else {
                result.text = "Análisis local (prototipo)\n\n" +
                    "Partido: ${home.text} vs ${away.text}\n\n" +
                    "1X2: Local 45% · Empate 29% · Visitante 26%\n" +
                    "Más de 1.5 goles: 72%\n" +
                    "Más de 2.5 goles: 51%\n" +
                    "Ambos anotan: 54%\n" +
                    "Gol 1er tiempo: 63%\n" +
                    "Gol 2º tiempo: 69%\n" +
                    "Córners +8.5: 57%\n" +
                    "Tarjetas +3.5: 55%\n\n" +
                    "⚠️ Estos porcentajes son de demostración. " +
                    "La conexión a datos reales y el entrenamiento histórico aún deben integrarse."
            }
        }

        box.addView(title)
        box.addView(info)
        box.addView(home)
        box.addView(away)
        box.addView(button)
        box.addView(result)

        scroll.addView(box)
        setContentView(scroll)
    }
}

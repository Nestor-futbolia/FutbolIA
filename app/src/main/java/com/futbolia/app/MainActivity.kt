package com.futbolia.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val API_URL =
            "https://futbolia-backend-we7w.onrender.com/ai/predict/upcoming?limit=10"
    }

    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var statusText: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildInterface()

        loadPredictions()
    }

    private fun buildInterface() {

        val scrollView = ScrollView(this)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 40)
            setBackgroundColor(Color.rgb(245, 247, 250))
        }

        scrollView.addView(container)

        setContentView(scrollView)

        val title = TextView(this).apply {
            text = "⚽ FÚTBOL IA"
            textSize = 30f
            setTextColor(Color.rgb(20, 35, 55))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        container.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {
            text = "Predicciones reales de inteligencia artificial"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        container.addView(subtitle)

        progress = ProgressBar(this).apply {
            visibility = View.VISIBLE
        }

        container.addView(
            progress,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        statusText = TextView(this).apply {
            text = "Cargando predicciones..."
            textSize = 15f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }

        container.addView(statusText)

        val refreshButton = Button(this).apply {
            text = "Actualizar predicciones"
            setOnClickListener {
                loadPredictions()
            }
        }

        container.addView(
            refreshButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun loadPredictions() {

        progress.visibility = View.VISIBLE
        statusText.text = "Consultando la IA..."
        statusText.setTextColor(Color.DKGRAY)

        executor.execute {

            try {

                val connection =
                    URL(API_URL).openConnection()
                        as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 30000
                connection.readTimeout = 30000
                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val responseCode =
                    connection.responseCode

                val stream =
                    if (responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val response =
                    stream.bufferedReader()
                        .use { it.readText() }

                connection.disconnect()

                if (responseCode !in 200..299) {

                    throw RuntimeException(
                        "Servidor HTTP $responseCode"
                    )
                }

                val json =
                    JSONObject(response)

                mainHandler.post {

                    progress.visibility = View.GONE

                    showPredictions(json)
                }

            } catch (exception: Exception) {

                mainHandler.post {

                    progress.visibility = View.GONE

                    statusText.text =
                        "No se pudieron cargar las predicciones."

                    statusText.setTextColor(
                        Color.rgb(180, 40, 40)
                    )

                    Toast.makeText(
                        this,
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showPredictions(
        json: JSONObject
    ) {

        val details =
            json.optJSONArray("details")

        if (
            details == null ||
            details.length() == 0
        ) {

            statusText.text =
                "No hay predicciones disponibles."

            return
        }

        statusText.text =
            "Predicciones generadas por la IA"

        statusText.setTextColor(
            Color.rgb(30, 120, 70)
        )

        // Elimina las tarjetas anteriores,
        // conservando título, subtítulo,
        // barra y botón.
        while (container.childCount > 4) {
            container.removeViewAt(4)
        }

        for (index in 0 until details.length()) {

            val item =
                details.optJSONObject(index)
                    ?: continue

            val card =
                createPredictionCard(item)

            container.addView(card)
        }
    }

    private fun createPredictionCard(
        item: JSONObject
    ): View {

        val matchId =
            item.optLong(
                "match_id",
                0
            )

        val prediction =
            item.optString(
                "prediction",
                "N/D"
            )

        val startingAt =
            item.optString(
                "starting_at",
                ""
            )

        val percentages =
            item.optJSONObject(
                "probabilities"
            )

        val home =
            percentages?.optDouble(
                "home",
                0.0
            ) ?: 0.0

        val draw =
            percentages?.optDouble(
                "draw",
                0.0
            ) ?: 0.0

        val away =
            percentages?.optDouble(
                "away",
                0.0
            ) ?: 0.0

        val predictionPercentage =
            item.optDouble(
                "prediction_percentage",
                0.0
            )

        val modelVersion =
            item.optString(
                "model_version",
                ""
            )

        val card =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    28,
                    24,
                    28,
                    24
                )

                setBackgroundColor(
                    Color.WHITE
                )

                elevation = 8f
            }

        val marginParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                setMargins(
                    0,
                    24,
                    0,
                    0
                )
            }

        val matchTitle =
            TextView(this).apply {

                text =
                    "Partido #$matchId"

                textSize = 20f

                setTextColor(
                    Color.rgb(20, 35, 55)
                )

                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )
            }

        card.addView(matchTitle)

        val dateText =
            TextView(this).apply {

                text =
                    formatDate(startingAt)

                textSize = 14f

                setTextColor(
                    Color.GRAY
                )

                setPadding(
                    0,
                    8,
                    0,
                    16
                )
            }

        card.addView(dateText)

        val predictionTitle =
            TextView(this).apply {

                text =
                    "Predicción IA: $prediction"

                textSize = 22f

                setTextColor(
                    Color.rgb(20, 120, 70)
                )

                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )
            }

        card.addView(predictionTitle)

        val confidence =
            TextView(this).apply {

                text =
                    "Probabilidad: " +
                    "${formatPercent(predictionPercentage)}%"

                textSize = 16f

                setTextColor(
                    Color.DKGRAY
                )

                setPadding(
                    0,
                    8,
                    0,
                    16
                )
            }

        card.addView(confidence)

        card.addView(
            probabilityRow(
                "Local",
                home
            )
        )

        card.addView(
            probabilityRow(
                "Empate",
                draw
            )
        )

        card.addView(
            probabilityRow(
                "Visitante",
                away
            )
        )

        val modelText =
            TextView(this).apply {

                text =
                    "Modelo: $modelVersion"

                textSize = 11f

                setTextColor(
                    Color.GRAY
                )

                setPadding(
                    0,
                    18,
                    0,
                    0
                )
            }

        card.addView(modelText)

        card.layoutParams =
            marginParams

        return card
    }

    private fun probabilityRow(
        label: String,
        value: Double
    ): View {

        val row =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    0,
                    6,
                    0,
                    6
                )
            }

        val labelText =
            TextView(this).apply {

                text =
                    label

                textSize = 15f

                setTextColor(
                    Color.DKGRAY
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        row.addView(labelText)

        val valueText =
            TextView(this).apply {

                text =
                    "${formatPercent(value)}%"

                textSize = 15f

                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )

                setTextColor(
                    Color.rgb(20, 35, 55)
                )
            }

        row.addView(valueText)

        return row
    }

    private fun formatPercent(
        value: Double
    ): String {

        return String.format(
            Locale.US,
            "%.2f",
            value * 100.0
        )
    }

    private fun formatDate(
        value: String
    ): String {

        if (value.isBlank()) {
            return "Fecha no disponible"
        }

        return try {

            val input =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                    Locale.US
                )

            input.timeZone =
                TimeZone.getTimeZone("UTC")

            val date =
                input.parse(value)

            val output =
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale("es", "EC")
                )

            if (date != null) {
                output.format(date)
            } else {
                value
            }

        } catch (
            exception: Exception
        ) {

            value
        }
    }

    override fun onDestroy() {

        executor.shutdown()

        super.onDestroy()
    }
}

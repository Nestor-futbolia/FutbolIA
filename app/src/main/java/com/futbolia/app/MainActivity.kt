package com.futbolia.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val apiBaseUrl =
        "https://futbolia-backend-we7w.onrender.com"

    private lateinit var predictionsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var refreshButton: Button

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildInterface()
        loadPredictions()
    }

    private fun buildInterface() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)

        val scrollContainer = android.widget.ScrollView(this)

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "⚽ FÚTBOL IA"
        title.textSize = 32f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.rgb(25, 45, 70))
        title.gravity = Gravity.CENTER

        content.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this)
        subtitle.text = "Predicciones reales de inteligencia artificial"
        subtitle.textSize = 20f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 8, 0, 16)

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        statusText = TextView(this)
        statusText.text = "Cargando predicciones..."
        statusText.textSize = 22f
        statusText.setTypeface(null, Typeface.BOLD)
        statusText.setTextColor(Color.rgb(40, 120, 80))
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 8, 0, 16)

        content.addView(
            statusText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        refreshButton = Button(this)
        refreshButton.text = "ACTUALIZAR PREDICCIONES"
        refreshButton.textSize = 18f
        refreshButton.setOnClickListener {
            loadPredictions()
        }

        content.addView(
            refreshButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        progressBar = ProgressBar(this)
        progressBar.visibility = View.VISIBLE

        val progressParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressParams.gravity = Gravity.CENTER
        progressParams.setMargins(0, 16, 0, 16)

        content.addView(progressBar, progressParams)

        predictionsContainer = LinearLayout(this)
        predictionsContainer.orientation = LinearLayout.VERTICAL

        val predictionsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        predictionsParams.setMargins(0, 16, 0, 0)

        content.addView(
            predictionsContainer,
            predictionsParams
        )

        scrollContainer.addView(content)

        root.addView(
            scrollContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
    }

    private fun loadPredictions() {

        progressBar.visibility = View.VISIBLE
        refreshButton.isEnabled = false

        statusText.text = "Cargando predicciones de la IA..."
        statusText.setTextColor(Color.rgb(40, 120, 80))

        predictionsContainer.removeAllViews()

        executor.execute {

            try {

                val url =
                    "$apiBaseUrl/ai/predict/upcoming?limit=10"

                val response = httpGet(url)

                val json = JSONObject(response)

                val ok = json.optBoolean("ok", false)

                if (!ok) {
                    throw Exception(
                        json.optString(
                            "detail",
                            "El servidor devolvió un error."
                        )
                    )
                }

                val details = json.optJSONArray("details")
                    ?: JSONArray()

                val cards = ArrayList<JSONObject>()

                for (i in 0 until details.length()) {

                    val detail = details.optJSONObject(i)
                        ?: continue

                    val matchId =
                        detail.optLong("match_id", 0L)

                    if (matchId <= 0L) {
                        continue
                    }

                    val prediction =
                        detail.optString("prediction", "")

                    val probabilities =
                        detail.optJSONObject("probabilities")

                    val hasPredictionData =
                        prediction.isNotBlank() &&
                        probabilities != null &&
                        probabilities.has("home") &&
                        probabilities.has("draw") &&
                        probabilities.has("away")

                    if (hasPredictionData) {

                        cards.add(detail)

                    } else {

                        /*
                         * IMPORTANTE:
                         *
                         * Si /ai/predict/upcoming devuelve
                         * "skipped" porque la predicción ya existe,
                         * consultamos la predicción guardada directamente.
                         */
                        try {

                            val storedUrl =
                                "$apiBaseUrl/ai/predict/$matchId"

                            val storedResponse =
                                httpGet(storedUrl)

                            val storedJson =
                                JSONObject(storedResponse)

                            if (storedJson.optBoolean("ok", false)) {

                                val merged =
                                    JSONObject(detail.toString())

                                copyIfExists(
                                    storedJson,
                                    merged,
                                    "prediction"
                                )

                                copyIfExists(
                                    storedJson,
                                    merged,
                                    "prediction_percentage"
                                )

                                copyIfExists(
                                    storedJson,
                                    merged,
                                    "model_version"
                                )

                                val storedProbabilities =
                                    storedJson.optJSONObject(
                                        "probabilities"
                                    )

                                if (storedProbabilities != null) {

                                    merged.put(
                                        "probabilities",
                                        storedProbabilities
                                    )
                                }

                                cards.add(merged)

                            } else {

                                cards.add(detail)
                            }

                        } catch (_: Exception) {

                            /*
                             * Si un partido concreto falla,
                             * mantenemos el registro original.
                             */
                            cards.add(detail)
                        }
                    }
                }

                mainHandler.post {

                    progressBar.visibility = View.GONE
                    refreshButton.isEnabled = true

                    if (cards.isEmpty()) {

                        statusText.text =
                            "No hay predicciones disponibles."

                        statusText.setTextColor(
                            Color.rgb(180, 70, 70)
                        )

                    } else {

                        statusText.text =
                            "Predicciones reales de la IA: ${cards.size}"

                        statusText.setTextColor(
                            Color.rgb(40, 120, 80)
                        )

                        for (card in cards) {
                            addPredictionCard(card)
                        }
                    }
                }

            } catch (e: Exception) {

                mainHandler.post {

                    progressBar.visibility = View.GONE
                    refreshButton.isEnabled = true

                    statusText.text =
                        "No se pudieron cargar las predicciones."

                    statusText.setTextColor(
                        Color.rgb(180, 50, 50)
                    )

                    addErrorCard(
                        e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    private fun copyIfExists(
        source: JSONObject,
        target: JSONObject,
        key: String
    ) {
        if (source.has(key) && !source.isNull(key)) {
            target.put(key, source.get(key))
        }
    }

    private fun addPredictionCard(data: JSONObject) {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(16, 16, 16, 16)
        card.setBackgroundColor(Color.rgb(250, 250, 250))

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.setMargins(0, 0, 0, 12)

        card.layoutParams = cardParams

        val matchId =
            data.optLong("match_id", 0L)

        val startingAt =
            data.optString("starting_at", "")

        val prediction =
            data.optString("prediction", "N/D")

        val predictionPercentage =
            data.optDouble(
                "prediction_percentage",
                0.0
            )

        val probabilities =
            data.optJSONObject("probabilities")

        val homeProbability =
            probabilities?.optDouble(
                "home",
                0.0
            ) ?: 0.0

        val drawProbability =
            probabilities?.optDouble(
                "draw",
                0.0
            ) ?: 0.0

        val awayProbability =
            probabilities?.optDouble(
                "away",
                0.0
            ) ?: 0.0

        val modelVersion =
            data.optString(
                "model_version",
                "N/D"
            )

        val matchTitle = TextView(this)
        matchTitle.text =
            "Partido #$matchId"
        matchTitle.textSize = 26f
        matchTitle.setTypeface(
            null,
            Typeface.BOLD
        )
        matchTitle.setTextColor(
            Color.rgb(25, 45, 70)
        )

        card.addView(matchTitle)

        val dateText = TextView(this)
        dateText.text =
            formatDate(startingAt)
        dateText.textSize = 18f
        dateText.setTextColor(Color.GRAY)
        dateText.setPadding(0, 4, 0, 14)

        card.addView(dateText)

        val predictionText = TextView(this)

        predictionText.text =
            "Predicción IA: $prediction"

        predictionText.textSize = 27f
        predictionText.setTypeface(
            null,
            Typeface.BOLD
        )
        predictionText.setTextColor(
            Color.rgb(35, 125, 80)
        )

        card.addView(predictionText)

        val confidenceText = TextView(this)

        confidenceText.text =
            "Probabilidad: ${
                formatPercent(predictionPercentage / 100.0)
            }"

        confidenceText.textSize = 21f
        confidenceText.setTextColor(Color.DKGRAY)
        confidenceText.setPadding(0, 6, 0, 10)

        card.addView(confidenceText)

        addProbabilityRow(
            card,
            "Local",
            homeProbability
        )

        addProbabilityRow(
            card,
            "Empate",
            drawProbability
        )

        addProbabilityRow(
            card,
            "Visitante",
            awayProbability
        )

        val modelText = TextView(this)

        modelText.text =
            "Modelo: $modelVersion"

        modelText.textSize = 16f
        modelText.setTextColor(Color.GRAY)
        modelText.setPadding(0, 10, 0, 0)

        card.addView(modelText)

        predictionsContainer.addView(card)
    }

    private fun addProbabilityRow(
        parent: LinearLayout,
        label: String,
        probability: Double
    ) {

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val labelText = TextView(this)

        labelText.text = label
        labelText.textSize = 20f
        labelText.setTextColor(
            Color.rgb(45, 45, 45)
        )

        val labelParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        row.addView(
            labelText,
            labelParams
        )

        val valueText = TextView(this)

        valueText.text =
            formatPercent(probability)

        valueText.textSize = 20f
        valueText.setTypeface(
            null,
            Typeface.BOLD
        )
        valueText.setTextColor(
            Color.rgb(25, 45, 70)
        )
        valueText.gravity = Gravity.END

        row.addView(
            valueText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun addErrorCard(message: String) {

        val errorText = TextView(this)

        errorText.text =
            "Error: $message"

        errorText.textSize = 16f
        errorText.setTextColor(
            Color.rgb(180, 50, 50)
        )
        errorText.setPadding(
            16,
            16,
            16,
            16
        )

        predictionsContainer.addView(
            errorText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun formatPercent(value: Double): String {

        return String.format(
            Locale.US,
            "%.2f%%",
            value * 100.0
        )
    }

    private fun formatDate(value: String): String {

        if (value.isBlank()) {
            return "Fecha no disponible"
        }

        return try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                    Locale.US
                )

            inputFormat.timeZone =
                TimeZone.getTimeZone("UTC")

            val date: Date =
                inputFormat.parse(value)
                    ?: return value

            val outputFormat =
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.US
                )

            outputFormat.timeZone =
                TimeZone.getDefault()

            outputFormat.format(date)

        } catch (_: Exception) {

            value
        }
    }

    private fun httpGet(urlString: String): String {

        val url = URL(urlString)

        val connection =
            url.openConnection()
                as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 20000
        connection.readTimeout = 30000
        connection.useCaches = false

        return try {

            val responseCode =
                connection.responseCode

            val stream =
                if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val body =
                stream?.bufferedReader()?.use {
                    it.readText()
                } ?: ""

            if (responseCode !in 200..299) {

                throw Exception(
                    "HTTP $responseCode: $body"
                )
            }

            body

        } finally {

            connection.disconnect()
        }
    }

    override fun onDestroy() {

        executor.shutdownNow()

        super.onDestroy()
    }
}

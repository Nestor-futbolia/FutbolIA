package com.futbolia.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Executors

class MainActivity : Activity() {

    companion object {
        private const val API_URL =
            "https://futbolia-backend-we7w.onrender.com/ai/predict/upcoming?limit=10"
    }

    private lateinit var mainContainer: LinearLayout
    private lateinit var predictionsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createInterface()
        loadPredictions()
    }

    private fun createInterface() {

        val scrollView = ScrollView(this)

        mainContainer = LinearLayout(this)
        mainContainer.orientation = LinearLayout.VERTICAL

        mainContainer.setPadding(
            32,
            40,
            32,
            40
        )

        mainContainer.setBackgroundColor(
            Color.rgb(245, 247, 250)
        )

        scrollView.addView(mainContainer)

        setContentView(scrollView)

        val title = TextView(this)

        title.text = "⚽ FÚTBOL IA"
        title.textSize = 30f
        title.setTextColor(
            Color.rgb(20, 35, 55)
        )

        title.gravity = Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        mainContainer.addView(title)

        val subtitle = TextView(this)

        subtitle.text =
            "Predicciones reales de inteligencia artificial"

        subtitle.textSize = 16f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        subtitle.setPadding(
            0,
            12,
            0,
            24
        )

        mainContainer.addView(subtitle)

        progressBar = ProgressBar(this)

        progressBar.visibility = View.VISIBLE

        val progressParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        progressParams.gravity = Gravity.CENTER

        mainContainer.addView(
            progressBar,
            progressParams
        )

        statusText = TextView(this)

        statusText.text =
            "Conectando con Fútbol IA..."

        statusText.textSize = 15f
        statusText.setTextColor(Color.DKGRAY)
        statusText.gravity = Gravity.CENTER

        statusText.setPadding(
            0,
            16,
            0,
            16
        )

        mainContainer.addView(statusText)

        val refreshButton = Button(this)

        refreshButton.text =
            "Actualizar predicciones"

        refreshButton.setOnClickListener {
            loadPredictions()
        }

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        mainContainer.addView(
            refreshButton,
            buttonParams
        )

        predictionsContainer = LinearLayout(this)

        predictionsContainer.orientation =
            LinearLayout.VERTICAL

        mainContainer.addView(
            predictionsContainer
        )
    }

    private fun loadPredictions() {

        progressBar.visibility = View.VISIBLE

        statusText.text =
            "Consultando predicciones de la IA..."

        statusText.setTextColor(
            Color.DKGRAY
        )

        executor.execute {

            var connection: HttpURLConnection? = null

            try {

                val url = URL(API_URL)

                connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val responseCode =
                    connection.responseCode

                val inputStream =
                    if (
                        responseCode >= 200 &&
                        responseCode < 300
                    ) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val response =
                    if (inputStream != null) {
                        inputStream
                            .bufferedReader()
                            .use {
                                it.readText()
                            }
                    } else {
                        ""
                    }

                if (
                    responseCode < 200 ||
                    responseCode >= 300
                ) {
                    throw Exception(
                        "Servidor HTTP $responseCode"
                    )
                }

                val json = JSONObject(response)

                mainHandler.post {

                    progressBar.visibility =
                        View.GONE

                    showPredictions(json)
                }

            } catch (error: Exception) {

                mainHandler.post {

                    progressBar.visibility =
                        View.GONE

                    statusText.text =
                        "No se pudieron cargar las predicciones."

                    statusText.setTextColor(
                        Color.rgb(180, 40, 40)
                    )

                    Toast.makeText(
                        this,
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {

                connection?.disconnect()
            }
        }
    }

    private fun showPredictions(
        json: JSONObject
    ) {

        predictionsContainer.removeAllViews()

        val details =
            findDetailsArray(json)

        if (
            details == null ||
            details.length() == 0
        ) {

            statusText.text =
                "No hay predicciones disponibles."

            statusText.setTextColor(
                Color.DKGRAY
            )

            return
        }

        statusText.text =
            "Predicciones reales de la IA: ${details.length()}"

        statusText.setTextColor(
            Color.rgb(30, 120, 70)
        )

        for (
            index in 0 until details.length()
        ) {

            val item =
                details.optJSONObject(index)

            if (item != null) {

                val card =
                    createPredictionCard(item)

                predictionsContainer.addView(card)
            }
        }
    }

    private fun findDetailsArray(
        json: JSONObject
    ): JSONArray? {

        val possibleNames = arrayOf(
            "details",
            "predictions",
            "matches",
            "data",
            "results"
        )

        for (name in possibleNames) {

            val array =
                json.optJSONArray(name)

            if (array != null) {
                return array
            }
        }

        return null
    }

    private fun createPredictionCard(
        item: JSONObject
    ): View {

        val matchId =
            readLong(
                item,
                "match_id",
                "fixture_id",
                "id"
            )

        val startingAt =
            readString(
                item,
                "starting_at",
                "startingAt",
                "fixture_date",
                "date",
                "match_date"
            )

        val prediction =
            readString(
                item,
                "prediction",
                "predicted",
                "result",
                "selection",
                "pick"
            ).ifBlank {
                "N/D"
            }

        val predictionPercentage =
            readProbability(
                item,
                "prediction_percentage",
                "predictionPercentage",
                "probability",
                "confidence",
                "percentage"
            )

        val modelVersion =
            readString(
                item,
                "model_version",
                "modelVersion",
                "version"
            ).ifBlank {
                "N/D"
            }

        val probabilities =
            item.optJSONObject(
                "probabilities"
            )

        val home =
            readProbability(
                probabilities,
                "home",
                "local",
                "HOME"
            )

        val draw =
            readProbability(
                probabilities,
                "draw",
                "empate",
                "DRAW"
            )

        val away =
            readProbability(
                probabilities,
                "away",
                "visitante",
                "AWAY"
            )

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            28,
            24,
            28,
            24
        )

        card.setBackgroundColor(
            Color.WHITE
        )

        card.elevation = 6f

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            24,
            0,
            0
        )

        card.layoutParams = cardParams

        val matchText =
            TextView(this)

        matchText.text =
            "Partido #$matchId"

        matchText.textSize = 20f

        matchText.setTextColor(
            Color.rgb(20, 35, 55)
        )

        matchText.setTypeface(
            null,
            Typeface.BOLD
        )

        card.addView(matchText)

        val dateText =
            TextView(this)

        dateText.text =
            formatDate(startingAt)

        dateText.textSize = 14f

        dateText.setTextColor(
            Color.GRAY
        )

        dateText.setPadding(
            0,
            8,
            0,
            16
        )

        card.addView(dateText)

        val predictionText =
            TextView(this)

        predictionText.text =
            "Predicción IA: $prediction"

        predictionText.textSize = 22f

        predictionText.setTextColor(
            Color.rgb(20, 120, 70)
        )

        predictionText.setTypeface(
            null,
            Typeface.BOLD
        )

        card.addView(predictionText)

        val confidenceText =
            TextView(this)

        confidenceText.text =
            "Probabilidad: " +
                    formatPercent(predictionPercentage) +
                    "%"

        confidenceText.textSize = 17f

        confidenceText.setTextColor(
            Color.DKGRAY
        )

        confidenceText.setPadding(
            0,
            8,
            0,
            16
        )

        card.addView(confidenceText)

        card.addView(
            createProbabilityRow(
                "Local",
                home
            )
        )

        card.addView(
            createProbabilityRow(
                "Empate",
                draw
            )
        )

        card.addView(
            createProbabilityRow(
                "Visitante",
                away
            )
        )

        val modelText =
            TextView(this)

        modelText.text =
            "Modelo: $modelVersion"

        modelText.textSize = 11f

        modelText.setTextColor(
            Color.GRAY
        )

        modelText.setPadding(
            0,
            18,
            0,
            0
        )

        card.addView(modelText)

        return card
    }

    private fun readString(
        obj: JSONObject?,
        vararg names: String
    ): String {

        if (obj == null) {
            return ""
        }

        for (name in names) {

            if (obj.has(name)) {

                val value =
                    obj.opt(name)

                if (
                    value != null &&
                    value != JSONObject.NULL
                ) {

                    val text =
                        value.toString()

                    if (text.isNotBlank()) {
                        return text
                    }
                }
            }
        }

        return ""
    }

    private fun readLong(
        obj: JSONObject?,
        vararg names: String
    ): Long {

        if (obj == null) {
            return 0L
        }

        for (name in names) {

            if (obj.has(name)) {

                val value =
                    obj.opt(name)

                if (value is Number) {
                    return value.toLong()
                }

                val text =
                    value?.toString()

                val parsed =
                    text?.toLongOrNull()

                if (parsed != null) {
                    return parsed
                }
            }
        }

        return 0L
    }

    private fun readProbability(
        obj: JSONObject?,
        vararg names: String
    ): Double {

        if (obj == null) {
            return 0.0
        }

        for (name in names) {

            if (!obj.has(name)) {
                continue
            }

            val value =
                obj.opt(name)

            val number =
                when (value) {

                    is Number ->
                        value.toDouble()

                    else ->
                        value
                            ?.toString()
                            ?.replace("%", "")
                            ?.replace(",", ".")
                            ?.toDoubleOrNull()
                            ?: 0.0
                }

            if (number > 1.0) {
                return number / 100.0
            }

            if (number >= 0.0) {
                return number
            }
        }

        return 0.0
    }

    private fun createProbabilityRow(
        label: String,
        probability: Double
    ): View {

        val row =
            LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            0,
            6,
            0,
            6
        )

        val labelText =
            TextView(this)

        labelText.text =
            label

        labelText.textSize = 15f

        labelText.setTextColor(
            Color.DKGRAY
        )

        val labelParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        labelParams.weight = 1f

        row.addView(
            labelText,
            labelParams
        )

        val percentageText =
            TextView(this)

        percentageText.text =
            formatPercent(probability) +
                    "%"

        percentageText.textSize = 15f

        percentageText.setTextColor(
            Color.rgb(20, 35, 55)
        )

        percentageText.setTypeface(
            null,
            Typeface.BOLD
        )

        row.addView(
            percentageText
        )

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

            val formats = arrayOf(
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss"
            )

            var date: java.util.Date? = null

            for (pattern in formats) {

                try {

                    val input =
                        SimpleDateFormat(
                            pattern,
                            Locale.US
                        )

                    input.timeZone =
                        TimeZone.getTimeZone("UTC")

                    date =
                        input.parse(value)

                    if (date != null) {
                        break
                    }

                } catch (_: Exception) {
                    // Intentar el siguiente formato.
                }
            }

            if (date == null) {
                return value
            }

            val output =
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale("es", "EC")
                )

            output.format(date)

        } catch (_: Exception) {

            value
        }
    }

    override fun onDestroy() {

        executor.shutdown()

        super.onDestroy()
    }
}

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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var container: LinearLayout
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

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 40)
            setBackgroundColor(
                Color.rgb(245, 247, 250)
            )
        }

        scrollView.addView(container)

        setContentView(scrollView)

        val title = TextView(this).apply {
            text = "⚽ FÚTBOL IA"
            textSize = 30f
            setTextColor(
                Color.rgb(20, 35, 55)
            )
            gravity = Gravity.CENTER
            setTypeface(
                null,
                Typeface.BOLD
            )
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

        progressBar = ProgressBar(this).apply {
            visibility = View.VISIBLE
        }

        val progressParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        progressParams.gravity = Gravity.CENTER

        container.addView(
            progressBar,
            progressParams
        )

        statusText = TextView(this).apply {
            text = "Conectando con Fútbol IA..."
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

        progressBar.visibility = View.VISIBLE

        statusText.text =
            "Consultando predicciones de la IA..."

        statusText.setTextColor(
            Color.DKGRAY
        )

        executor.execute {

            var connection: HttpURLConnection? = null

            try {

                connection =
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

                val inputStream =
                    if (responseCode >= 200 &&
                        responseCode < 300
                    ) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val response =
                    inputStream
                        ?.bufferedReader()
                        ?.use {
                            it.readText()
                        }
                        ?: ""

                if (responseCode < 200 ||
                    responseCode >= 300
                ) {

                    throw Exception(
                        "Servidor HTTP $responseCode"
                    )
                }

                val json =
                    JSONObject(response)

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

        val details =
            json.optJSONArray("details")

        if (details == null ||
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
            "Predicciones reales de la IA"

        statusText.setTextColor(
            Color.rgb(30, 120, 70)
        )

        removeOldCards()

        for (i in 0 until details.length()) {

            val item =
                details.optJSONObject(i)
                    ?: continue

            val card =
                createPredictionCard(item)

            container.addView(card)
        }
    }

    private fun removeOldCards() {

        while (container.childCount > 5) {
            container.removeViewAt(5)
        }
    }

    private fun createPredictionCard(
        item: JSONObject
    ): View {

        val matchId =
            item.optLong(
                "match_id",
                0L
            )

        val startingAt =
            item.optString(
                "starting_at",
                ""
            )

        val prediction =
            item.optString(
                "prediction",
                "N/D"
            )

        val predictionPercentage =
            item.optDouble(
                "prediction_percentage",
                0.0
            )

        val modelVersion =
            item.optString(
                "model_version",
                "N/D"
            )

        val probabilities =
            item.optJSONObject(
                "probabilities"
            )

        val home =
            probabilities?.optDouble(
                "home",
                0.0
            ) ?: 0.0

        val draw =
            probabilities?.optDouble(
                "draw",
                0.0
            ) ?: 0.0

        val away =
            probabilities?.optDouble(
                "away",
                0.0
            ) ?: 0.0

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

                elevation = 6f
            }

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT

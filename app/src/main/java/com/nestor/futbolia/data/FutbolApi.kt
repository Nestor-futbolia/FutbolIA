package com.nestor.futbolia.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class FutbolApi {

    companion object {
        const val BASE_URL =
            "https://futbolia-backend-we7w.onrender.com"
    }

    private suspend fun get(path: String): String =
        withContext(Dispatchers.IO) {

            val url = URL("$BASE_URL$path")

            val connection =
                url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 20_000
                connection.readTimeout = 30_000

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val responseCode = connection.responseCode

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
                        "Error HTTP $responseCode"
                    )
                }

                body

            } finally {
                connection.disconnect()
            }
        }

    suspend fun getUpcomingPredictions(
        limit: Int = 10
    ): List<PredictionDetail> {

        val response =
            get("/ai/predict/upcoming?limit=$limit")

        val root = JSONObject(response)

        val details =
            root.optJSONArray("details")
                ?: JSONArray()

        val result =
            mutableListOf<PredictionDetail>()

        for (i in 0 until details.length()) {

            val item =
                details.optJSONObject(i)
                    ?: continue

            if (!item.optBoolean("ok", false)) {
                continue
            }

            val probabilities =
                item.optJSONObject("probabilities")

            val percentages =
                item.optJSONObject("percentages")

            result.add(
                PredictionDetail(

                    matchId =
                        item.optInt("match_id"),

                    startingAt =
                        item.optString(
                            "starting_at",
                            null
                        ),

                    prediction =
                        item.optString(
                            "prediction",
                            "N/D"
                        ),

                    predictionProbability =
                        item.optDouble(
                            "prediction_probability",
                            0.0
                        ),

                    predictionPercentage =
                        item.optDouble(
                            "prediction_percentage",
                            0.0
                        ),

                    probabilities =
                        PredictionProbabilities(
                            home =
                                probabilities?.optDouble(
                                    "home",
                                    0.0
                                ) ?: 0.0,

                            draw =
                                probabilities?.optDouble(
                                    "draw",
                                    0.0
                                ) ?: 0.0,

                            away =
                                probabilities?.optDouble(
                                    "away",
                                    0.0
                                ) ?: 0.0
                        ),

                    percentages =
                        PredictionPercentages(
                            home =
                                percentages?.optDouble(
                                    "home",
                                    0.0
                                ) ?: 0.0,

                            draw =
                                percentages?.optDouble(
                                    "draw",
                                    0.0
                                ) ?: 0.0,

                            away =
                                percentages?.optDouble(
                                    "away",
                                    0.0
                                ) ?: 0.0
                        ),

                    modelVersion =
                        item.optString(
                            "model_version",
                            "N/D"
                        )
                )
            )
        }

        return result
    }

    suspend fun getPrediction(
        matchId: Int
    ): PredictionDetail {

        val response =
            get("/ai/predict/$matchId")

        val root =
            JSONObject(response)

        val probabilities =
            root.optJSONObject("probabilities")

        val percentages =
            root.optJSONObject("percentages")

        return PredictionDetail(

            matchId =
                root.optInt("match_id"),

            startingAt =
                root.optString(
                    "starting_at",
                    null
                ),

            prediction =
                root.optString(
                    "prediction",
                    "N/D"
                ),

            predictionProbability =
                root.optDouble(
                    "prediction_probability",
                    0.0
                ),

            predictionPercentage =
                root.optDouble(
                    "prediction_percentage",
                    0.0
                ),

            probabilities =
                PredictionProbabilities(
                    home =
                        probabilities?.optDouble(
                            "home",
                            0.0
                        ) ?: 0.0,

                    draw =
                        probabilities?.optDouble(
                            "draw",
                            0.0
                        ) ?: 0.0,

                    away =
                        probabilities?.optDouble(
                            "away",
                            0.0
                        ) ?: 0.0
                ),

            percentages =
                PredictionPercentages(
                    home =
                        percentages?.optDouble(
                            "home",
                            0.0
                        ) ?: 0.0,

                    draw =
                        percentages?.optDouble(
                            "draw",
                            0.0
                        ) ?: 0.0,

                    away =
                        percentages?.optDouble(
                            "away",
                            0.0
                        ) ?: 0.0
                ),

            modelVersion =
                root.optString(
                    "model_version",
                    "N/D"
                )
        )
    }

    suspend fun getFixtures(
        next: Int = 20
    ): List<FixtureInfo> {

        val response =
            get("/fixtures?next=$next")

        val root =
            JSONObject(response)

        val array =
            root.optJSONArray("response")
                ?: JSONArray()

        val result =
            mutableListOf<FixtureInfo>()

        for (i in 0 until array.length()) {

            val item =
                array.optJSONObject(i)
                    ?: continue

            val fixture =
                item.optJSONObject("fixture")
                    ?: continue

            val teams =
                item.optJSONObject("teams")
                    ?: continue

            val home =
                teams.optJSONObject("home")
                    ?: continue

            val away =
                teams.optJSONObject("away")
                    ?: continue

            val league =
                item.optJSONObject("league")

            val goals =
                item.optJSONObject("goals")

            result.add(
                FixtureInfo(

                    id =
                        fixture.optInt("id"),

                    startingAt =
                        fixture.optString(
                            "date",
                            null
                        ),

                    status =
                        fixture
                            .optJSONObject("status")
                            ?.optString(
                                "short",
                                null
                            ),

                    homeTeam =
                        TeamInfo(
                            id =
                                home.optInt("id"),

                            name =
                                home.optString(
                                    "name",
                                    "Local"
                                ),

                            code =
                                home.optString(
                                    "code",
                                    null
                                ),

                            logo =
                                home.optString(
                                    "logo",
                                    null
                                )
                        ),

                    awayTeam =
                        TeamInfo(
                            id =
                                away.optInt("id"),

                            name =
                                away.optString(
                                    "name",
                                    "Visitante"
                                ),

                            code =
                                away.optString(
                                    "code",
                                    null
                                ),

                            logo =
                                away.optString(
                                    "logo",
                                    null
                                )
                        ),

                    homeGoals =
                        if (
                            goals?.isNull("home") == true
                        ) {
                            null
                        } else {
                            goals?.optInt("home")
                        },

                    awayGoals =
                        if (
                            goals?.isNull("away") == true
                        ) {
                            null
                        } else {
                            goals?.optInt("away")
                        },

                    leagueName =
                        league?.optString(
                            "name",
                            null
                        ),

                    leagueCountry =
                        league?.optString(
                            "country",
                            null
                        )
                )
            )
        }

        return result
    }

    suspend fun getFixture(
        matchId: Int
    ): FixtureInfo? {

        val response =
            get("/fixtures/$matchId")

        val root =
            JSONObject(response)

        val array =
            root.optJSONArray("response")
                ?: return null

        if (array.length() == 0) {
            return null
        }

        val item =
            array.optJSONObject(0)
                ?: return null

        val fixture =
            item.optJSONObject("fixture")
                ?: return null

        val teams =
            item.optJSONObject("teams")
                ?: return null

        val home =
            teams.optJSONObject("home")
                ?: return null

        val away =
            teams.optJSONObject("away")
                ?: return null

        val league =
            item.optJSONObject("league")

        val goals =
            item.optJSONObject("goals")

        return FixtureInfo(

            id =
                fixture.optInt("id"),

            startingAt =
                fixture.optString(
                    "date",
                    null
                ),

            status =
                fixture
                    .optJSONObject("status")
                    ?.optString(
                        "short",
                        null
                    ),

            homeTeam =
                TeamInfo(
                    id =
                        home.optInt("id"),

                    name =
                        home.optString(
                            "name",
                            "Local"
                        ),

                    code =
                        home.optString(
                            "code",
                            null
                        ),

                    logo =
                        home.optString(
                            "logo",
                            null
                        )
                ),

            awayTeam =
                TeamInfo(
                    id =
                        away.optInt("id"),

                    name =
                        away.optString(
                            "name",
                            "Visitante"
                        ),

                    code =
                        away.optString(
                            "code",
                            null
                        ),

                    logo =
                        away.optString(
                            "logo",
                            null
                        )
                ),

            homeGoals =
                if (goals?.isNull("home") == true) {
                    null
                } else {
                    goals?.optInt("home")
                },

            awayGoals =
                if (goals?.isNull("away") == true) {
                    null
                } else {
                    goals?.optInt("away")
                },

            leagueName =
                league?.optString(
                    "name",
                    null
                ),

            leagueCountry =
                league?.optString(
                    "country",
                    null
                )
        )
    }

    suspend fun getAiStatus(): AiStatus {

        val response =
            get("/ai/status")

        val root =
            JSONObject(response)

        return AiStatus(

            active =
                root.optBoolean(
                    "active",
                    false
                ),

            modelVersion =
                root.optString(
                    "model_version",
                    null
                ),

            modelName =
                root.optString(
                    "model_name",
                    null
                ),

            trainedAt =
                root.optString(
                    "trained_at",
                    null
                ),

            trainingMatches =
                if (
                    root.has("training_matches")
                ) {
                    root.optInt(
                        "training_matches"
                    )
                } else {
                    null
                },

            validationAccuracy =
                if (
                    root.has("validation_accuracy")
                ) {
                    root.optDouble(
                        "validation_accuracy"
                    )
                } else {
                    null
                },

            validationLogLoss =
                if (
                    root.has("validation_log_loss")
                ) {
                    root.optDouble(
                        "validation_log_loss"
                    )
                } else {
                    null
                }
        )
    }

    suspend fun getPerformance(): AiPerformance {

        val response =
            get("/ai/performance")

        val root =
            JSONObject(response)

        return AiPerformance(

            evaluatedMatches =
                root.optInt(
                    "evaluated_matches",
                    0
                ),

            correctMatches =
                root.optInt(
                    "correct_matches",
                    0
                ),

            accuracy =
                if (root.has("accuracy")) {
                    root.optDouble("accuracy")
                } else {
                    null
                },

            accuracyPercent =
                if (
                    root.has("accuracy_percent")
                ) {
                    root.optDouble(
                        "accuracy_percent"
                    )
                } else {
                    null
                },

            logLoss =
                if (root.has("log_loss")) {
                    root.optDouble("log_loss")
                } else {
                    null
                },

            brierScore =
                if (
                    root.has("brier_score")
                ) {
                    root.optDouble(
                        "brier_score"
                    )
                } else {
                    null
                }
        )
    }
}

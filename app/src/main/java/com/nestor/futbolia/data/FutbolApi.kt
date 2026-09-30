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

            val connection =
                (URL("$BASE_URL$path").openConnection()
                        as HttpURLConnection)

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 20000
                connection.readTimeout = 30000
                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val code = connection.responseCode

                val stream =
                    if (code in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val body =
                    stream?.bufferedReader()?.use {
                        it.readText()
                    } ?: ""

                if (code !in 200..299) {
                    throw Exception(
                        "Servidor respondió HTTP $code: $body"
                    )
                }

                body

            } finally {
                connection.disconnect()
            }
        }

    private fun probability(
        json: JSONObject?,
        key: String
    ): Double {

        if (json == null) {
            return 0.0
        }

        if (json.has(key)) {
            return json.optDouble(
                key,
                0.0
            )
        }

        val lower = key.lowercase()

        if (json.has(lower)) {
            return json.optDouble(
                lower,
                0.0
            )
        }

        return 0.0
    }

    private fun percentage(
        percentages: JSONObject?,
        probabilities: JSONObject?,
        key: String
    ): Double {

        if (percentages != null) {

            if (percentages.has(key)) {
                return percentages.optDouble(
                    key,
                    0.0
                )
            }

            val lower = key.lowercase()

            if (percentages.has(lower)) {
                return percentages.optDouble(
                    lower,
                    0.0
                )
            }
        }

        return probability(
            probabilities,
            key
        ) * 100.0
    }

    private fun parsePrediction(
        root: JSONObject
    ): PredictionDetail {

        val probabilities =
            root.optJSONObject(
                "probabilities"
            )

        val percentages =
            root.optJSONObject(
                "percentages"
            )

        val selectedProbability =
            if (root.has("probability")) {
                root.optDouble(
                    "probability",
                    0.0
                )
            } else {
                root.optDouble(
                    "prediction_probability",
                    0.0
                )
            }

        val selectedPercentage =
            if (root.has("prediction_percentage")) {
                root.optDouble(
                    "prediction_percentage",
                    selectedProbability * 100.0
                )
            } else {
                selectedProbability * 100.0
            }

        val startingAtValue =
            if (
                root.has("starting_at") &&
                !root.isNull("starting_at")
            ) {
                root.optString(
                    "starting_at"
                ).takeIf {
                    it.isNotBlank()
                }
            } else {
                null
            }

        return PredictionDetail(

            matchId =
                root.optInt(
                    "match_id",
                    0
                ),

            startingAt =
                startingAtValue,

            prediction =
                root.optString(
                    "prediction",
                    "N/D"
                ),

            predictionProbability =
                selectedProbability,

            predictionPercentage =
                selectedPercentage,

            probabilities =
                PredictionProbabilities(

                    home =
                        probability(
                            probabilities,
                            "HOME"
                        ),

                    draw =
                        probability(
                            probabilities,
                            "DRAW"
                        ),

                    away =
                        probability(
                            probabilities,
                            "AWAY"
                        )
                ),

            percentages =
                PredictionPercentages(

                    home =
                        percentage(
                            percentages,
                            probabilities,
                            "HOME"
                        ),

                    draw =
                        percentage(
                            percentages,
                            probabilities,
                            "DRAW"
                        ),

                    away =
                        percentage(
                            percentages,
                            probabilities,
                            "AWAY"
                        )
                ),

            modelVersion =
                root.optString(
                    "model_version",
                    "N/D"
                )
        )
    }

    suspend fun getPrediction(
        matchId: Int
    ): PredictionDetail {

        val response =
            get(
                "/ai/predict/$matchId"
            )

        return parsePrediction(
            JSONObject(response)
        )
    }

    suspend fun getUpcomingPredictions(
        limit: Int
    ): List<PredictionDetail> {

        val response =
            get(
                "/ai/predict/upcoming?limit=$limit"
            )

        val root =
            JSONObject(response)

        val details =
            root.optJSONArray(
                "details"
            ) ?: JSONArray()

        val result =
            mutableListOf<PredictionDetail>()

        for (index in 0 until details.length()) {

            val item =
                details.optJSONObject(index)
                    ?: continue

            if (
                !item.optBoolean(
                    "ok",
                    false
                )
            ) {
                continue
            }

            val matchId =
                item.optInt(
                    "match_id",
                    0
                )

            if (matchId <= 0) {
                continue
            }

            val status =
                item.optString(
                    "status",
                    ""
                )

            val prediction =
                item.optString(
                    "prediction",
                    ""
                )

            val skipped =
                status.equals(
                    "skipped",
                    ignoreCase = true
                )

            /*
             * Cuando el backend dice "skipped"
             * porque ya existe una predicción,
             * consultamos el endpoint individual.
             */

            if (
                skipped ||
                prediction.isBlank()
            ) {

                try {

                    val realPrediction =
                        getPrediction(
                            matchId
                        )

                    result.add(
                        realPrediction
                    )

                } catch (
                    _: Exception
                ) {

                    /*
                     * No se agregan datos inventados.
                     */

                }

            } else {

                result.add(
                    parsePrediction(
                        item
                    )
                )
            }
        }

        return result
    }

    suspend fun getUpcomingPredictions(): List<PredictionDetail> {
        return getUpcomingPredictions(
            limit = 10
        )
    }

    suspend fun getFixtures(
        next: Int
    ): List<FixtureInfo> {

        val response =
            get(
                "/fixtures?next=$next"
            )

        val root =
            JSONObject(response)

        val array =
            root.optJSONArray(
                "response"
            ) ?: JSONArray()

        val result =
            mutableListOf<FixtureInfo>()

        for (index in 0 until array.length()) {

            val item =
                array.optJSONObject(index)
                    ?: continue

            val fixture =
                item.optJSONObject(
                    "fixture"
                ) ?: continue

            val teams =
                item.optJSONObject(
                    "teams"
                ) ?: continue

            val home =
                teams.optJSONObject(
                    "home"
                ) ?: continue

            val away =
                teams.optJSONObject(
                    "away"
                ) ?: continue

            val league =
                item.optJSONObject(
                    "league"
                )

            val goals =
                item.optJSONObject(
                    "goals"
                )

            result.add(

                FixtureInfo(

                    id =
                        fixture.optInt(
                            "id",
                            0
                        ),

                    startingAt =
                        fixture.optString(
                            "date",
                            ""
                        ).takeIf {
                            it.isNotBlank()
                        },

                    status =
                        fixture
                            .optJSONObject(
                                "status"
                            )
                            ?.optString(
                                "short",
                                null
                            ),

                    homeTeam =
                        TeamInfo(

                            id =
                                home.optInt(
                                    "id",
                                    0
                                ),

                            name =
                                home.optString(
                                    "name",
                                    "Local"
                                ),

                            code =
                                home.optString(
                                    "code",
                                    null
                                ).takeIf {
                                    !it.isNullOrBlank()
                                },

                            logo =
                                home.optString(
                                    "logo",
                                    null
                                ).takeIf {
                                    !it.isNullOrBlank()
                                }
                        ),

                    awayTeam =
                        TeamInfo(

                            id =
                                away.optInt(
                                    "id",
                                    0
                                ),

                            name =
                                away.optString(
                                    "name",
                                    "Visitante"
                                ),

                            code =
                                away.optString(
                                    "code",
                                    null
                                ).takeIf {
                                    !it.isNullOrBlank()
                                },

                            logo =
                                away.optString(
                                    "logo",
                                    null
                                ).takeIf {
                                    !it.isNullOrBlank()
                                }
                        ),

                    homeGoals =
                        if (
                            goals == null ||
                            goals.isNull("home")
                        ) {
                            null
                        } else {
                            goals.optInt(
                                "home"
                            )
                        },

                    awayGoals =
                        if (
                            goals == null ||
                            goals.isNull("away")
                        ) {
                            null
                        } else {
                            goals.optInt(
                                "away"
                            )
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

    suspend fun getFixtures(): List<FixtureInfo> {
        return getFixtures(
            next = 20
        )
    }

    suspend fun getFixture(
        matchId: Int
    ): FixtureInfo? {

        val response =
            get(
                "/fixtures/$matchId"
            )

        val root =
            JSONObject(response)

        val array =
            root.optJSONArray(
                "response"
            ) ?: return null

        if (array.length() == 0) {
            return null
        }

        val item =
            array.optJSONObject(
                0
            ) ?: return null

        val fixture =
            item.optJSONObject(
                "fixture"
            ) ?: return null

        val teams =
            item.optJSONObject(
                "teams"
            ) ?: return null

        val home =
            teams.optJSONObject(
                "home"
            ) ?: return null

        val away =
            teams.optJSONObject(
                "away"
            ) ?: return null

        val league =
            item.optJSONObject(
                "league"
            )

        val goals =
            item.optJSONObject(
                "goals"
            )

        return FixtureInfo(

            id =
                fixture.optInt(
                    "id",
                    0
                ),

            startingAt =
                fixture.optString(
                    "date",
                    ""
                ).takeIf {
                    it.isNotBlank()
                },

            status =
                fixture
                    .optJSONObject(
                        "status"
                    )
                    ?.optString(
                        "short",
                        null
                    ),

            homeTeam =
                TeamInfo(

                    id =
                        home.optInt(
                            "id",
                            0
                        ),

                    name =
                        home.optString(
                            "name",
                            "Local"
                        ),

                    code =
                        home.optString(
                            "code",
                            null
                        ).takeIf {
                            !it.isNullOrBlank()
                        },

                    logo =
                        home.optString(
                            "logo",
                            null
                        ).takeIf {
                            !it.isNullOrBlank()
                        }
                ),

            awayTeam =
                TeamInfo(

                    id =
                        away.optInt(
                            "id",
                            0
                        ),

                    name =
                        away.optString(
                            "name",
                            "Visitante"
                        ),

                    code =
                        away.optString(
                            "code",
                            null
                        ).takeIf {
                            !it.isNullOrBlank()
                        },

                    logo =
                        away.optString(
                            "logo",
                            null
                        ).takeIf {
                            !it.isNullOrBlank()
                        }
                ),

            homeGoals =
                if (
                    goals == null ||
                    goals.isNull("home")
                ) {
                    null
                } else {
                    goals.optInt(
                        "home"
                    )
                },

            awayGoals =
                if (
                    goals == null ||
                    goals.isNull("away")
                ) {
                    null
                } else {
                    goals.optInt(
                        "away"
                    )
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
            get(
                "/ai/status"
            )

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
                    root.has(
                        "training_matches"
                    )
                ) {
                    root.optInt(
                        "training_matches"
                    )
                } else {
                    null
                },

            validationAccuracy =
                if (
                    root.has(
                        "validation_accuracy"
                    )
                ) {
                    root.optDouble(
                        "validation_accuracy"
                    )
                } else {
                    null
                },

            validationLogLoss =
                if (
                    root.has(
                        "validation_log_loss"
                    )
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
            get(
                "/ai/performance"
            )

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
                if (
                    root.has(
                        "accuracy"
                    )
                ) {
                    root.optDouble(
                        "accuracy"
                    )
                } else {
                    null
                },

            accuracyPercent =
                if (
                    root.has(
                        "accuracy_percent"
                    )
                ) {
                    root.optDouble(
                        "accuracy_percent"
                    )
                } else {
                    null
                },

            logLoss =
                if (
                    root.has(
                        "log_loss"
                    )
                ) {
                    root.optDouble(
                        "log_loss"
                    )
                } else {
                    null
                },

            brierScore =
                if (
                    root.has(
                        "brier_score"
                    )
                ) {
                    root.optDouble(
                        "brier_score"
                    )
                } else {
                    null
                )
        )
    }
}

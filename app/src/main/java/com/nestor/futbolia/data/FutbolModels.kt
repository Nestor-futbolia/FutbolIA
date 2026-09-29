package com.nestor.futbolia.data

data class PredictionProbabilities(
    val home: Double = 0.0,
    val draw: Double = 0.0,
    val away: Double = 0.0
)

data class PredictionPercentages(
    val home: Double = 0.0,
    val draw: Double = 0.0,
    val away: Double = 0.0
)

data class PredictionDetail(
    val matchId: Int,
    val startingAt: String? = null,
    val prediction: String = "N/D",
    val predictionProbability: Double = 0.0,
    val predictionPercentage: Double = 0.0,
    val probabilities: PredictionProbabilities =
        PredictionProbabilities(),
    val percentages: PredictionPercentages =
        PredictionPercentages(),
    val modelVersion: String = "N/D"
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val code: String? = null,
    val logo: String? = null
)

data class FixtureInfo(
    val id: Int,
    val startingAt: String? = null,
    val status: String? = null,
    val homeTeam: TeamInfo,
    val awayTeam: TeamInfo,
    val homeGoals: Int? = null,
    val awayGoals: Int? = null,
    val leagueName: String? = null,
    val leagueCountry: String? = null
)

data class MatchUi(
    val fixture: FixtureInfo? = null,
    val prediction: PredictionDetail? = null
)

data class AiStatus(
    val active: Boolean = false,
    val modelVersion: String? = null,
    val modelName: String? = null,
    val trainedAt: String? = null,
    val trainingMatches: Int? = null,
    val validationAccuracy: Double? = null,
    val validationLogLoss: Double? = null
)

data class AiPerformance(
    val evaluatedMatches: Int = 0,
    val correctMatches: Int = 0,
    val accuracy: Double? = null,
    val accuracyPercent: Double? = null,
    val logLoss: Double? = null,
    val brierScore: Double? = null
)

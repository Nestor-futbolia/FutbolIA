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
    val startingAt: String?,
    val prediction: String,
    val predictionProbability: Double,
    val predictionPercentage: Double,
    val probabilities: PredictionProbabilities,
    val percentages: PredictionPercentages,
    val modelVersion: String
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val code: String? = null,
    val logo: String? = null
)

data class FixtureInfo(
    val id: Int,
    val startingAt: String?,
    val status: String?,
    val homeTeam: TeamInfo,
    val awayTeam: TeamInfo,
    val homeGoals: Int? = null,
    val awayGoals: Int? = null,
    val leagueName: String? = null,
    val leagueCountry: String? = null
)

data class MatchUi(
    val fixture: FixtureInfo?,
    val prediction: PredictionDetail?,
    val loading: Boolean = false,
    val error: String? = null
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

data class AppUiState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val matches: List<MatchUi> = emptyList(),
    val selectedMatch: MatchUi? = null,
    val aiStatus: AiStatus? = null,
    val aiPerformance: AiPerformance? = null,
    val error: String? = null
)

package com.nestor.futbolia.data

class FutbolRepository(
    private val api: FutbolApi = FutbolApi()
) {

    suspend fun loadHome(
        limit: Int = 10
    ): List<MatchUi> {

        val predictions =
            api.getUpcomingPredictions(limit)

        val fixtures =
            api.getFixtures(
                next = maxOf(
                    20,
                    limit * 2
                )
            )

        val fixtureMap =
            fixtures.associateBy {
                it.id
            }

        return predictions.map { prediction ->

            MatchUi(
                fixture =
                    fixtureMap[
                        prediction.matchId
                    ],
                prediction =
                    prediction
            )
        }
    }

    suspend fun loadMatch(
        matchId: Int
    ): MatchUi {

        val prediction =
            api.getPrediction(matchId)

        val fixture =
            api.getFixture(matchId)

        return MatchUi(
            fixture = fixture,
            prediction = prediction
        )
    }

    suspend fun loadAiStatus(): AiStatus {
        return api.getAiStatus()
    }

    suspend fun loadPerformance(): AiPerformance {
        return api.getPerformance()
    }
}

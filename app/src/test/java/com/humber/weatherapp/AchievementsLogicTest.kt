package com.humber.weatherapp

import org.junit.Assert.*
import org.junit.Test

class AchievementsLogicTest {

    @Test
    fun testPointsUnlockAchievementsCorrectly() {
        val testPoints = 550
        val expected = listOf("achv1", "achv2", "achv3", "achv4")

        val unlocked = DailyRewardManager.achievementsList
            .filter { testPoints >= it.pointsRequired }
            .map { it.id }

        assertEquals(expected, unlocked)
    }

    @Test
    fun testNoAchievementsUnlockedWithZeroPoints() {
        val testPoints = 0

        val unlocked = DailyRewardManager.achievementsList
            .filter { testPoints >= it.pointsRequired }

        assertTrue(unlocked.isEmpty())
    }
}

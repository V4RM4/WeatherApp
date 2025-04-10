package com.humber.weatherapp

import org.junit.Assert.*
import org.junit.Test

class DailyRewardManagerTest {

    @Test
    fun testAchievementUnlocking() {
        val points = 120
        val achievementsUnlocked = DailyRewardManager.achievementsList.filter {
            points >= it.pointsRequired
        }

        assertEquals(2, achievementsUnlocked.size)
        assertTrue(achievementsUnlocked.any { it.name == "Breeze Beginner" })
        assertTrue(achievementsUnlocked.any { it.name == "Sunny Side Up" })
    }
}
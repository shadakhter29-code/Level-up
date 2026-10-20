package com.example

import com.example.data.model.GamificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun testLevel1_InitialState() {
        val levelInfo = GamificationEngine.calculateLevel(0)
        assertEquals(1, levelInfo.level)
        assertEquals("Novice Scholar", levelInfo.title)
        assertEquals(0, levelInfo.totalXp)
        assertEquals(100, levelInfo.xpRequiredForNextLevel)
        assertEquals(0, levelInfo.xpInCurrentLevel)
        assertEquals(0f, levelInfo.progressPercentage, 0.001f)
    }

    @Test
    fun testLevel1_PartialXp() {
        val levelInfo = GamificationEngine.calculateLevel(50)
        assertEquals(1, levelInfo.level)
        assertEquals(50, levelInfo.totalXp)
        assertEquals(50, levelInfo.xpInCurrentLevel)
        assertEquals(100, levelInfo.xpRequiredForNextLevel)
        assertEquals(0.5f, levelInfo.progressPercentage, 0.001f)
    }

    @Test
    fun testLevel2_Threshold() {
        // At 100 XP, user reaches Level 2
        val levelInfo = GamificationEngine.calculateLevel(100)
        assertEquals(2, levelInfo.level)
        assertEquals("Apprentice Learner", levelInfo.title)
        assertEquals(100, levelInfo.totalXp)
        assertEquals(0, levelInfo.xpInCurrentLevel)
        assertEquals(150, levelInfo.xpRequiredForNextLevel)
        assertEquals(0f, levelInfo.progressPercentage, 0.001f)
    }

    @Test
    fun testLevel3_Threshold() {
        // Level 1: 100 XP. Level 2: 150 XP. Total needed for Level 3: 250 XP
        val levelInfo = GamificationEngine.calculateLevel(250)
        assertEquals(3, levelInfo.level)
        assertEquals("Focus Fighter", levelInfo.title)
        assertEquals(250, levelInfo.totalXp)
        assertEquals(0, levelInfo.xpInCurrentLevel)
        assertEquals(200, levelInfo.xpRequiredForNextLevel)
    }

    @Test
    fun testXpProgressCoercion() {
        val levelInfo = GamificationEngine.calculateLevel(175)
        assertEquals(2, levelInfo.level)
        assertEquals(75, levelInfo.xpInCurrentLevel)
        assertEquals(150, levelInfo.xpRequiredForNextLevel)
        assertEquals(0.5f, levelInfo.progressPercentage, 0.001f)
    }

    @Test
    fun testNegativeXpSafety() {
        val levelInfo = GamificationEngine.calculateLevel(-50)
        assertEquals(1, levelInfo.level)
        assertEquals(0, levelInfo.totalXp)
        assertEquals(0f, levelInfo.progressPercentage, 0.001f)
    }

    @Test
    fun testPastDaysGeneration() {
        val days = GamificationEngine.getPastDays(7)
        assertEquals(7, days.size)
        // Today should be the last item
        assertEquals(GamificationEngine.getTodayDateString(), days.last())
    }
}

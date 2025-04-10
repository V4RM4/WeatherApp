package com.humber.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DailyRewardManager {

    // Achievement data class.
    data class Achievement(val id: String, val name: String, val emoji: String, val pointsRequired: Int)

    // Updated list of achievement thresholds.
    val achievementsList = listOf(
        Achievement("achv1", "Breeze Beginner", "🍃", 50),
        Achievement("achv2", "Sunny Side Up", "☀️", 100),
        Achievement("achv3", "Storm Chaser", "⛈️", 200),
        Achievement("achv4", "Lightning Legend", "⚡", 500),
        Achievement("achv5", "Rainbow God", "🌈", 1000)
    )

    fun checkAndAwardDailyReward(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("DailyRewardPrefs", Context.MODE_PRIVATE)
        val lastRewardDate = prefs.getString("lastRewardDate", "")
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        if (lastRewardDate == today) {
            // Reward has already been given today – do nothing.
            return
        }

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get().addOnSuccessListener { document ->
            val currentPoints = document.getLong("points")?.toInt() ?: 0
            val newPoints = currentPoints + 10

            // Get any existing unlocked achievements.
            val unlockedAchievements = document.get("unlockedAchievements") as? List<String> ?: emptyList<String>()
            val updatedUnlocked = unlockedAchievements.toMutableList()

            // Unlock achievements if the new total meets or exceeds thresholds.
            for (achievement in achievementsList) {
                if (newPoints >= achievement.pointsRequired && !updatedUnlocked.contains(achievement.id)) {
                    updatedUnlocked.add(achievement.id)
                }
            }

            // Update Firestore with the new points and unlocked achievements.
            userDocRef.update("points", newPoints, "unlockedAchievements", updatedUnlocked)
                .addOnSuccessListener {
                    Toast.makeText(context, "Daily reward: 10 points awarded! Total points: $newPoints", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update reward: ${e.message}", Toast.LENGTH_LONG).show()
                }

            // Save today's date to prevent duplicate rewards.
            prefs.edit().putString("lastRewardDate", today).apply()
        }
    }
}

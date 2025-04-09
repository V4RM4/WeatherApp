package com.humber.weatherapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AchievementsAdapter(
    private val achievements: List<DailyRewardManager.Achievement>,
    private val unlockedAchievementIds: List<String>
) : RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.achievementCard)
        val achievementTitle: TextView = itemView.findViewById(R.id.achievementTitle)
        val achievementDesc: TextView = itemView.findViewById(R.id.achievementDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        val unlocked = unlockedAchievementIds.contains(achievement.id)

        holder.achievementTitle.text = "${achievement.emoji} ${achievement.name}"
        holder.achievementDesc.text = if (unlocked) {
            "Unlocked (requires ${achievement.pointsRequired} pts)"
        } else {
            "Locked (requires ${achievement.pointsRequired} pts)"
        }

        // Change the card appearance based on unlocked status.
        if (unlocked) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#e0f7fa"))  // Light cyan background.
            holder.achievementTitle.setTextColor(Color.parseColor("#00796b"))       // Dark teal text.
            holder.achievementDesc.setTextColor(Color.parseColor("#004d40"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#eeeeee"))      // Light gray.
            holder.achievementTitle.setTextColor(Color.GRAY)
            holder.achievementDesc.setTextColor(Color.DKGRAY)
        }
    }

    override fun getItemCount(): Int = achievements.size
}

package com.sayed.campusdock.Data.Socials

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int,
    val totalVotes: Int // Add this to easily calculate percentage
) {
    val percentage: Int
        get() = if (totalVotes > 0) ((votes.toFloat() / totalVotes) * 100).toInt() else 0
}

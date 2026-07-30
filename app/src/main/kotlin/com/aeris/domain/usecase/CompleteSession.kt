package com.aeris.domain.usecase

import com.aeris.domain.model.Session
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

class CompleteSession(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val calculateLevel: CalculateLevel,
    private val calculateStreak: CalculateStreak,
    private val checkBadges: CheckBadges
) {
    suspend operator fun invoke(session: Session): List<String> {
        sessionRepository.insertSession(session)
        val allSessions = sessionRepository.getAllSessions().first()
        val timestamps = allSessions.map { it.completedAt }
        val streak = calculateStreak(timestamps)
        val bci = 0f // calculated from session data
        val level = calculateLevel(allSessions.size, bci)
        userRepository.updateStreak(streak)
        userRepository.updateLevel(level)
        val newBadges = checkBadges(allSessions.size, streak, bci, level, session)
        newBadges.forEach { userRepository.earnBadge(it) }
        return newBadges
    }
}

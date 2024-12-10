package com.bangkit.capstone.domain.repository

import com.bangkit.capstone.domain.model.RiskLevel
import java.util.concurrent.ConcurrentHashMap

object RiskLevelRepository {
    private val riskLevels = ConcurrentHashMap<String, RiskLevel>()

    fun setRiskLevel(identifier: String, riskLevel: RiskLevel) {
        riskLevels[identifier] = riskLevel
    }

    fun getRiskLevel(identifier: String): RiskLevel? {
        return riskLevels[identifier]
    }
}
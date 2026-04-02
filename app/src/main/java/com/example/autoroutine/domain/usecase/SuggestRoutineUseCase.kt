package com.example.autoroutine.domain.usecase

import com.example.autoroutine.data.local.LogEntity
import com.example.autoroutine.data.local.RoutineEntity

/**
 * 사용자 로그 패턴을 분석하여 새로운 루틴을 제안하는 UseCase
 */
class SuggestRoutineUseCase {

    fun execute(logs: List<LogEntity>): List<RoutineEntity> {
        val suggestedRoutines = mutableListOf<RoutineEntity>()
        
        // 간단한 Rule-based 추론 예시
        // 밤(Night)에 이어폰을 꽂고 YouTube를 실행하는 빈도가 높다면?
        val nightYoutubeLogs = logs.filter { 
            it.timeOfDay == "Night" && it.foregroundApp == "com.google.android.youtube" 
        }

        if (nightYoutubeLogs.size >= 5) { // 5번 이상 패턴 반복 시 제안
            suggestedRoutines.add(
                RoutineEntity(
                    ruleName = "심야 유튜브 모드",
                    condition = "timeOfDay=Night;foregroundApp=youtube",
                    action = "vol=30%;brightness=10%;dnd=on",
                    isUserAccepted = false
                )
            )
        }

        // 출근/등교(배터리 100% -> 감소, 오전) 시 이어폰 연결하면 음악 앱 추천 등
        val morningMusicLogs = logs.filter {
            it.timeOfDay == "Morning" && it.isHeadsetConnected
        }

        if (morningMusicLogs.size >= 3) {
            suggestedRoutines.add(
                RoutineEntity(
                    ruleName = "아침 출근/등교 모드",
                    condition = "timeOfDay=Morning;headset=connected",
                    action = "launchApp=Spotify;vol=50%",
                    isUserAccepted = false
                )
            )
        }

        return suggestedRoutines
    }
}

package com.example.autoroutine.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

/**
 * 백그라운드에서 주기적으로 기기 상태(앱, 배터리, 이어폰 연결 등)를 수집하는 서비스
 */
class ContextMonitorService : Service() {

    private lateinit var deviceEventReceiver: DeviceEventReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d("ContextMonitorService", "Service Created - Start capturing context logs")
        
        // 동적 Broadcast(이어폰 장착 등 런타임에만 잡을 수 있는 이벤트) 등록
        deviceEventReceiver = DeviceEventReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(deviceEventReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 백그라운드 무한 루프 또는 JobScheduler/WorkManager로 주기적 상태 스캔
        captureCurrentContext()
        return START_STICKY
    }

    private fun captureCurrentContext() {
        // 1. Get Battery Level via BatteryManager
        // 2. Get Headset state via AudioManager
        // 3. Get Foreground App via UsageStatsManager
        // 4. Get TimeOfDay
        // 5. Save to LogEntity (Room DB)
        
        Log.d("ContextMonitorService", "Context Captured!")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(deviceEventReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not a bound service
    }
}

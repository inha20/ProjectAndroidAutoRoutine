package com.example.autoroutine.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

// 시스템 브로드캐스트 이벤트(이어폰 등)를 잡아 모니터 서비스에 전달하는 리시버
class DeviceEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("DeviceEventReceiver", "Action received: $action")
        
        when (action) {
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", -1)
                val isConnected = state == 1
                Log.d("DeviceEventReceiver", "Headset connected: $isConnected")
                
                // 여기서 실 운영환경이라면 ContextMonitorService를 깨우거나 Data Repository에 Insert합니다.
                // context?.startService(...)
            }
            Intent.ACTION_BATTERY_LOW -> {
                Log.d("DeviceEventReceiver", "Battery is Low")
            }
        }
    }
}

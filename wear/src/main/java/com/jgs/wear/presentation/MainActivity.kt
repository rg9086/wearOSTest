/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.jgs.wear.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.jgs.wear.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 주기적으로 심박수 전송
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isRunning) {
                    sendHeartRateData()
                    handler.postDelayed(this, 1000) // 1초마다 반복
                }
            }
        }, 1000)
    }

    private fun sendHeartRateData() {
        val heartRate = Random.nextInt(60, 101) // 60 ~ 100 사이의 랜덤 심박수 생성
        val dataMapRequest = PutDataMapRequest.create("/heart_rate").apply {
            dataMap.putInt("heart_rate", heartRate)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        Wearable.getDataClient(this)
            .putDataItem(dataMapRequest.asPutDataRequest())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Heart rate data sent successfully: $heartRate")
                } else {
                    println("Failed to send heart rate data: ${task.exception?.message}")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}


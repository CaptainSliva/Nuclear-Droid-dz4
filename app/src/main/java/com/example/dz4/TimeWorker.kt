package com.example.dz4

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dz4.FunctionsApp.createNotify
import java.time.LocalDateTime

class TimeWorker(private  val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("Work", "Work create")
        val nextTime = LocalDateTime.now().plusMinutes(inputData.getLong(nextTime, 0))
        createNotify(context, "Notify", nextTime)


        return Result.success()
    }


}
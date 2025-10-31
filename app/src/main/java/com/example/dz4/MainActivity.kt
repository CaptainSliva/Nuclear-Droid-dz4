package com.example.dz4

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.dz4.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


val nextTime = "next_time"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currentStatusText = "Текущий статус:"
        val workTag = "periodic"

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {}
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.POST_NOTIFICATIONS) -> {}
            else -> {
                requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        binding.bStart.setOnClickListener { view ->
            try {
                val etTime = binding.etInterval.text.toString().toLong()
                if (etTime >= 15) {
                    val data = workDataOf(
                        nextTime to etTime
                    )
                    val constraints = androidx.work.Constraints.Builder()
                        .setRequiresCharging(binding.cbPower.isChecked)
                        .setRequiresBatteryNotLow(binding.cbFullPower.isChecked)
                        .build()
                    val workRequest = PeriodicWorkRequest.Builder(
                        TimeWorker::class,
                        etTime,
                        TimeUnit.MINUTES)
                        .addTag(workTag)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    WorkManager.getInstance(this).enqueue(workRequest)
                    WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
                        binding.tvStatus.text =  "$currentStatusText ${workInfo?.state}"
                    }
                    Snackbar.make(view, "Работа запущена", Snackbar.LENGTH_SHORT).show()
                }
                else {
                    Snackbar.make(view, "Число меньше 15", Snackbar.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Snackbar.make(view, "Введите цифры", Snackbar.LENGTH_SHORT).show()
            }

        }
        binding.bStop.setOnClickListener { view ->
            WorkManager.getInstance(this).cancelAllWorkByTag(workTag)
            Snackbar.make(view, "Работа завершена", Snackbar.LENGTH_SHORT).show()
        }
    }
}
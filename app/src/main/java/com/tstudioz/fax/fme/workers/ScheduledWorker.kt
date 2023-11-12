package com.tstudioz.fax.fme.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.activities.MainActivity
import com.tstudioz.fax.fme.data.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

@InternalCoroutinesApi
class ScheduledWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    private val repository: Repository by inject(Repository::class.java)

    override fun doWork(): Result {
        val job = SupervisorJob()
        val dispatchers = Dispatchers.IO
        val context = job + dispatchers
        val scope = CoroutineScope(context)

        scope.launch {
            repository.fetchTimetable("temer00", "2020-04-06", "2020-04-11")
                    .onEach { repository.compareTimetables(it) }
                    .onStart { Timber.i("fetching timetable started") }
                    .catch { e ->
                        Timber.e(e, "workmanager job ex")
                        Result.failure()
                    }
                    .collect { result ->
                        Timber.d("workmanager list size ${result.toList()}")
                        sendNotification(result.toList().size)
                    }
        }

        return Result.success()
    }

    private fun sendNotification(promjene: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("fc_comp", "FESB Companion app channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
                .setAction("raspored")

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, "fc_comp")
                .setContentTitle("$promjene promjena u rasporedu")
                .setContentText("Dotaknite za prikaz rasporeda")
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setPriority(2)
                .setSmallIcon(R.drawable.fc_loco_small)

        notificationManager.notify(1, notification.build())
    }
}
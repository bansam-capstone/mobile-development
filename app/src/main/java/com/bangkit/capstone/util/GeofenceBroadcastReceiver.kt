package com.bangkit.capstone.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bangkit.capstone.R
import com.bangkit.capstone.domain.model.RiskLevel
import com.bangkit.capstone.domain.repository.RiskLevelRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.concurrent.atomic.AtomicInteger

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val notificationId = AtomicInteger(0)
    private val locationAddressMap = mapOf(
        "slamet-riyadi" to "Jl. Slamet Riyadi",
        "antasari" to "Jl. P Antasari",
        "simpang-agus-salim" to "Jl. KH. Agus Salim",
        "mugirejo" to "Jl. Mugirejo",
        "simpang-lembuswana" to "Simpang Lembuswana",
        "kapten-sudjono" to "Jl. Kapten Soedjono Aj",
        "brigjend-katamso" to "Jl. Brigjend Katamsog",
        "gatot-subroto" to "Jl. Gatot Subroto",
        "cendana" to "Jl. Cendana",
        "di-panjaitan" to "Jl. D.I. Panjaitan",
        "damanhuri" to "Jl. Damanhuri",
        "pertigaan-pramuka-perjuangan" to "Pertigaan Pramuka Perjuangan",
        "padat-karya-sempaja-simpang-wanyi" to "Jl. Padat Karya",
        "simpang-sempaja" to "Simpang Sempaja",
        "ir-h-juanda" to "Simpang Juanda Fly Over",
        "tengkawang" to "Jl. Tengkawang",
        "sukorejo" to "Jl. Sukorejo"
    )
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            sendNotification(context, "Error", errorMessage, R.drawable.ic_notifications_active, NotificationCompat.PRIORITY_LOW)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

            for (geofence in triggeringGeofences) {
                val identifier = geofence.requestId
                val nameStreet = locationAddressMap[identifier] ?: "Lokasi tidak dikenal"
                val riskLevel = RiskLevelRepository.getRiskLevel(identifier)

                val message: String
                val title: String
                val icon: Int
                val priority: Int

                when (riskLevel) {
                    RiskLevel.WASPADA -> {
                        title = "Peringatan Waspada Banjir"
                        message = when (geofenceTransition) {
                            Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area waspada banjir di $nameStreet!"
                            Geofence.GEOFENCE_TRANSITION_EXIT -> "Anda telah meninggalkan area waspada banjir di $nameStreet. Tetap waspada!"
                            else -> "Transisi banjir tidak dikenali untuk lokasi $nameStreet."
                        }
                        icon = R.drawable.ic_notifications_active
                        priority = NotificationCompat.PRIORITY_HIGH
                    }
                    RiskLevel.BAHAYA -> {
                        title = "Peringatan Bahaya Banjir"
                        message = when (geofenceTransition) {
                            Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area dengan bahaya banjir di $nameStreet!"
                            Geofence.GEOFENCE_TRANSITION_EXIT -> "Anda telah meninggalkan area bahaya banjir di $nameStreet. Tetap waspada!"
                            else -> "Transisi banjir tidak dikenali untuk lokasi $nameStreet."
                        }
                        icon = R.drawable.ic_notifications_active
                        priority = NotificationCompat.PRIORITY_MAX
                    }
                    RiskLevel.AMAN, null -> {
                        title = "Peringatan Aman Banjir"
                        message = when (geofenceTransition) {
                            Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area aman dari banjir di $nameStreet!"
                            Geofence.GEOFENCE_TRANSITION_EXIT -> "Anda telah meninggalkan area aman dari banjir di $nameStreet."
                            else -> "Transisi banjir tidak dikenali untuk lokasi $nameStreet."
                        }
                        icon = R.drawable.ic_notifications_active
                        priority = NotificationCompat.PRIORITY_MIN
                    }
                }

                sendNotification(context, title, message, icon, priority)
            }
        } else {
            val errorMessage = "Jenis transisi geofence tidak dikenal: $geofenceTransition"
            sendNotification(context, "Transisi Tidak Dikenal", errorMessage, R.drawable.ic_notifications_active, NotificationCompat.PRIORITY_LOW)
        }
    }

    private fun sendNotification(
        context: Context,
        title: String,
        message: String,
        icon: Int,
        priority: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk notifikasi geofence"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        notificationManager.notify(notificationId.incrementAndGet(), builder.build())
    }

    companion object {
        const val CHANNEL_ID = "geofence_channel_1"
        private const val CHANNEL_NAME = "Geofence Notifications"
    }
}

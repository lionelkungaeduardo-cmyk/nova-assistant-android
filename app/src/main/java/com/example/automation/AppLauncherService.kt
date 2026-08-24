package com.example.automation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import com.example.receiver.NovaAlarmReceiver

data class ExecutionResult(
    val success: Boolean,
    val message: String,
    val actionType: String? = null,
    val pendingIntent: Intent? = null
)

class AppLauncherService(private val context: Context) {

    fun openApp(packageName: String, appName: String): ExecutionResult {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
                ExecutionResult(true, "Abrindo $appName...", actionType = "OPEN_APP")
            } catch (e: Exception) {
                ExecutionResult(false, "Erro ao iniciar $appName: ${e.message}")
            }
        } else {
            // Intent fallback for web / Play Store
            ExecutionResult(false, "O aplicativo $appName não foi detectado instalado no dispositivo.")
        }
    }

    fun openWhatsApp(message: String? = null, phone: String? = null): ExecutionResult {
        return try {
            val intent = if (!phone.isNullOrBlank()) {
                val url = "https://api.whatsapp.com/send?phone=$phone" + if (!message.isNullOrBlank()) "&text=${Uri.encode(message)}" else ""
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            } else if (!message.isNullOrBlank()) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    `package` = "com.whatsapp"
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                sendIntent
            } else {
                context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com"))
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            ExecutionResult(true, if (!message.isNullOrBlank()) "Preparando mensagem no WhatsApp..." else "Abrindo WhatsApp...")
        } catch (e: Exception) {
            ExecutionResult(false, "Não foi possível abrir o WhatsApp: ${e.message}")
        }
    }

    fun openYouTube(query: String? = null): ExecutionResult {
        return try {
            val intent = if (!query.isNullOrBlank()) {
                Intent(Intent.ACTION_SEARCH).apply {
                    `package` = "com.google.android.youtube"
                    putExtra("query", query)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            ExecutionResult(true, if (!query.isNullOrBlank()) "Buscando \"$query\" no YouTube..." else "Abrindo YouTube...")
        } catch (e: Exception) {
            // Web fallback
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query ?: "")}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
            ExecutionResult(true, "Abrindo YouTube no navegador...")
        }
    }

    fun openBrowser(urlOrQuery: String): ExecutionResult {
        return try {
            val url = if (urlOrQuery.startsWith("http://") || urlOrQuery.startsWith("https://")) {
                urlOrQuery
            } else {
                "https://www.google.com/search?q=${Uri.encode(urlOrQuery)}"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ExecutionResult(true, "Pesquisando no navegador...")
        } catch (e: Exception) {
            ExecutionResult(false, "Erro ao abrir navegador: ${e.message}")
        }
    }

    fun openSettings(subscreen: String? = null): ExecutionResult {
        return try {
            val action = when (subscreen?.lowercase()) {
                "wifi", "wi-fi" -> Settings.ACTION_WIFI_SETTINGS
                "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
                "display", "tela" -> Settings.ACTION_DISPLAY_SETTINGS
                "apps", "aplicativos" -> Settings.ACTION_APPLICATION_SETTINGS
                "som", "audio" -> Settings.ACTION_SOUND_SETTINGS
                else -> Settings.ACTION_SETTINGS
            }
            val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
            ExecutionResult(true, "Abrindo configurações do sistema...")
        } catch (e: Exception) {
            ExecutionResult(false, "Erro ao abrir configurações: ${e.message}")
        }
    }

    fun setSystemAlarm(hour: Int, minutes: Int, message: String): ExecutionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ExecutionResult(true, "Alarme programado para as %02d:%02d: $message".format(hour, minutes))
        } catch (e: Exception) {
            ExecutionResult(false, "Não foi possível abrir o gerenciador de alarmes do sistema.")
        }
    }

    fun scheduleLocalReminder(id: Long, title: String, triggerAtMillis: Long): ExecutionResult {
        return try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NovaAlarmReceiver::class.java).apply {
                action = NovaAlarmReceiver.ACTION_ALARM_TRIGGER
                putExtra(NovaAlarmReceiver.EXTRA_TITLE, title)
                putExtra(NovaAlarmReceiver.EXTRA_ID, id)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }

            ExecutionResult(true, "Lembrete registrado com sucesso.")
        } catch (e: Exception) {
            ExecutionResult(false, "Lembrete salvo localmente (alarme restrito pelo sistema).")
        }
    }
}

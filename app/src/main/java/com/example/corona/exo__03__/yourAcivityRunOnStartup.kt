package com.example.corona.exo__03__

import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class yourActivityRunOnStartup : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(context, Adhan::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(i)
        }
    }

}
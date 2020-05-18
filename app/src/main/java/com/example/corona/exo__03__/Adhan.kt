package com.example.corona.exo__03__

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.os.HandlerCompat.postDelayed
import java.text.SimpleDateFormat
import java.util.*
import android.media.MediaPlayer
import android.media.VolumeProvider
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.exo__03__.Class.Day
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList


class Adhan : Service() {
    // file pour ajouter les requetes http
    lateinit var mRequestQueue: RequestQueue
    val url = "http://api.aladhan.com/v1/timingsByCity?city=Algiers&country=Algeria&method=3"
    var mawa9it : ArrayList<String> = arrayListOf(
        "","","","",""
    )
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        getData()
    }

    fun getData(){

        // Créer une file qui va gérer les requetes http
        mRequestQueue = Volley.newRequestQueue(baseContext)


        // Création d'une requte http de type Get
        val mJsonObjectRequest =StringRequest(Request.Method.GET, url,
            Response.Listener { response ->

                try
                {
                    var obj = JSONObject(response)
                    var data: JSONObject = obj.getJSONObject("data")
                    var gson = Gson()
                    var jsonObject = data.getJSONObject("timings")
                    var day = gson.fromJson(jsonObject.toString(), Day::class.java)
                    mawa9it = arrayListOf(
                        day.fajr!!,day.dhuhr!!,
                        day.asr!!,day.maghrib!!,
                        day.isha!!
                    )
                    Log.d("response",mawa9it.toString())

                }
                catch (e:Exception){

                }
                //it : JSON Object
            }, Response.ErrorListener {
                it.printStackTrace()
            })

        // Ajouter la requete http construit dans la file des requetes http
        // pour le lancer
        mRequestQueue.add(mJsonObjectRequest)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(baseContext,"service adhan has started .",Toast.LENGTH_SHORT).show()
        val someHandler = Handler(mainLooper)
        var hour : String
        var minute : String
        var a : String
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                hour = SimpleDateFormat("HH:mm", Locale.US).format(Date())
                if(hour == "01:00"){
                    getData()
                    Log.d("update","day update")
                }
                for(i in 0 until mawa9it.size){
                    if(hour == mawa9it.get(i))
                    {

                        val salat = getResources().getStringArray(R.array.salawat)[i]
                        val notification = NotificationCompat.Builder(baseContext,"FCM_CHANNEL_ID")
                            .setSmallIcon(R.drawable.ic_architecture_and_city)
                            .setContentTitle("صلاة "+salat)
                            .setContentText("حان الان ادان صلاة "+salat+ " بتوقيت الجزائر العاصمة و ضواحيها ")
                            .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adthan))
                            .setAutoCancel(true)
                            .build()

                        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        manager.notify(1002,notification)
                        break
                    }
                }

                someHandler.postDelayed(this, 60000)
            }
        }, 10)
        return START_REDELIVER_INTENT

    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(baseContext,"service adhan has stoped .",Toast.LENGTH_SHORT).show()
    }
}

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
    val url = "https://muslimsalat.com/alger.json?key=1311bbd1c188a26a9b79dd6a77ae5882"
    var mawa9it : ArrayList<String> = ArrayList()
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
        val mJsonObjectRequest =StringRequest(Request.Method.GET, "https://muslimsalat.com/alger.json?key=1311bbd1c188a26a9b79dd6a77ae5882",
            Response.Listener { response ->

                try
                {
                    var obj = JSONObject(response)
                    var jsonArray: JSONArray = obj.getJSONArray("items")
                    var gson = Gson()
                    var jsonObject = jsonArray.getJSONObject(0)
                    var day = gson.fromJson(jsonObject.toString(), Day::class.java)
                    mawa9it = arrayListOf(
                        day.fajr!!.toUpperCase(),day.dhuhr!!.toUpperCase(),
                        day.asr!!.toUpperCase(),day.maghrib!!.toUpperCase(),
                        day.isha!!.toUpperCase()
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

        // si la base de données est hors service
        if (mawa9it.isEmpty()){
            mawa9it = arrayListOf(
                "4:01 AM","12:45 AM",
                "4.30 PM","7:53 PM",
                "9:15 PM"
            )
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(baseContext,"service adhan has started .",Toast.LENGTH_SHORT).show()
        val someHandler = Handler(mainLooper)
        var hour : String
        var minute : String
        var a : String
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                hour = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                if(hour[0].toString()=="0"){
                    hour = hour.substring(1)
                }
                if(hour == "1:00 AM"){
                    getData()
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
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(baseContext,"service adhan has stoped .",Toast.LENGTH_SHORT).show()
    }
}

package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.reminder.SQLDB
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val ALARM_REQUEST_CODE = 123

    val myDB = SQLDB(this)

    val IDs = ArrayList<String>()
    val Titles = ArrayList<String>()
    val descriptions = ArrayList<String>()
    val increments = ArrayList<String>()
    val days = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "My channel name"
            val descriptionText = "My channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("123", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        setupAlarm(); // Set up daily alarm

        val title_input = findViewById<TextView>(R.id.TitleText)
        val desc_input = findViewById<TextView>(R.id.DescText)
        val button_input = findViewById<Button>(R.id.button)

        button_input.setOnClickListener(){
            val myDB = SQLDB(this)
            myDB.addReminder(title_input.text.toString(),
            desc_input.text.toString()
            )
            ResetView()
        }
        Log.d("TAG", "app works")
        storeDataInArrays()
        DisplayInLinearLayout()
    }

    fun storeDataInArrays() {
        IDs.clear()
        Titles.clear()
        descriptions.clear()
        increments.clear()
        days.clear()
        val cursor = myDB.readAllData()
        if(cursor?.count == 0) {
            Toast.makeText(this, "Nothing stored", Toast.LENGTH_SHORT).show()
        }else{
            while (cursor?.moveToNext() == true){
                IDs.add(cursor.getString(0))
                Titles.add(cursor.getString(1))
                descriptions.add(cursor.getString(2))
                increments.add(cursor.getString(3))
                days.add(cursor.getString(4))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun DisplayInLinearLayout() {

        for (i in 0 until Titles.size) {

            val vertical_layout = findViewById<LinearLayout>(R.id.VerticalLayout)

            // title text
            val dynamicTextview = TextView(this)
            dynamicTextview.text = Titles.get(i)
            dynamicTextview.setTypeface(null, Typeface.BOLD)
            dynamicTextview.setTextSize(24f)
            dynamicTextview.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val descTextView = TextView(this)
            descTextView.text = descriptions.get(i)
            descTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val removeButton = Button(this)
            removeButton.id = IDs.get(i).toInt()
            removeButton.width = 50

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            layoutParams.gravity = Gravity.RIGHT
            removeButton.layoutParams = layoutParams

            val incrementsview = TextView(this)
            incrementsview.text = increments.get(i)

            val daysView = TextView(this)
            daysView.text = days.get(i) + " Days until reminder"

            removeButton.setOnClickListener() {
                myDB.deleteRow(removeButton.id.toString())
                ResetView()
            }

            val shapeDrawable = GradientDrawable()
            shapeDrawable.shape = GradientDrawable.RECTANGLE
            shapeDrawable.cornerRadius = 16f // adjust this value as needed
            shapeDrawable.setColor(Color.WHITE)
            shapeDrawable.setStroke(2, Color.BLACK)

            val dynamicLayout = LinearLayout(this)
            dynamicLayout.orientation = LinearLayout.VERTICAL
            dynamicLayout.addView(dynamicTextview)
            dynamicLayout.addView(descTextView)
            dynamicLayout.addView(removeButton)
            dynamicLayout.addView(incrementsview)
            dynamicLayout.addView(daysView)


            dynamicLayout.setPadding(64, 16, 0, 16)
            dynamicLayout.id = View.generateViewId()
            dynamicLayout.background = shapeDrawable

            vertical_layout.addView(dynamicLayout)

        }

    }

    fun ResetView(){
        val vertical_layout = findViewById<LinearLayout>(R.id.VerticalLayout)
        vertical_layout.removeAllViews()
        Log.d("TAG", vertical_layout.childCount.toString())
        storeDataInArrays()
        DisplayInLinearLayout()
    }

    private fun setupAlarm() {
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, 0)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        // Set the alarm to fire every day at 8am
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Log.d("ALARM", "alarm setup")
        Log.d("ALARM", calendar.timeInMillis.toString())
    }

}

class MyBroadcastReceiver : BroadcastReceiver() {

    val CHANNEL_ID = "123"
    var NOTIFICATION_ID = 1


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ALARM", "alarm started")
        val IDs = ArrayList<String>()
        val Titles = ArrayList<String>()
        val descriptions = ArrayList<String>()
        val increments = ArrayList<String>()
        val days = ArrayList<String>()

        val myDB = SQLDB(context!!)

        fun storeDataInArrays2() {
            IDs.clear()
            Titles.clear()
            descriptions.clear()
            increments.clear()
            days.clear()
            val cursor = myDB.readAllData()
            if (cursor?.count == 0) {
                Log.d("tag", "nothin")
            } else {
                while (cursor?.moveToNext() == true) {
                    IDs.add(cursor.getString(0))
                    Titles.add(cursor.getString(1))
                    descriptions.add(cursor.getString(2))
                    increments.add(cursor.getString(3))
                    days.add(cursor.getString(4))
                }
            }
        }
        storeDataInArrays2()

        for (i in IDs.indices) {
            if (days[i].toInt() - 1 == 0) {
                NOTIFICATION_ID++
                val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(Titles[i])
                    .setContentText(descriptions[i])
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Show the notification
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(
                    NOTIFICATION_ID,
                    builder.build()
                ) // PERMISSION NOT CHECKED
            }
        }

         // for each notification at the last day in its increment, up the increment and set the new number of days. also decrement every other day


        for (i in IDs.indices) {
            days[i] = (days[i].toInt() - 1).toString()
            if (days[i] == "0") {
                increments[i] = (increments[i].toInt() + 1).toString()
                when (increments[i].toInt()) {
                    1 -> days[i] = "1"
                    2 -> days[i] = "3"
                    3 -> days[i] = "7"
                    4 -> days[i] = "30"
                    else -> days[i] = "90"
                }

            }

        }
        // update database with new time windows
        myDB.IncrementAll(IDs, Titles, descriptions, increments, days)

    }
}
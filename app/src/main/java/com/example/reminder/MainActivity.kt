package com.example.reminder

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.reminder.SQLDB

class MainActivity : AppCompatActivity() {

    val myDB = SQLDB(this)

    val IDs = ArrayList<String>()
    val Titles = ArrayList<String>()
    val descriptions = ArrayList<String>()
    val increments = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val cursor = myDB.readAllData()
        if(cursor?.count == 0) {
            Toast.makeText(this, "Nothing stored", Toast.LENGTH_SHORT).show()
        }else{
            while (cursor?.moveToNext() == true){
                IDs.add(cursor.getString(0))
                Titles.add(cursor.getString(1))
                descriptions.add(cursor.getString(2))
                increments.add(cursor.getString(3))
            }
        }
    }

    fun DisplayInLinearLayout() {

        for (i in 0 until Titles.size) {

            val vertical_layout = findViewById<LinearLayout>(R.id.VerticalLayout)

            // title text
            val dynamicTextview = TextView(this)
            dynamicTextview.text = Titles.get(i)
            dynamicTextview.setTypeface(null, Typeface.BOLD)
            dynamicTextview.setTextSize(24f)

            val descTextView = TextView(this)
            descTextView.text = descriptions.get(i)

            val removeButton = Button(this)
            removeButton.id = IDs.get(i).toInt()

            removeButton.setOnClickListener() {
                myDB.deleteRow(removeButton.id.toString())
                ResetView()
            }

            val dynamicLayout = LinearLayout(this)
            dynamicLayout.orientation = LinearLayout.VERTICAL
            dynamicLayout.addView(dynamicTextview)
            dynamicLayout.addView(descTextView)
            dynamicLayout.addView(removeButton)
            dynamicLayout.setPadding(64, 16, 0, 16)
            dynamicLayout.id = View.generateViewId()

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

}
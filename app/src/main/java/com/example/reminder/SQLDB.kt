package com.example.reminder

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class SQLDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TBL_REMINDERS = "tbl_reminders"
        private const val ID = "id"
        private const val TITLE = "Title"
        private const val DESC = "Desc"
        private const val INCREMENT = "Increment"

    }
    private val context = context
    override fun onCreate(db: SQLiteDatabase?) {
        // Create your database tables here
        val createTblReminders = ("CREATE TABLE " + TBL_REMINDERS +
                " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                DESC + " TEXT, " +
                INCREMENT + " INTEGER);"
                )
        db?.execSQL(createTblReminders)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Upgrade your database tables here
        db?.execSQL("DROP TABLE IF EXISTS " + TBL_REMINDERS)
        onCreate(db)
    }

    fun addReminder(title: String, desc: String)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(TITLE, title)
        cv.put(DESC, desc)
        cv.put(INCREMENT, 1)
        val result = db.insert(TBL_REMINDERS, null, cv)
        if (result == -1L) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun readAllData(): Cursor? {
        val query = "SELECT * FROM " + TBL_REMINDERS + ";"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        if (db != null) {
            cursor = db.rawQuery(query, null) as Cursor?
        }
        return cursor;
    }

    fun deleteRow(row_id:String) {
        val db = this.writableDatabase
        val result = db.delete(TBL_REMINDERS, "id=?", arrayOf(row_id))

        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }


    }

}

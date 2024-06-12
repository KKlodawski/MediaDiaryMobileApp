package com.example.prmproj2.adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import com.example.prmproj2.model.entry

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "Entries"
        private const val COLUMN_ID = "entryId"
        private const val COLUMN_NOTE = "note"
        private const val COLUMN_LOCATION_LAT = "location_lat"
        private const val COLUMN_LOCATION_LONG = "location_long"
        private const val COLUMN_IMAGE_ID = "imageId"
        private const val COLUMN_VOICE_ID = "voiceId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =
            "CREATE TABLE $TABLE_NAME " +
            "(" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NOTE TEXT, " +
                "$COLUMN_LOCATION_LAT REAL, " +
                "$COLUMN_LOCATION_LONG REAL, " +
                "$COLUMN_IMAGE_ID INTEGER, " +
                "$COLUMN_VOICE_ID INTEGER)"
        db?.execSQL(CREATE_TABLE_QUERY)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun size(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count
            }
        }
        return 0
    }

    fun addEntry(entry: entry): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE, entry.note)
        entry.location?.let {
            values.put(COLUMN_LOCATION_LAT, it.latitude)
            values.put(COLUMN_LOCATION_LONG, it.longitude)
        }
        values.put(COLUMN_IMAGE_ID, entry.imageId)
        values.put(COLUMN_VOICE_ID, entry.voiceId)
        val result = db.insert(TABLE_NAME, null, values)
        return result
    }

    @SuppressLint("Range")
    fun getAllEntries(): List<entry> {
        val entries = mutableListOf<entry>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val entryId = it.getInt(it.getColumnIndex(COLUMN_ID))
                    val note = it.getString(it.getColumnIndex(COLUMN_NOTE))
                    val latitude = it.getDouble(it.getColumnIndex(COLUMN_LOCATION_LAT))
                    val longitude = it.getDouble(it.getColumnIndex(COLUMN_LOCATION_LONG))
                    val location = if (latitude != 0.0 && longitude != 0.0) Location("").apply {
                        this.latitude = latitude
                        this.longitude = longitude
                    } else null
                    val imageId = it.getInt(it.getColumnIndex(COLUMN_IMAGE_ID))
                    val voiceId = it.getInt(it.getColumnIndex(COLUMN_VOICE_ID))
                    val entry = entry(entryId, note, location, imageId, voiceId)
                    entries.add(entry)
                } while (it.moveToNext())
            }
        }
        return entries
    }

    fun truncateTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }

    fun getLastIndex(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX($COLUMN_ID) FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(query, null)
        var lastIndex = -1
        cursor.use {
            if (it.moveToFirst()) {
                lastIndex = it.getInt(0)
            }
        }
        return lastIndex
    }

    @SuppressLint("Range")
    fun getEntryById(id: Int): entry? {
        val entry: entry
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id"
        val cursor: Cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                val entryId = it.getInt(it.getColumnIndex(COLUMN_ID))
                val note = it.getString(it.getColumnIndex(COLUMN_NOTE))
                val latitude = it.getDouble(it.getColumnIndex(COLUMN_LOCATION_LAT))
                val longitude = it.getDouble(it.getColumnIndex(COLUMN_LOCATION_LONG))
                val location = if (latitude != 0.0 && longitude != 0.0) Location("").apply {
                    this.latitude = latitude
                    this.longitude = longitude
                } else null
                val imageId = it.getInt(it.getColumnIndex(COLUMN_IMAGE_ID))
                val voiceId = it.getInt(it.getColumnIndex(COLUMN_VOICE_ID))
                entry = entry(entryId, note, location, imageId, voiceId)
                return entry
            }
        }
        return null
    }

    fun editEntry(entry: entry): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE, entry.note)
        entry.location?.let {
            values.put(COLUMN_LOCATION_LAT, it.latitude)
            values.put(COLUMN_LOCATION_LONG, it.longitude)
        }
        values.put(COLUMN_IMAGE_ID, entry.imageId)
        values.put(COLUMN_VOICE_ID, entry.voiceId)

        return db.update(TABLE_NAME,values,"$COLUMN_ID = ?", arrayOf(entry.entryId.toString())) > 0
    }

}

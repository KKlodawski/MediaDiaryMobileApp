package com.example.prmproj2

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.databinding.ActivityMainBinding
import com.example.prmproj2.model.entry

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Database(context = this)
        if(database.size() == 0){
            database.addEntry(
                entry(entryId = 1,note = "To jest jakaś notatka z miasta warszawa!!!!!!!", imageId = 1, voiceId = 1, location = Location(
                    LocationManager.GPS_PROVIDER).apply{
                    latitude = 52.2297
                    longitude = 21.0122
                })
            )
            database.addEntry(entry(entryId = 2, note = "TestNote2", imageId = 2, voiceId = 2, location = null))
        }

        Log.d("ERSR", "onCreate: ${database.size()}")
        Log.d("ERSR", "onCreate: ${database.getAllEntries()}")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}
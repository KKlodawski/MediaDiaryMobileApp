package com.example.prmproj2.data

import android.annotation.SuppressLint
import com.example.prmproj2.model.entry

object EntryRepositoryObjects : EntryRepository {

    @SuppressLint("ResourceType")
    private val entryList = mutableListOf(
        entry(entryId = 1,note = "TestNote", imageId = 1, voiceId = 1, location = null),
        entry(entryId = 2, note = "TestNote2", imageId = 2, voiceId = 2, location = null)
    )

    override fun getEntryList(): List<entry> {
        return entryList;
    }
}
package com.example.prmproj2.data

import com.example.prmproj2.model.entry

interface EntryRepository {
    fun getEntryList(): List<entry>
}
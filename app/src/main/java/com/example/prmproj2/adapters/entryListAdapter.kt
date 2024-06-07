package com.example.prmproj2.adapters

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj2.R
import com.example.prmproj2.databinding.EntryContentBinding
import com.example.prmproj2.model.entry
import java.io.File

class EntryObject(private val entryViewBinding: EntryContentBinding ) : RecyclerView.ViewHolder(entryViewBinding.root){
    fun onBind(entryObject: entry, context: Context) = with(entryViewBinding){
        noteContent.setText(entryObject.getNote(context))

        val contentResolver = context.contentResolver
        val externalUri =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileName = "entry${entryObject.entryId}.jpg"

        val cursor = contentResolver.query(
            externalUri,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.DISPLAY_NAME} = ?",
            arrayOf(fileName),
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            val imageUri = ContentUris.withAppendedId(externalUri, id)
            val localFile = File(context.filesDir, fileName)
            if(localFile.exists()){
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                image.setImageBitmap(bitmap)
            }
        } else {
            image.setImageResource(R.drawable.entry1)
        }

        cursor?.close()
    }

}

class EntryListAdapter(private val context: Context) : RecyclerView.Adapter<EntryObject>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryObject {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = EntryContentBinding.inflate(layoutInflater, parent, false)

        return EntryObject(binding)
    }

    var entryList: List<entry> = mutableListOf()

    override fun getItemCount(): Int {
        return entryList.size
    }

    override fun onBindViewHolder(holder: EntryObject, position: Int) {
        holder.onBind(entryList[position], context)
    }

}
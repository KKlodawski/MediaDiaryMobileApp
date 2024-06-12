package com.example.prmproj2.adapters

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj2.R
import com.example.prmproj2.databinding.EntryContentBinding
import com.example.prmproj2.model.entry
import java.io.File
import java.io.FileInputStream

class EntryObject(private val entryViewBinding: EntryContentBinding ) : RecyclerView.ViewHolder(entryViewBinding.root){
    private var mediaPlayer: MediaPlayer? = null
    fun onBind(entryObject: entry, context: Context, onItemClick: () -> Unit) = with(entryViewBinding){
        noteContent.setText(entryObject.getNote(context))

        val contentResolver = context.contentResolver
        val externalUri =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileName = "entry${entryObject.imageId}.jpg"

        if(entryObject.imageId != 0) {

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
                if (localFile.exists()) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    image.setImageBitmap(bitmap)
                }
            } else {
                image.setImageResource(R.drawable.entry1)
            }
            cursor?.close()
        } else {
            image.setImageResource(R.drawable.entry1)
        }

        val dirPath = "${context.externalCacheDir?.absoluteFile}/audio${entryObject.voiceId}.mp3"
        val audioF = File(dirPath)

        entryViewBinding.playAudio.apply {
            setOnClickListener {
                if(audioF.exists()) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(dirPath)
                        prepare()
                        start()
                    }
                } else {
                    Log.d("ERSR", "onBind: File doesn't exists ${dirPath}")
                    Toast.makeText(context, R.string.AudioNotExists, Toast.LENGTH_SHORT).show()
                }
            }
        }

        root.setOnClickListener {
            onItemClick()
        }
    }

}

class EntryListAdapter(private val context: Context, private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<EntryObject>() {
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
        holder.onBind(entryList[position], context) { onItemClick(position+1) }

    }

}
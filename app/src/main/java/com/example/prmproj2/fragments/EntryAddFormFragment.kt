package com.example.prmproj2.fragments

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.prmproj2.R
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.databinding.FragmentEntryAddFormBinding
import com.example.prmproj2.model.entry
import java.io.File
import java.io.FileOutputStream

class EntryAddFormFragment : Fragment() {
    lateinit var binding: FragmentEntryAddFormBinding
    lateinit var database: Database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEntryAddFormBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val contentResolver = context?.contentResolver
        val externalUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileName = "entry${database.getLastIndex()+1}.jpg"

        val cursor = contentResolver?.query(
            externalUri,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.DISPLAY_NAME} = ?",
            arrayOf(fileName),
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            val imageUri = ContentUris.withAppendedId(externalUri, id)

            val inputStream = contentResolver.openInputStream(imageUri)
            val outputFile = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(outputFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val bitmap = BitmapFactory.decodeFile(outputFile.path)
            binding.imageView.setImageBitmap(bitmap)
        }

        cursor?.close()



        binding.addImageButton.apply {
            setOnClickListener {
                findNavController().navigate(
                    R.id.action_entryAddFormFragment_to_imageFragment
                )
            }
        }

        binding.saveButton.apply {
            setOnClickListener {
                val index = database.getLastIndex()+1
                database.addEntry(
                    entry(
                        entryId = index,
                        note = binding.editTextTextMultiLine.text.toString(),
                        imageId = index,
                        voiceId = index,
                        location = null
                    )
                )
                findNavController().popBackStack()
            }
        }

    }
}
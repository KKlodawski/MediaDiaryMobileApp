package com.example.prmproj2.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.prmproj2.R
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.databinding.FragmentImageBinding
import com.example.prmproj2.model.FormType
import java.io.File

private const val TYPE_KEY = "type"
class ImageFragment : Fragment() {
    lateinit var type : FormType
    lateinit var binding: FragmentImageBinding
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var database: Database
    var imageUri: Uri? = null

    val onTakePhoto: (Boolean) -> Unit = { photography: Boolean ->
        if(!photography){
            try{
            if(imageUri != null) {
                requireContext().contentResolver.delete(imageUri!!, null, null)
            }
            } catch (e: Exception) {
                Log.d("ERSR", "2:${e.message} ")
            }

        } else {
            loadBitmap()
        }
    }

    private fun loadBitmap() {
        try {
            val imageUri = imageUri ?: return
            requireContext().contentResolver.openInputStream(imageUri)
                ?.use {
                    BitmapFactory.decodeStream(it)
                }?.let {
                    binding.paintView.background = it
                }
        } catch (e: Exception) {
            Log.d("ERSR", "loadBitmap: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database(requireContext())
        arguments?.let {
            type = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, FormType::class.java)
            } else {
                it.getSerializable(TYPE_KEY) as? FormType
            } ?: FormType.New
        }
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            onTakePhoto
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentImageBinding.inflate(layoutInflater,container,false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveImageButton.apply {
            setOnClickListener {
                save()
            }
        }
        createPicture()
    }

    private fun save() {
        val bmp = binding.paintView.generateBitmap()
        if(imageUri != null) {
            requireContext().contentResolver.openOutputStream(imageUri!!)?.use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, it )
            }
        }
        findNavController().popBackStack()
    }

    private fun createPicture() {
        try {
            val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            var ct: ContentValues
            type.let { type ->
                when(type){
                    is FormType.New -> {
                        ct = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "entry${database.getLastIndex() + 1}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        }
                    }
                    is FormType.Edit -> {
                        ct = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "entry${type.id}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        }
                        requireContext().contentResolver.delete(imagesUri,null,null)
                    }
                }
            }


            imageUri = requireContext().contentResolver.insert(imagesUri, ct)
            Log.d("ERSR", "createPicture: ${imagesUri} ${imagesUri == null}")
            if (imagesUri != null) {
                Log.d("ERSR", "createPicture: cameraLaunch")
                cameraLauncher.launch(imageUri)
            }
        } catch (e: Exception) {
            Log.d("ERSR", "1:${e.message}")
        }
    }


}
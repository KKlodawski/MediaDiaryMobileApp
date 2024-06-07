package com.example.prmproj2.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    lateinit var binding: FragmentImageBinding
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var database: Database
    var imageUri: Uri? = null

    val onTakePhoto: (Boolean) -> Unit = { photography: Boolean ->
        if(!photography){
            imageUri?.let {
                requireContext().contentResolver.delete(it, null, null)
            }
        } else {
            loadBitmap()
        }
    }

    private fun loadBitmap() {
        val imageUri = imageUri ?: return
        requireContext().contentResolver.openInputStream(imageUri)
            ?.use {
                BitmapFactory.decodeStream(it)
            }?.let {
                binding.paintView.background = it
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database(requireContext())
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
        imageUri?.let {
            requireContext().contentResolver.openOutputStream(it)?.use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, it )
            }
        }
        findNavController().popBackStack()
    }

    private fun createPicture() {
        val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val ct = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "entry${database.getLastIndex()+1}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        imageUri = requireContext().contentResolver.insert(imagesUri, ct)
        imageUri?.let {
            cameraLauncher.launch(it)
        }
    }



}
package com.example.prmproj2.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.media.MediaRecorder
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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.prmproj2.R
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.databinding.FragmentEntryAddFormBinding
import com.example.prmproj2.model.FormType
import com.example.prmproj2.model.entry
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log

private const val TYPE_KEY = "type"
class EntryAddFormFragment : Fragment() {
    lateinit var type : FormType
    lateinit var binding: FragmentEntryAddFormBinding
    lateinit var database: Database
    private var  mediaRecorder: MediaRecorder? = null
    private var isVoiceSet = false
    private var isImageSet = false
    private var savedEntry: entry? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
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
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    locationOn()
                }
            }

    }

    @SuppressLint("MissingPermission")
    private fun locationOn() {
        binding.map.apply {
            overlays.add(
                MyLocationNewOverlay(this).apply {
                    enableMyLocation()
                }
            )
            getSystemService(requireContext(),LocationManager::class.java)
                ?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                    controller.animateTo(GeoPoint(it.latitude, it.longitude), 20.0, 1000)
                }
        }
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
        var fileName = "entry${database.getLastIndex()+1}.jpg"

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        checkPermissions()

        (type as? FormType.Edit)?.let {
            Log.d("ERSR", "(Edit) onViewCreated: ${database.getEntryById(it.id)}")
            binding.map.isVisible = false
            val loadedEntry = database.getEntryById(it.id)
            if (loadedEntry != null) {
                binding.editTextTextMultiLine.setText(loadedEntry.note)
                savedEntry = loadedEntry
                fileName = "entry${loadedEntry.imageId}.jpg"
            }
        }

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

            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val outputFile = File(requireContext().filesDir, fileName)
                val outputStream = FileOutputStream(outputFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val bitmap = BitmapFactory.decodeFile(outputFile.path)
                binding.imageView.setImageBitmap(bitmap)
            } catch(e: Exception) {

            }
        }

        cursor?.close()

        binding.addImageButton.apply {
            setOnClickListener {
                if (!isPermissionGranted(Manifest.permission.CAMERA)) {
                    isClickable = false
                } else {
                    isClickable = true
                    Log.d("ERSR", "onViewCreated: ${isImageSet}")
                    if (!isImageSet)
                        type.let { type ->
                            Log.d("ERSR", "onViewCreated: ${type}")
                            when(type){
                                is FormType.New -> {
                                    findNavController().navigate(
                                        R.id.action_entryAddFormFragment_to_imageFragment
                                    )
                                }

                                is FormType.Edit -> {
                                    findNavController().navigate(
                                        R.id.action_entryAddFormFragment_to_imageFragment,
                                        bundleOf("type" to FormType.Edit(type.id))
                                    )
                                }
                            }
                        }

                }
            }
        }

        binding.saveButton.apply {
            setOnClickListener {
                if((type as? FormType.Edit)?.id == null) {
                    if (binding.editTextTextMultiLine.text.length > 0) {
                        val index = database.getLastIndex() + 1
                        database.addEntry(
                            entry(
                                entryId = index,
                                note = binding.editTextTextMultiLine.text.toString(),
                                imageId = if (isImageSet) index else null,
                                voiceId = if (isVoiceSet) index else null,
                                location = Location("APP").apply {
                                    longitude = binding.map.mapCenter.longitude
                                    latitude = binding.map.mapCenter.latitude
                                }
                            )
                        )
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, R.string.NoteLengthError, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (binding.editTextTextMultiLine.text.length > 0) {
                        savedEntry?.let {
                            database.editEntry(
                                entry(
                                    entryId = savedEntry!!.entryId,
                                    note = binding.editTextTextMultiLine.text.toString(),
                                    imageId = if(!isImageSet) savedEntry!!.imageId else savedEntry!!.entryId,
                                    voiceId = if(!isVoiceSet) savedEntry!!.voiceId else savedEntry!!.entryId,
                                    location = savedEntry!!.location,
                                    )
                            )
                        }
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, R.string.NoteLengthError, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.audioButton.apply {
            setOnClickListener {
                if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                    isClickable = false
                } else {
                    if (mediaRecorder == null) {
                        createAudio()
                        this.setText(R.string.Recording)
                    } else {
                        stopAudio()
                        this.setText(R.string.Recorded)
                    }
                }
            }
        }


    }
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationOn()
        }
        if (checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            locationOn()
        }
    }

    private fun createAudio() {
        if(!isPermissionGranted(Manifest.permission.RECORD_AUDIO) || !isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) || !isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d("ERSR", "createAudio: not granted")
            permissionLauncher.launch(arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ));
        } else {
            try {
                val dirPath = "${requireContext().externalCacheDir?.absoluteFile}/"
                var fileName = "audio${database.getLastIndex()+1}"

                savedEntry?.let {
                    fileName = "audio${it.entryId}"
                }

                var mr: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(requireContext())
                } else MediaRecorder()

                mr.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile("${dirPath}${fileName}.mp3")

                    prepare()
                    start()

                    mediaRecorder = this
                    isVoiceSet = true
                }
            } catch (e: Exception) {
                Log.d("ERSR", "createAudio: ${e.message}")
            }
        }
    }

    fun stopAudio() {
        mediaRecorder?.stop()
    }
}
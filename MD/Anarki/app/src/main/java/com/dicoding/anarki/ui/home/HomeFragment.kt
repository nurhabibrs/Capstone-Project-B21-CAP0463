package com.dicoding.anarki.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.anarki.R
import com.dicoding.anarki.databinding.FragmentHomeBinding
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.utils.getFileName
import com.dicoding.anarki.utils.snackbar
import com.dicoding.anarki.viemodel.ViewModelFactory
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class HomeFragment : Fragment(), UploadRequest.UploadCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding
    private val homeViewModel by lazy {
        ViewModelProvider(
            this, ViewModelFactory.getInstance(requireActivity())
        ).get(HomeViewModel::class.java)
    }
    private var selectedImageUri: Uri? = null
    private lateinit var photoFile: File
    private lateinit var file: File
    private lateinit var body: UploadRequest

    companion object {
        const val REQUEST_CODE_CAM = 100
        const val REQUEST_CODE_DIR = 101
        private const val FILE_NAME = "photo.jpg"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DummyAds (AdMob)
        MobileAds.initialize(requireActivity()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        binding.apply {
            btnTakePicture.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFile(FILE_NAME)

                val fileProvider = context?.let { it1 ->
                    FileProvider.getUriForFile(
                        it1,
                        "com.dicoding.anarki.fileprovider",
                        photoFile
                    )
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                if (context?.packageManager?.let { it1 -> cameraIntent.resolveActivity(it1) } != null) {
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAM)
                } else {
                    Toast.makeText(context, "Unable to open camera", Toast.LENGTH_SHORT).show()
                }
            }
            btnAddPicture.setOnClickListener {
                val directoryIntent = Intent(Intent.ACTION_PICK)
                directoryIntent.type = "image/"
                startActivityForResult(directoryIntent, REQUEST_CODE_DIR)
            }
            btnPredict.setOnClickListener {
                btnPredict.visibility = View.GONE
                textAction.text = "Waiting for Server"
                uploadImage()
                context?.let { getDataUserFromApi(it) }
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE_CAM && resultCode == Activity.RESULT_OK) {
            //val imageData = data?.extras?.get("data") as Bitmap
            val imageData = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.apply {
                imgPreviewSample.visibility = View.GONE
                imgPreview.visibility = View.VISIBLE
                imgPreview.setImageBitmap(imageData)
                val goImage = getImageUriFromBitmap(imageData)
                selectedImageUri = goImage
                btnPredict.visibility = View.VISIBLE
                //imgPreview.setImageURI(selectedImageUri)
            }
        } else if (requestCode == REQUEST_CODE_DIR && resultCode == Activity.RESULT_OK) {
            binding.apply {
                imgPreviewSample.visibility = View.GONE
                imgPreview.visibility = View.VISIBLE
                selectedImageUri = data?.data
                imgPreview.setImageURI(selectedImageUri)
                btnPredict.visibility = View.VISIBLE
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun getPhotoFile(fileName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun getDataUserFromApi(context: Context) {
        homeViewModel.getPredictionResult(context, file, body).observe(viewLifecycleOwner, { user ->
            binding.progressBar.progress = 100
            binding.progressBar.visibility = View.INVISIBLE
            val result = user.data
            Glide.with(this)
                .load(result?.image)
                .apply(RequestOptions().centerCrop())
                .into(binding.imgPreview)
            val text1 = result?.akurasi

            val text2: String = when (result?.pecandu) {
                true -> {
                    "Positive"
                }
                false -> {
                    "Negative"
                }
                else -> {
                    ""
                }
            }
            binding.tvResultPredict.text =
                resources.getString(R.string.fill_result, text1.toString(), text2)
        })
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.layoutRoot.snackbar("Select an Image First")
            return
        }
        val resolver = requireActivity().contentResolver
        val cacheDir = activity?.externalCacheDir

        val parcelFileDescriptor =
            resolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return


        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        file = File(cacheDir, resolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0

        body = UploadRequest(file, "image", this)
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.progressBar.progress = percentage
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes)
        val resolver = requireActivity().contentResolver
        val path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

}
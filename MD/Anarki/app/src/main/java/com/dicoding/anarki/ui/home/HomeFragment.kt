package com.dicoding.anarki.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.anarki.R
import com.dicoding.anarki.databinding.FragmentHomeBinding
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.utils.getFileName
import com.dicoding.anarki.utils.snackbar
import com.dicoding.anarki.viemodel.ViewModelFactory
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import java.io.*

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
//                if (context?.packageManager?.let { it1 -> cameraIntent.resolveActivity(it1) } != null) {
                    startForCamera.launch(cameraIntent)
//                } else {
//                    Toast.makeText(context, "Unable to open camera", Toast.LENGTH_SHORT).show()
//                }
            }
            btnAddPicture.setOnClickListener {
                val directoryIntent = Intent(Intent.ACTION_PICK)
                directoryIntent.type = "image/"
                startForDirectory.launch(directoryIntent)
            }
            btnPredict.setOnClickListener {
                btnPredict.visibility = View.GONE
                textAction.text = getString(R.string.waiting_server)
                uploadImage()
                context?.let { getDataUserFromApi(it) }
            }

        }
    }

    private val startForCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageData = checkStateAndRotation(photoFile)
                binding.apply {
                    imgPreviewSample.visibility = View.GONE
                    imgPreview.visibility = View.VISIBLE
                    imgPreview.setImageBitmap(imageData)
//                imageData = Bitmap.createScaledBitmap(imageData, 200, 200, false)
                    val goImage = imageData?.let { getImageUriFromBitmap(it) }
                    selectedImageUri = goImage
                    btnPredict.visibility = View.VISIBLE
                }
            }
        }

    private val startForDirectory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.apply {
                    imgPreviewSample.visibility = View.GONE
                    imgPreview.visibility = View.VISIBLE
                    selectedImageUri = result.data?.data
                    imgPreview.setImageURI(selectedImageUri)
                    btnPredict.visibility = View.VISIBLE
                }
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
            val result = user.data
            val text1 = result?.id
            val text2: String = result?.result.toString()
            if (text1 == null) {
                binding.tvResultPredict.text = getString(R.string.waiting_server)
            } else {
                binding.tvResultPredict.text =
                    resources.getString(R.string.fill_result, text1.toString(), text2)
            }
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, bytes)
        val resolver = requireActivity().contentResolver
        val path = MediaStore.Images.Media.insertImage(
            resolver,
            bitmap,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path.toString())
    }

    private fun checkStateAndRotation(imageFile: File): Bitmap? {
        // Dimensions check (true?)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        FileInputStream(imageFile).use { imageStream ->
            BitmapFactory.decodeStream(imageStream, null, options)
        }
        // Bitmap Decode
        options.inJustDecodeBounds = false
        val bitmap = FileInputStream(imageFile).use { imageStream ->
            BitmapFactory.decodeStream(imageStream, null, options)
        }
        return bitmap?.let { rotateImageCondition(it, imageFile) }
    }

    private fun rotateImageCondition(img: Bitmap, imageFile: File): Bitmap {
        val ei = FileInputStream(imageFile).use { ExifInterface(it) }
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

}
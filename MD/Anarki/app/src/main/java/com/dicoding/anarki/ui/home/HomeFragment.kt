package com.dicoding.anarki.ui.home

import android.annotation.SuppressLint
import android.app.Activity
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
import com.dicoding.anarki.databinding.FragmentHomeBinding
import com.dicoding.anarki.model.PredictResponse
import com.dicoding.anarki.model.UploadRequest
import com.dicoding.anarki.network.ConfigNetwork
import com.dicoding.anarki.utils.getFileName
import com.dicoding.anarki.utils.snackbar
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class HomeFragment : Fragment(), UploadRequest.UploadCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding
    private var selectedImageUri: Uri? = null
    private lateinit var photoFile: File

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


        binding.apply {
            btnTakePicture.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFile(FILE_NAME)

                // This DOESN'T work for API >= 24 (starting 2016)
                // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)

                val fileProvider = context?.let { it1 -> FileProvider.getUriForFile(it1, "com.dicoding.anarki.fileprovider", photoFile) }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                if (context?.packageManager?.let { it1 -> cameraIntent.resolveActivity(it1) } != null) {
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAM)
                } else {
                    Toast.makeText(context, "Unable to open camera", Toast.LENGTH_SHORT).show()
                }
                btnPredict.visibility = View.VISIBLE
            }
            btnAddPicture.setOnClickListener {
                val directoryIntent = Intent(Intent.ACTION_PICK)
                directoryIntent.type = "image/"
                startActivityForResult(directoryIntent, REQUEST_CODE_DIR)
                btnPredict.visibility = View.VISIBLE
            }
            btnPredict.setOnClickListener {
                btnPredict.visibility = View.GONE
                uploadImage()

                /*val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                val result = "Drug Addicted"
                tvResultPredict.text = "Timestamp : $currentTime \nPrediction   : $result"*/
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
                val goImage = getImageUriFromBitmap(this, imageData)
                selectedImageUri = goImage
                //imgPreview.setImageURI(selectedImageUri)
            }
        } else if (requestCode == REQUEST_CODE_DIR && resultCode == Activity.RESULT_OK) {
            binding.apply {
                imgPreviewSample.visibility = View.GONE
                imgPreview.visibility = View.VISIBLE
                selectedImageUri = data?.data
                imgPreview.setImageURI(selectedImageUri)
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

    @SuppressLint("UseRequireInsteadOfGet")
    private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.layoutRoot.snackbar("Select an Image First")
            return
        }
        val resolver = requireActivity().contentResolver
        val cacheDir = activity!!.externalCacheDir

        val parcelFileDescriptor =
            resolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return



        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, resolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0
        val body = UploadRequest(file, "image", this)
        val client = ConfigNetwork.getRetrofit()
            client.uploadImage(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                body
            ),
        ).enqueue(object : Callback<PredictResponse> {
            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                binding.layoutRoot.snackbar(t.message!!)
                binding.progressBar.progress = 0
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                response.body()?.let {
                    binding.layoutRoot.snackbar(it.message)
                    binding.progressBar.progress = 100
                    binding.progressBar.visibility = View.INVISIBLE
                }
                val user = response.body()
                val text1 = user!!.akurasi
                val text2bol = user.pecandu

                val text2: String = if (text2bol == true) {
                    "Positive"
                } else {
                    "negative"
                }
                binding.tvResultPredict.text = "Akurasi = $text1 \nPecandu = $text2"
            }
        })

    }

    override fun onProgressUpdate(percentage: Int) {
        binding.progressBar.progress = percentage
    }

    private fun getImageUriFromBitmap(context: FragmentHomeBinding, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val resolver = requireActivity().contentResolver
        val path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

}
package com.daepiro.numberoneproject.presentation.view.community

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.databinding.FragmentCommunityTownWritingBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.util.Extensions.showToast
import com.daepiro.numberoneproject.presentation.viewmodel.CommunityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CommunityTownWritingFragment : BaseFragment<FragmentCommunityTownWritingBinding>(R.layout.fragment_community_town_writing) {
    val viewModel by activityViewModels<CommunityViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val imageUrls = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    private var imageUriList = mutableListOf<String>()
    //private var imagePartList = mutableListOf<MultipartBody.Part>()
    private var title: String = ""
    private var content: String = ""
    private var latitudeForsend = 0.0
    private var longitudeForsend = 0.0
    private var articleTag: String = ""
    private var regionAgreementCheck = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel


        binding.select.setOnClickListener {
            showBottomSheet()
        }

        val checkbox = binding.checkLocationPermission
        checkbox.setOnCheckedChangeListener{_,isChecked->
            if(isChecked){
                regionAgreementCheck = true
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                getCurrentLocation()
            }else{
                regionAgreementCheck = false
                longitudeForsend=0.0
                latitudeForsend=0.0
            }
        }


        binding.backBtn.setOnClickListener{
            findNavController().popBackStack()
        }


        binding.titleTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                title = s.toString()
            }
        })

        binding.contentTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                content = s.toString()
            }
        })

        viewModel.tagData.observe(viewLifecycleOwner, Observer { tag ->
            articleTag = when (tag) {
                "일상" -> "LIFE"
                "교통" -> "TRAFFIC"
                "치안" -> "SAFETY"
                else -> "NONE"
            }
        })

        binding.addPhoto.setOnClickListener {
//            if (checkPermission()) {
//                getImage()
//            }
            //targetsdk에 따라 부여 권한이 다르기 때문에 다음과 같이 해줘야한다
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                requestAllStoragePermission()
                Log.d("adsfas", "요청되었나요?")
            }else{
                requestReadStoragePermission()
                Log.d("adsfas", "요청되었나요1?")
            }
        }

            //저장되기 전에 화면이 전환되서 그런거 아닐까??
        binding.complete.setOnClickListener {
            val imagePartList = UrisToMultipartBody(imageUriList,requireContext().contentResolver)
            postComment(title,content,articleTag,imagePartList,latitudeForsend,longitudeForsend,regionAgreementCheck)
            findNavController().navigateUp()
            Log.d("postComment", "$imagePartList")
        }
    }

    //위치권한 미동의시 권한 요청
    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitudeForsend = it.latitude
                    longitudeForsend = it.longitude
                }
            }
    }


//    private fun checkPermission(): Boolean {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
//            Log.d("checkPermission", "false")
//            return false
//        }
//        Log.d("checkPermission", "true")
//        return true
//    }

    private fun requestReadStoragePermission(){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    getImage()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast("갤러리 접근 필수로 필요합니다.")
                }
            })
            .setPermissions(//READ_EXTERNAL_STORAGE
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun requestAllStoragePermission(){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    getImage()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast("갤러리 접근 필수로 필요합니다.")
                }
            })
            .setPermissions(
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
            .check()
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImage()
                }
                else {
                    Log.e("writingFragment", "갤러리 접근 권한 허용이 필요합니다")
                }
            }
            1-> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    // 권한 거부 처리
                    Log.e("writingFragment", "위치 권한 허용이 필요합니다")
                }
            }
        }
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
// for(element in imageList){
//            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), element)
//            val multiPartBody = MultipartBody.Part.createFormData("imageList[]", element.name, requestFile)
//            files.add(multiPartBody)
//            Log.d("whyitisnts", "File NAme: ${element.name}, ${element.getMimeType()}")
//        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                Log.d("imageList", "${uri.toString()}")
                imageUriList.add(uri.toString())
            }
        }
    }

    private fun UrisToMultipartBody(
        uris : List<String>,
        contentResolver: ContentResolver
    ): List<MultipartBody.Part>{
        val multiPartList = mutableListOf<MultipartBody.Part>()
        uris.forEach { uriString->
            val uri = Uri.parse(uriString)
            try{
                contentResolver.openInputStream(uri)?.use{inputStream ->
                    val tempFile = File.createTempFile("upload_", ".jpg", context?.cacheDir).apply {
                        deleteOnExit()
                    }
                    inputStream.copyTo(tempFile.outputStream())
                    val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val result = MultipartBody.Part.createFormData("imageList", tempFile.name, requestFile)
                    multiPartList.add(result)
                }
            }catch (e:Exception){
                Log.d("UrisToMultipartBody", "$uriString")
                null
            }
        }
        return multiPartList
    }

    private fun createTempFileFromUri(uri: Uri): File?{
        return try{
            val tempFile = File.createTempFile("upload", ".jpg", context?.cacheDir).apply {
                context?.contentResolver?.openInputStream(uri)?.use{input->
                    FileOutputStream(this).use{output->
                        input.copyTo(output)
                    }
                }
            }
            tempFile
        }catch (e:IOException){
            e.printStackTrace()
            null
        }
    }


//    private fun bitmapToMultipartBody(bitmap: Bitmap): MultipartBody.Part {
//        // 비트맵을 바이트 배열로 변환
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//
//        // 바이트 배열을 RequestBody로 변환
//        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
//
//        // MultipartBody.Part로 변환
//        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
//    }

//    private fun uriToBitmap(uri:Uri):Bitmap?{
//        val inputStream = requireContext().contentResolver.openInputStream(uri)
//        return BitmapFactory.decodeStream(inputStream)
//    }
//
//    private fun addImageToImageUrls(imageUrl: String) {
//        imageUrls.add(imageUrl)
//        updateImageViews()
//    }

    private fun updateImageViews() {
        val imageViews = listOf(binding.image1, binding.image2, binding.image3)
        val cardViews = listOf(binding.card1, binding.card2, binding.card3)

        imageUrls.forEachIndexed { index, imageUrl ->
            if (index < imageViews.size) {
                loadImageIntoImageView(imageUrl, imageViews[index], cardViews[index])
            }
        }
    }

    private fun loadImageIntoImageView(imageUrl: String, imageView: ImageView, cardView: CardView) {
        cardView.visibility = View.VISIBLE
        Glide.with(this).load(imageUrl).into(imageView)
    }

    //게시글 작성 api요청
    private fun postComment(title:String, content:String, articleTag:String, imageList:List<MultipartBody.Part>, longitude:Double, latitude:Double, regionAgreementCheck:Boolean){
        viewModel.postComment(title, content, articleTag,imageList, latitude, longitude, regionAgreementCheck)
        Log.d("postComment1", "${title}, ${content}, ${latitude} , ${longitude} , ${imageList}")
    }


    private fun showBottomSheet() {
        val bottomSheet = TagSelectBottomFragment()
        bottomSheet.show(parentFragmentManager, "select")
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
        private const val GALLERY_REQUEST_CODE = 101
    }
}




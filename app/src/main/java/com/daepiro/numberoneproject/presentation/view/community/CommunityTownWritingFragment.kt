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
    private lateinit var adapter : CommunityWritingImageAdapter
    private var selectedImageUri: Uri? = null
    private var imageUriList = mutableListOf<String>()
    private var title: String = ""
    private var content: String = ""
    private var latitudeForsend = 0.0
    private var longitudeForsend = 0.0
    private var articleTag: String = ""
    private var regionAgreementCheck = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        adapter = CommunityWritingImageAdapter(mutableListOf())
        binding.imgList.adapter = adapter
        viewModel._tagData.value = null

        adapter.onImageRemoved = { uri->
            imageUriList.remove(uri)
        }

        binding.select.setOnClickListener {
            showBottomSheet()
        }

        binding.checkLocationPermission.setOnCheckedChangeListener{_,isChecked->
            if(isChecked) {
                regionAgreementCheck = true
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                getCurrentLocation()
            } else {
                regionAgreementCheck = false
                longitudeForsend=0.0
                latitudeForsend=0.0
            }
        }

        binding.checkContainer.setOnClickListener {
            binding.checkLocationPermission.performClick()
        }

        binding.backBtn.setOnClickListener {
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestAllStoragePermission()
            }else{
                requestReadStoragePermission()
            }
        }


        binding.complete.setOnClickListener {
            val imagePartList = UrisToMultipartBody(imageUriList,requireContext().contentResolver)
            postComment(title,content,articleTag,imagePartList,latitudeForsend,longitudeForsend,regionAgreementCheck)
            viewModel._tagData.value = null
            imageUriList = mutableListOf()
            findNavController().navigateUp()
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
            .setPermissions(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                binding.imgContainer.visibility = View.VISIBLE
                imageUriList.add(uri.toString())
                adapter.addImage(uri.toString())

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
                null
            }
        }
        return multiPartList
    }


    private fun postComment(title:String, content:String, articleTag:String, imageList:List<MultipartBody.Part>, longitude:Double, latitude:Double, regionAgreementCheck:Boolean){
        viewModel.postComment(title, content, articleTag,imageList, latitude, longitude, regionAgreementCheck)
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




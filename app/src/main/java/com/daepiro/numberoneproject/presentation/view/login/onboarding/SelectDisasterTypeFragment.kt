package com.daepiro.numberoneproject.presentation.view.login.onboarding

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.DisasterTypeModel
import com.daepiro.numberoneproject.data.model.DisastertypeDataModel
import com.daepiro.numberoneproject.data.model.InitDataOnBoardingRequest
import com.daepiro.numberoneproject.databinding.FragmentSelectDisasterTypeBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.viewmodel.OnboardingViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectDisasterTypeFragment : BaseFragment<FragmentSelectDisasterTypeBinding>(R.layout.fragment_select_disaster_type) {
    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var adapter: GridviewAdapter
    private val selectedItems = mutableListOf<DisastertypeDataModel>()
    private var totalItems = listOf<DisasterTypeModel>()
    private var fcmToken = ""
    private var currentCategory = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.allCategory.isSelected = true
        val data = setData()

        //일부 글자 색상 변경
        var fullText = binding.sub.text
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("재난 유형")
        val end = start + "재난 유형".length

        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange_500)),
            start, // 변경할 텍스트의 시작 인덱스
            end, // 변경할 텍스트의 끝 인덱스
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.sub.text = spannable
        binding.check.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                adapter.selectAllItems()
                selectedItems.clear()
                selectedItems.addAll(adapter.original)
                updateButtonColor(true)
            } else {
                adapter.deselectAllItems()
                selectedItems.clear()
            }
            sendDisasterType()
        }
        binding.checkcontainer.setOnClickListener {
            binding.check.performClick()
        }

        binding.allCategory.setOnClickListener {
            currentCategory = ""
            clearSelectionsExcept(binding.allCategory)
            adapter.filterByCategory(currentCategory, selectedItems)
            adapter.updateList(setUpdateData(), selectedItems)
        }

        binding.naturlDisaster.setOnClickListener {
            currentCategory = "자연재난"
            clearSelectionsExcept(binding.naturlDisaster)
            adapter.filterByCategory(currentCategory, selectedItems)
        }
        binding.socialDisaster.setOnClickListener {
            currentCategory = "사회재난"
            clearSelectionsExcept(binding.socialDisaster)
            adapter.filterByCategory(currentCategory, selectedItems)
        }
        binding.emergency.setOnClickListener {
            currentCategory = "비상대비"
            clearSelectionsExcept(binding.emergency)
            adapter.filterByCategory(currentCategory, selectedItems)
        }
        binding.etc.setOnClickListener {
            currentCategory = "기타"
            clearSelectionsExcept(binding.etc)
            adapter.filterByCategory(currentCategory, selectedItems)
        }


        //온보딩시 초기 입력 데이터 전송 fcm토큰 넣어야함
        binding.completeBtn.setOnClickListener {
            sendDisasterType()
            Log.d("completeBtn","$totalItems")
            Log.d("completeBtn","$selectedItems")
            val body = InitDataOnBoardingRequest(
                realname = viewModel.realname,
                nickname = viewModel.nickname,
                fcmToken = fcmToken,
                addresses = viewModel.getAddressForApi.toList(),
                disasterTypes =  totalItems
            )
            lifecycleScope.launch {
                viewModel.postInitData(body)
            }

            val action = SelectDisasterTypeFragmentDirections.actionSelectDisasterTypeFragmentToGuideLastFragment()
            findNavController().navigate(action)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setFcmTokenListener()
    }

    private fun clearSelectionsExcept(exceptTextView: TextView) {
        val textViews = listOf(binding.allCategory, binding.naturlDisaster, binding.socialDisaster, binding.emergency, binding.etc)

        textViews.forEach { textView ->
            textView.isSelected = textView == exceptTextView
        }
    }

    private fun setFcmTokenListener() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            fcmToken = task.result
            Log.d("taag fcmToken", fcmToken)
        })
    }

    override fun subscribeUi() {
        super.subscribeUi()
    }

    override fun setupInit() {
        super.setupInit()
        setupRecycler(setData())
    }
    private fun setupRecycler(data: List<DisastertypeDataModel>) {
        binding.recyclerview.layoutManager = GridLayoutManager(requireContext(),3)
        adapter = GridviewAdapter(
            data,
            object : GridviewAdapter.onItemClickListener{
                override fun onItemClickListener(disasterType: String, isSelected: Boolean) {
                        handleItemClick(disasterType, isSelected)
                }
            }
        )
        binding.recyclerview.adapter = adapter
        //초기선택상태반영
        adapter.updateList(setUpdateData(),selectedItems)
    }

    private fun handleItemClick(disasterType: String, isSelected: Boolean) {
        val item = adapter.original.find {it.disasterType == disasterType}
        item?.let{
            it.isSelected = isSelected
            if(isSelected) {
                if(!selectedItems.contains(it)) {
                    selectedItems.add(it)
                } else{}
            } else {
                selectedItems.remove(it)
            }
        }
        adapter.notifyDataSetChanged()
        updateCheckAllStatus()
        updateButtonColor(selectedItems.isNotEmpty())
}

    private fun updateButtonColor(isAnyItemSelected: Boolean) {
        if (isAnyItemSelected) {
            binding.completeBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.orange_500))
            binding.completeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.completeBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface))
            binding.completeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_100))
        }
    }

    fun sendDisasterType(){
        val disasterTypeList = selectedItems.filter{it.isSelected}
            .map{ DisasterTypeModel(it.disasterType) }
        totalItems = disasterTypeList
        Log.d("sendDisasterType","$selectedItems")
    }

    private fun updateCheckAllStatus() {
        val isAllSelected = adapter.original.all { it.isSelected }
        binding.check.setOnCheckedChangeListener(null)
        binding.check.isChecked = isAllSelected
        binding.check.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                adapter.selectAllItems()
                selectedItems.clear()
                selectedItems.addAll(adapter.original)
            } else{
                adapter.deselectAllItems()
                selectedItems.clear()
            }
            updateButtonColor(selectedItems.isNotEmpty())
            sendDisasterType()
        }
    }

    private fun setData():List<DisastertypeDataModel> = listOf(
        DisastertypeDataModel("자연재난","가뭄",R.drawable.ic_drought),
        DisastertypeDataModel("자연재난","강풍",R.drawable.ic_wind),
        DisastertypeDataModel("사회재난","가스",R.drawable.ic_gas),
        DisastertypeDataModel("자연재난","건조",R.drawable.ic_dry),
        DisastertypeDataModel("사회재난","교통",R.drawable.ic_traffic),
        DisastertypeDataModel("사회재난","금융",R.drawable.ic_money),
        DisastertypeDataModel("자연재난","대설",R.drawable.ic_snow),
        DisastertypeDataModel("자연재난","대조기",R.drawable.ic_daejo),
        DisastertypeDataModel("자연재난","미세먼지",R.drawable.ic_dust),
        DisastertypeDataModel("사회재난","붕괴",R.drawable.ic_collide),
        DisastertypeDataModel("비상대비","비상사태",R.drawable.ic_emergency),
        DisastertypeDataModel("자연재난","산불",R.drawable.ic_monutinefire),
        DisastertypeDataModel("자연재난","산사태",R.drawable.ic_landslide),
        DisastertypeDataModel("사회재난","수도",R.drawable.ic_water),
        DisastertypeDataModel("기타","실종",R.drawable.ic_missing),
        DisastertypeDataModel("자연재난","안개",R.drawable.ic_fog),
        DisastertypeDataModel("사회재난","에너지",R.drawable.ic_energy),
        DisastertypeDataModel("사회재난","의료",R.drawable.ic_medical),
        DisastertypeDataModel("사회재난","전염병",R.drawable.ic_pendemic),
        DisastertypeDataModel("사회재난","정전",R.drawable.ic_lightoff),
        DisastertypeDataModel("자연재난","지진",R.drawable.ic_earthquake),

        DisastertypeDataModel("자연재난","태풍",R.drawable.ic_storm),
        DisastertypeDataModel("비상대비","테러",R.drawable.ic_terror),
        DisastertypeDataModel("사회재난","통신",R.drawable.ic_network),
        DisastertypeDataModel("사회재난","폭발",R.drawable.ic_explosion),
        DisastertypeDataModel("자연재난","폭염",R.drawable.ic_hot),
        DisastertypeDataModel("자연재난","풍랑",R.drawable.ic_wave),
        DisastertypeDataModel("자연재난","한파",R.drawable.ic_cold),
        DisastertypeDataModel("자연재난","호우",R.drawable.ic_rain),
        DisastertypeDataModel("자연재난","홍수",R.drawable.ic_flood),
        DisastertypeDataModel("비상대비","화생방사고",R.drawable.ic_cbr),
        DisastertypeDataModel("사회재난","화재",R.drawable.ic_fire),
        DisastertypeDataModel("사회재난","환경오염사고",R.drawable.ic_environment),
        DisastertypeDataModel("사회재난","AI",R.drawable.ic_ai),
        DisastertypeDataModel("기타","기타",R.drawable.ic_add),
        )
    private fun setUpdateData(): List<DisastertypeDataModel> {
        return setData().map { newItem ->
            val isSelected = selectedItems.any { it.disasterType == newItem.disasterType }
            newItem.copy(isSelected = isSelected)
        }
    }

}
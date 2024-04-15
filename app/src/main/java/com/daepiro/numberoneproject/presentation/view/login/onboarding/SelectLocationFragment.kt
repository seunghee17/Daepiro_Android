package com.daepiro.numberoneproject.presentation.view.login.onboarding

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.databinding.FragmentSelectLocationBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.view.networkerror.LocationAdapter
import com.daepiro.numberoneproject.presentation.viewmodel.OnboardingViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding>(R.layout.fragment_select_location) {
    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var adapter1: LocationAdapter
    private lateinit var adapter2: LocationAdapter
    private lateinit var adapter3: LocationAdapter

    var selectedItems1 = mutableMapOf<Int, String>()
    var selectedItems2 = mutableMapOf<Int, String>()
    var selectedItems3 = mutableMapOf<Int, String>()

    private var addressesList = mutableListOf<Map<String, String>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        //일부 글자 색상 변경
        var fullText = binding.title.text
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("지역")
        val end = start + "지역".length

        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange_500)), // 색상을 변경할 Span 객체
            start, // 변경할 텍스트의 시작 인덱스
            end, // 변경할 텍스트의 끝 인덱스
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.title.text = spannable

        binding.btn.setOnClickListener {
            addressesList.forEach { address ->
                viewModel.updateData(address)
            }
            val action = SelectLocationFragmentDirections.actionSelectLocationFragmentToSelectDisasterTypeFragment()
            findNavController().navigate(action)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupInit()
        subscribeUi()
    }

    override fun setupInit() {
        super.setupInit()
        setUpClickListener()
        setupTabtabs()
        setupAdapters()
        binding.select1.isSelected = false
        binding.select2.isSelected = false
        binding.select3.isSelected = false
    }

    override fun subscribeUi() {
        super.subscribeUi()
    }

    private fun setList(resourceId: Int) {
        val localList = requireContext().resources.getStringArray(resourceId).toList()
        adapter1.updateList(localList)
        adapter2.updateList(localList)
        adapter3.updateList(localList)
    }

    private fun setUpClickListener() {
        binding.select1.setOnClickListener {
            handleSelectClick(
                binding.locationSelect1.tabLayout,
                selectedItems1,
                binding.locationSelect1.root
            )
        }
        binding.select1Img.setOnClickListener {
            handleSelectClick(
                binding.locationSelect1.tabLayout,
                selectedItems1,
                binding.locationSelect1.root
            )
        }

        binding.select2.setOnClickListener {
            handleSelectClick(
                binding.locationSelect2.tabLayout,
                selectedItems2,
                binding.locationSelect2.root,
                mapOf(binding.plus2 to View.GONE, binding.select2Txt to View.VISIBLE)
            )
            binding.select2Txt.text = ""
        }
        binding.select2Img.setOnClickListener {
            handleSelectClick(
                binding.locationSelect2.tabLayout,
                selectedItems2,
                binding.locationSelect2.root,
                mapOf(binding.plus2 to View.GONE, binding.select2Txt to View.VISIBLE)
            )
            binding.select2Txt.text = ""
        }

        binding.select3.setOnClickListener {
            handleSelectClick(
                binding.locationSelect3.tabLayout,
                selectedItems3,
                binding.locationSelect3.root,
                mapOf(binding.plus3 to View.GONE, binding.select3Txt to View.VISIBLE)
            )
            binding.select3Txt.text = ""
        }
        binding.select3Img.setOnClickListener {
            handleSelectClick(
                binding.locationSelect3.tabLayout,
                selectedItems3,
                binding.locationSelect3.root,
                mapOf(binding.plus3 to View.GONE, binding.select3Txt to View.VISIBLE)
            )
            binding.select3Txt.text = ""
        }
    }

    private fun handleSelectClick(tabLayout: TabLayout, selectedItems: MutableMap<Int, String>, visibilityView: View, additionalVisibilityViews: Map<View, Int> = emptyMap()) {
        val isSelected = !visibilityView.isSelected
        visibilityView.isSelected = isSelected

        if (isSelected) {
            selectedItems.clear()
            tabLayout.getTabAt(0)?.select()
            setList(R.array.all)
            visibilityView.visibility = View.VISIBLE
            additionalVisibilityViews.forEach { (view, visibility) -> view.visibility = visibility }
        } else {
            visibilityView.visibility = View.GONE
            additionalVisibilityViews.forEach { (view, _) -> view.visibility = View.GONE }
        }
    }

    private fun setupTabtabs() {
        setupTabLayout(binding.locationSelect1.tabLayout, selectedItems1)
        setupTabLayout(binding.locationSelect2.tabLayout, selectedItems2)
        setupTabLayout(binding.locationSelect3.tabLayout, selectedItems3)
    }

    private fun setupAdapters() {
        adapter1 = LocationAdapter(emptyList()).apply {
            itemClickListener = { position, value ->
                val currentTabPosition = binding.locationSelect1.tabLayout.selectedTabPosition
                moveToNextTab(binding.locationSelect1.tabLayout, currentTabPosition, value, selectedItems1,0)
            }
        }
        binding.locationSelect1.recyclerList.adapter = adapter1

        adapter2 = LocationAdapter(emptyList()).apply {
            itemClickListener = { position, value ->
                val currentTabPosition = binding.locationSelect2.tabLayout.selectedTabPosition
                moveToNextTab(binding.locationSelect2.tabLayout, currentTabPosition, value, selectedItems2,1)
            }
        }
        binding.locationSelect2.recyclerList.adapter = adapter2

        adapter3 = LocationAdapter(emptyList()).apply {
            itemClickListener = { position, value ->
                val currentTabPosition = binding.locationSelect3.tabLayout.selectedTabPosition
                moveToNextTab(binding.locationSelect3.tabLayout, currentTabPosition, value, selectedItems3,2)
            }
        }
        binding.locationSelect3.recyclerList.adapter = adapter3

        setList(R.array.all)
    }

    private fun setupTabLayout(tabLayout: TabLayout, selectedItemsMap: MutableMap<Int, String>) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 먼저 아이템 선택되었는지 확인
                if (tab != null && tab.position != 0 && selectedItemsMap[0].isNullOrEmpty()) {
                    tabLayout.getTabAt(0)?.select()
                    return
                }
                tab?.let { selectTab ->
                    val resourceName = when (selectTab.position) {
                        0 -> "all"
                        1 -> selectedItemsMap[0] ?: "all"
                        else -> {
                            (0 until selectTab.position)
                                .mapNotNull { selectedItemsMap[it] }
                                .joinToString(separator = "_")
                        }
                    }
                    val resourceId = requireContext().resources.getIdentifier(resourceName, "array", requireContext().packageName)
                    if (resourceId > 0) {
                        setList(resourceId)
                    } else {
                        Log.e("onTabSelected", "invalid resource id")
                    }
                    for (i in selectTab.position + 1 until tabLayout.tabCount) {
                        val isTabAble = selectedItemsMap.containsKey(i - 1)
                        tabLayout.getTabAt(i)?.view?.isClickable = isTabAble
                    }
                }
                Log.d("onViewCreated", "$selectedItemsMap")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

private fun moveToNextTab(currentTabLayout: TabLayout, currentTab: Int, value: String, selectedItemsMap: MutableMap<Int, String>, index: Int) {
    selectedItemsMap[currentTab] = value
    val addressObject = mapOf(
        "lv1" to (selectedItemsMap[0] ?: ""),
        "lv2" to (selectedItemsMap[1] ?: ""),
        "lv3" to (selectedItemsMap[2] ?: "")
    )
    while (addressesList.size <= index) {
        addressesList.add(mapOf("lv1" to "", "lv2" to "", "lv3" to ""))
    }
    addressesList.set(index,addressObject)

    val fullAddress = addressesList.joinToString(separator = " ") {
        "${it["lv1"]} ${it["lv2"]} ${it["lv3"]}".trim()
    }
    viewModel.updateShowAddress(addressObject, index)

    if (currentTab < currentTabLayout.tabCount - 1) {
        currentTabLayout.getTabAt(currentTab + 1)?.apply {
            view?.isClickable = true
            select()
        }
    } else {
        // 마지막 선택시, 다음 탭 레이아웃 초기화
        if (currentTabLayout == binding.locationSelect1.tabLayout) {
            initializeNextTabLayout(binding.locationSelect2.tabLayout)
        } else if (currentTabLayout == binding.locationSelect2.tabLayout) {
            initializeNextTabLayout(binding.locationSelect3.tabLayout)
        }
        when (currentTabLayout) {
            binding.locationSelect1.tabLayout -> binding.locationSelect1.root.visibility = View.GONE
            binding.locationSelect2.tabLayout -> binding.locationSelect2.root.visibility = View.GONE
            binding.locationSelect3.tabLayout -> binding.locationSelect3.root.visibility = View.GONE
        }
    }
}


    private fun initializeNextTabLayout(nextTabLayout: TabLayout) {
        val resourceId = R.array.all
        setListForTabLayout(nextTabLayout, resourceId)
    }

    private fun setListForTabLayout(tabLayout: TabLayout, resourceId: Int) {
        val localList = requireContext().resources.getStringArray(resourceId).toList()
        val adapter = when (tabLayout) {
            binding.locationSelect1.tabLayout -> adapter1
            binding.locationSelect2.tabLayout -> adapter2
            binding.locationSelect3.tabLayout -> adapter3
            else -> return
        }
        adapter.updateList(localList)
    }
}











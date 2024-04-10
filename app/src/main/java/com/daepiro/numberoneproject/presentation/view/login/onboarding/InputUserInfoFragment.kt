package com.daepiro.numberoneproject.presentation.view.login.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.databinding.FragmentInputUserInfoBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.util.Extensions.showToast
import com.daepiro.numberoneproject.presentation.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InputUserInfoFragment: BaseFragment<FragmentInputUserInfoBinding>(R.layout.fragment_input_user_info) {
    private val viewModel:OnboardingViewModel by activityViewModels()
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnTouchListener { v, event ->
            hideKeyboard(v)
            true
        }

        val originalText = getString(R.string.대피로에서_사용하실_닉네임을_설정해주세요_)
        val spannableString = SpannableString(originalText)
        val startIndex = originalText.indexOf(getString(R.string.닉네임))
        val startIndex2 = originalText.indexOf(getString(R.string.이름))

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.orange_500)),
            startIndex,
            startIndex + getString(R.string.닉네임).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.orange_500)),
            startIndex2,
            startIndex2 + getString(R.string.이름).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvNickname.text = spannableString


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnNext.setOnClickListener {
            if (binding.nameEdit.text.isNotEmpty() && binding.nicknameEdit.text.isNotEmpty()) {
                val action = InputUserInfoFragmentDirections.actionInputUserInfoFragmentToSelectLocationFragment()
                findNavController().navigate(action)
            } else {
                showToast("이름과 닉네임을 입력해주세요.")
            }

        }

        binding.nameEdit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.realname = p0.toString()
            }

        })

        binding.nicknameEdit.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.nickname = p0.toString()
            }

        })
    }
}
package com.daepiro.numberoneproject.presentation.view.community

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.data.model.CommunityRereplyRequestBody
import com.daepiro.numberoneproject.data.model.CommunityTownReplyRequestBody
import com.daepiro.numberoneproject.databinding.FragmentCommunityTownDetailBinding
import com.daepiro.numberoneproject.presentation.base.BaseFragment
import com.daepiro.numberoneproject.presentation.util.Extensions.repeatOnStarted
import com.daepiro.numberoneproject.presentation.view.MainActivity
import com.daepiro.numberoneproject.presentation.viewmodel.CommunityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CommunityTownDetailFragment : BaseFragment<FragmentCommunityTownDetailBinding>(R.layout.fragment_community_town_detail) {
    val viewModel by activityViewModels<CommunityViewModel>()
    private lateinit var adapter:CommunityTownDetailImageAdapter
    private lateinit var adapterReply : CommunityTownDetailReplyAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        adapter = CommunityTownDetailImageAdapter(emptyList())
        setUpReplyRecyclerView()
        binding.images.adapter = adapter
        binding.replyRecycler.adapter = adapterReply
        collectReply()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        collectImage()
        collectTitle()


        binding.replyContainer.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.updateContent(p0.toString())
            }

        })

        binding.complete.setOnClickListener{
            //ollectWriting()
            if(viewModel.townDetail.value.articleId != null){
                val articleId = viewModel.townDetail.value.articleId
                val response = binding.replyContainer.text.toString()
                if(response.isNotEmpty()){
                    collectWriting(articleId,response)
                    binding.replyContainer.text.clear()
                }
            }
            binding.replyContainer.text.clear()
        }

        binding.additional.setOnClickListener{
            showBottomSheet()
            collectCommentDelete()
        }
    }
    //사진 데이터 감지
    private fun collectImage(){
        repeatOnStarted {
            viewModel.townDetail.collect{response->
                if(response.imageUrls == null){
                    viewModel._isVisible.value = false
                }
                response.imageUrls?.let {
                    adapter.updateList(it)
                    viewModel._isVisible.value = true
                }
            }
        }
    }

    private fun collectTitle(){
        repeatOnStarted {
            viewModel.townDetail.collect{response->
                if(response != null){
                    binding.title.text = response.title
                    //userporfile
                    Glide.with(binding.userProfile)
                        .load(response.ownerProfileImageUrl)
                        .error(R.drawable.character_progress)
                        .into(binding.userProfile)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpReplyRecyclerView(){
        adapterReply = CommunityTownDetailReplyAdapter(emptyList(),object : CommunityTownDetailReplyAdapter.onItemClickListener{
            override fun onAdditionalItemClick(commentid: Int) {
                showBottomSheet()
                //deleteReply
                collectReplyDelete(commentid)
            }

            override fun onReplyClick(commentid:Int) {
                if(binding.replyContainer.text.toString().isNotEmpty()){
                    //대댓글작성
                    val data = CommunityRereplyRequestBody(
                        content = binding.replyContainer.text.toString()
                    )
                    val articleId = viewModel.townDetail.value.articleId
                    viewModel.writeRereply(articleId,commentid,data)
                }
            }
        },viewModel::getTimeDifference
        )
    }

    //댓글 업데이트
    private fun collectReply(){
        repeatOnStarted {
            viewModel.replyResult.collect{response ->
                if(response.isEmpty()){
                    //return@collect
                    binding.replyRecycler.visibility = View.GONE
                }else{
                    binding.replyRecycler.visibility = View.VISIBLE
                    adapterReply.updateList(response)
                }

            }
        }
    }

    private fun collectWriting(articleId:Int,response:String) {
        val data = CommunityTownReplyRequestBody(
            content = response,
            longitude = 0.0,
            latitude = 0.0
        )
        viewModel.writeReply(articleId, data)
    }

    //게시물 삭제 상태 체크
    private fun collectCommentDelete(){
        val articleId = viewModel.townDetail.value.articleId
        repeatOnStarted {
            viewModel.additionalState.collect{state->
                if(state == "삭제하기"){
                    viewModel.deleteComment(articleId)
                }
            }
        }
    }

    //댓글 삭제 상태 체크
    private fun collectReplyDelete(commentid:Int){
        repeatOnStarted {
            viewModel.additionalState.collect{state->
                if(state == "삭제하기"){
                    viewModel.deleteReply(commentid)
                }
            }
        }
    }
    private fun showBottomSheet(){
        val bottomSheet = CommunityReplyBottomSheetFragment()
        bottomSheet.show(parentFragmentManager,"select")
    }

}
package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.utils.BaseDiffUtil
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.adapter_subscribers.view.*
import kotlinx.android.synthetic.main.adapter_subscribers.view.tv_age
import kotlinx.android.synthetic.main.adapter_subscribers.view.tv_gender

class SubscribingAdapter(
    private val member: MutableList<Member>,
    private val context: Context,
    private val memberViewModel: MemberViewModel,
    private val m_seq: String,
    private val onClickProfileListener: OnClickProfileListener,
    private val onEndlessScrollListener: SubscribersAdapter.OnEndlessScrollListener
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_ITEM = 1
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        mcontext = parent.context
        val view: View?
        return when (viewType) {
            VIEW_TYPE_LOADING ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
                LoadingViewHolder(view, isLoadingAdded)
            }
            VIEW_TYPE_ITEM -> { view =LayoutInflater.from(context).inflate(R.layout.adapter_subscribers, parent, false)
               ViewHolder(view, isLoadingAdded, onEndlessScrollListener)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){

            VIEW_TYPE_LOADING ->{
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
            }

            VIEW_TYPE_ITEM -> {
                val viewHolder: ViewHolder = holder as ViewHolder
                val item = member[position]
                var viewModel = memberViewModel
                var seq = m_seq
                viewHolder.bindView(item, viewModel,seq,onClickProfileListener)
            }
        }
    }

    fun updateList(members: MutableList<Member>) {

        this.member.clear()
        this.member.addAll(members)
        notifyItemInserted(this.member.size)
        // diif util 리사이클러뷰 재활용 능력 향상시켜줌 깜빡임 현상없어짐
//        val diffUtil = BaseDiffUtil(members, this.member)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//
//        this.member.clear()
//        this.member.addAll(members)
//        diffResult.dispatchUpdatesTo(this)

    }
    override fun getItemViewType(position: Int): Int {
        return if(position == member.size ) VIEW_TYPE_LOADING
        else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if(member == null) 0
        else member.size
    }

    class ViewHolder(itemView: View,
                     isLoadingAdded: Boolean,
                     onEndlessScrollListener: SubscribersAdapter.OnEndlessScrollListener): RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String
        private var isLoadingAdded = isLoadingAdded
        private val onEndlessScrollListener = onEndlessScrollListener

        fun bindView(item: Member, memberViewModel: MemberViewModel,m_seq: String,onClickProfileListener: OnClickProfileListener) {

            // 마지막 피드 인지 아닌지 체크
            if(item.last_index.equals("true")){
                isLoadingAdded = false
                onEndlessScrollListener.OnEndless(true)
            }else if(item.last_index.equals("false")) {
                isLoadingAdded = true
                onEndlessScrollListener.OnEndless(false)
            }

            // my_m_seq 가져오기
            val preferences: SharedPreferences = itemView.context.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
            my_m_seq = preferences.getString("inputMseq", "")

            // 환경설정 스위치
            val preferences_push: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)

            itemView.tv_profile_nick.text = item.nick_name
            itemView.tv_gender.text = item.gender
            itemView.tv_age.text = item.age

            if(item.profile_img == null){
                var profile_img = "http://storymaw.com/data/member/user.png"
                Glide.with(itemView.context)
                    .load(profile_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(itemView.iv_profile)
            }else {
                Glide.with(itemView.context)
                    .load(item.profile_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.white)
                    .into(itemView.iv_profile)
            }

            if(my_m_seq == item.m_seq){

                itemView.check_follow.visibility = View.INVISIBLE
            }

            itemView.check_follow.apply {
                memberViewModel.memberSubscribeChecked2(item.m_seq!!, my_m_seq, "checked", this, context)
            }

            itemView.check_follow.setOnClickListener {
                if (it.check_follow.isChecked == true) {
                    it.check_follow.text = "구독취소"

                    itemView.check_follow.setTextColor(itemView.resources.getColor(R.color.white))
                    memberViewModel.memberSubscribe2(item.m_seq!!, m_seq, "true")
                    memberViewModel.addNotifiaction(my_m_seq,item.m_seq!!,0,"구독알림","님이 구독중 입니다.")
                    //구독하기 push
                    memberViewModel.memberPush(item.m_seq!!,my_m_seq,"subscriber")

                } else {
                    it.check_follow.text = "구독하기"
                    itemView.check_follow.setTextColor(itemView.resources.getColor(R.color.black))
                    memberViewModel.memberSubscribe2(item.m_seq!!, m_seq, "false")

                }
            }

            val onClickProfile = onClickProfileListener

            itemView.layout_item.setOnClickListener {
                onClickProfile.OnClickProfile(item, itemView.tv_profile_nick, itemView.iv_profile)

            }

        }
    }


    internal class LoadingViewHolder(itemView: View,isLoadingAdded:Boolean):RecyclerView.ViewHolder(itemView){
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private var isLoading = isLoadingAdded
        fun bindView() {
            if(isLoading == true){
                progressBar.visibility = View.VISIBLE
            }else{
                progressBar.visibility = View.GONE
            }
        }


    }


    interface OnClickProfileListener{
        fun OnClickProfile(member: Member, tv: TextView, iv: ImageView)
    }
    interface OnEndlessScrollListener{
        fun OnEndless(boolean_value: Boolean)
    }


}
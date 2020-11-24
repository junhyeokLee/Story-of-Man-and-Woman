package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.adapter_subscribers.view.*

class SubscribersAdapter(
    private val member: List<Member>,
    private val context: Context,
    private val memberViewModel: MemberViewModel,
    private val onClickProfileListener: OnClickProfileListener
)
    : RecyclerView.Adapter<SubscribersAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String

        fun bindView(item: Member, memberViewModel: MemberViewModel,onClickProfileListener: OnClickProfileListener) {
            // my_m_seq 가져오기
            val preferences: SharedPreferences = itemView.context.getSharedPreferences(
                "m_seq",
                Context.MODE_PRIVATE
            )
            my_m_seq = preferences.getString("inputMseq", "")

            Log.e("어답터실행됨", "실행")
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
                memberViewModel.memberSubscribeChecked(
                    item.m_seq!!,
                    my_m_seq,
                    "checked",
                    this,
                    context
                );
            }

            itemView.check_follow.setOnClickListener {
                if (it.check_follow.isChecked == true) {
                    it.check_follow.setTextColor(itemView.resources.getColor(R.color.white))
                    it.check_follow.text = "구독취소"
//                    followerCount.setText("1")
                    memberViewModel.memberSubscribe2(item.m_seq!!, my_m_seq, "true")


                } else {
                    it.check_follow.setTextColor(itemView.resources.getColor(R.color.black))
                    it.check_follow.text = "구독하기"
//                    followerCount.setText("0")
                    memberViewModel.memberSubscribe2(item.m_seq!!, my_m_seq, "false")

                }
            }

            val onClickProfile = onClickProfileListener

            itemView.layout_item.setOnClickListener {
                onClickProfile.OnClickProfile(item, itemView.tv_profile_nick, itemView.iv_profile)

            }

//            itemView.tag_name.text = "  # "+item.tag_name+"   "
//
//            itemView.tag_name.setOnClickListener {
//                val intent = Intent(itemView.context, FeedSearchActivity::class.java)
//                intent.putExtra("tag_seq",item.tag_seq.toString())
//                intent.putExtra("tag_name",item.tag_name)
//                itemView.context.startActivity(intent)
//
//            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_subscribers, parent, false)
        Log.e("어답터실행됨", "실행")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = member[position]
        var viewModel = memberViewModel
        Log.e("어답터실행됨", "실행")
        holder.bindView(item, viewModel,onClickProfileListener)


    }
    interface OnClickProfileListener{
        fun OnClickProfile(member: Member, tv: TextView, iv: ImageView)
    }
    override fun getItemCount(): Int {
        Log.e("어답터실행됨", "실행")

        return member.size   // 7개 까지만
    }



}